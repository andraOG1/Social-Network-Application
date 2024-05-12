package com.example.demo.repository.RepoDB;


//import com.jogamp.common.util.Bitfield;
import com.example.demo.domain.validators.UtilizatorValidator;
import com.example.demo.domain.validators.ValidationException;
import com.example.demo.domain.validators.Validator;
import com.example.demo.repository.Page;
import com.example.demo.repository.Pageable;
import com.example.demo.repository.PagingRepository;
import com.example.demo.repository.Repository;
import com.example.demo.domain.Utilizator;

import java.net.URI;

import java.sql.*;
import java.util.*;

//public class UserRepoDB implements Repository<Long, Utilizator>
public class UserRepoDB implements PagingRepository<Long, Utilizator> {

    private String url;
    private String user;
    private String password;
    private Validator<Utilizator> validator;

    public UserRepoDB(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        validator = new UtilizatorValidator();
    }


    //////////////////////////////////////////////////////////
    //TABEL PAGINAT (afisare paginata a elementelor)

    @Override
    public Page<Utilizator> findAllPAGINAT(Pageable pageable)
    {
        List<Utilizator> utilizatorList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url,user,password);
        PreparedStatement pagePreparedStatement = connection.prepareStatement("SELECT * FROM users " +
                "LIMIT ? OFFSET ?");

        PreparedStatement countPreparedStatement = connection.prepareStatement
                ("SELECT COUNT(*) AS count FROM users")
        )
        {
            pagePreparedStatement.setInt(1,pageable.getPageSize());
            pagePreparedStatement.setInt(2,pageable.getPageSize() * pageable.getPageNumber());

            try(ResultSet pageResultSet = pagePreparedStatement.executeQuery();
            ResultSet countResultSet = countPreparedStatement.executeQuery();
            )
            {
                while(pageResultSet.next())
                {
                    Long id = pageResultSet.getLong("id");
                    String prenume = pageResultSet.getString("first_name");
                    String nume = pageResultSet.getString("last_name");
                    String parola = pageResultSet.getString("password");
                    Utilizator utilizator = new Utilizator(prenume,nume,parola);
                    utilizator.setId(id);
                    utilizatorList.add(utilizator);
                }
                int totalCount = 0;
                if(countResultSet.next())
                {
                    totalCount = countResultSet.getInt("count");
                }

                return new Page<>(utilizatorList,totalCount);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    ///////////////////////////////////////////////////////////

    @Override
    public Optional<Utilizator> findOne(Long aLong)
    {
        if(aLong == null)
            throw  new IllegalArgumentException("Eroare! Id ul nu poate fi nul");

        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement = connection.prepareStatement("select * from users " +
                    "where id = ? "  );
        ){

            statement.setInt(1,Math.toIntExact(aLong));
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next())
            {
                String prenume = resultSet.getString("first_name");
                String nume = resultSet.getString("last_name");
                String parola = resultSet.getString("password");
                Utilizator user = new Utilizator(prenume,nume,parola);
                user.setId(aLong);
                return Optional.ofNullable(user);
            }
        }

        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    public Optional<Utilizator> cautare(String nume, String prenume)
    {
        if(nume == "" || prenume == "")
            throw  new IllegalArgumentException("Numele sau Prenumele nu pot fi nule.");

        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement = connection.prepareStatement("select * from users " +
                    "where first_name = ? AND last_name = ? "  );
            ){


            statement.setString(1,prenume);
            statement.setString(2,nume);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next())
            {
                Long userId = resultSet.getLong("id");
                String prn = resultSet.getString("first_name");
                String numm = resultSet.getString("last_name");
                String parolaa = resultSet.getString("password");
                Utilizator user = new Utilizator(prn,numm,parolaa);
                user.setId(userId);
                return Optional.ofNullable(user);
            }
        }

        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Utilizator> findAll() {

        Set<Utilizator> users = new HashSet<>();
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement = connection.prepareStatement("select * from users");
            ResultSet resultSet = statement.executeQuery()
        ){
            while(resultSet.next())
            {
                Long id = resultSet.getLong("id");
                String prenume = resultSet.getString("first_name");
                String nume = resultSet.getString("last_name");
                String parolaa = resultSet.getString("password");
                Utilizator user = new Utilizator(prenume,nume,parolaa);
                user.setId(id);
                users.add(user);
            }
            return users;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) {

        if(entity.getId()==null)
            throw new IllegalArgumentException("Eroare! Id-ul nu poate fi null.");

        validator.validate(entity);

        //exista deja utilizatorul pe care vrem sa l adaugam
        if(findOne(entity.getId()).isPresent())
            return Optional.of(entity);

        //daca nu, incersam sa il adaugam
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement = connection.prepareStatement("insert into users(id,first_name,last_name,password) " +
                    "values(?,?,?,?)");
        )
        {
            statement.setInt(1,entity.getId().intValue());
            statement.setString(2,entity.getFirstName());
            statement.setString(3, entity.getLastName());
            statement.setString(4, entity.getPassword());

            //dupa ce inseram, vrem sa facem si un update
            int affectedRows = statement.executeUpdate();

            //Optional.ofNullable creates an Optional containing the specified entity if it's not null. So, it returns an
            //Optional containing the entity if affectedRows is 0.
            //If affectedRows is not 0, it means the insertion was successful and affected some rows.
            //In this case, it returns Optional.empty(), which represents an empty Optional because the
            //insertion was successful, and there's no need to return the entity again.
            return affectedRows == 0 ? Optional.ofNullable(entity) : Optional.empty();
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Utilizator> update(Utilizator entity) {

        if(entity.getId()==null)
            throw new IllegalArgumentException("Eroare! Id-ul nu poate fi null.");

        validator.validate(entity);

        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement = connection.prepareStatement("update users set first_name = ?, last_name = ? where id = ?");
        )
        {
            statement.setString(1,entity.getFirstName());
            statement.setString(2,entity.getLastName());
            statement.setInt(3,entity.getId().intValue());
            int affectedRows = statement.executeUpdate();

            return affectedRows == 0 ? Optional.empty() : Optional.ofNullable(entity);
        }

        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> delete(Long aLong) {

        if(aLong == null)
            throw new IllegalArgumentException("Eroare! ID-ul nu poate sa fie null.");

        Optional<Utilizator> entity = findOne(aLong);

        try(Connection connection = DriverManager.getConnection(url,user,password);
        PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id=?");
        )
        {
            statement.setInt(1,aLong.intValue());
            int affectedRows = statement.executeUpdate();

            //daca a fost afectat tabelu, deci NU E 0, practic afisam ce am sters
            return affectedRows == 0? Optional.empty() : Optional.ofNullable(entity.get());
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }


    public List<Long> getFriendsIds(Long idPrincipal)
    {
        List<Long> idList = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement = connection.prepareStatement(
                    "select users.id, prietenii.id1, prietenii.id2 " +
                            " from users " +
                            " INNER JOIN prietenii ON (users.id = prietenii.id1 or users.id = prietenii.id2) " +
                            "where users.id = ?" );
        )
        {
            statement.setInt(1,idPrincipal.intValue());
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");

                if(idPrincipal != id1)
                    idList.add(id1); //adica adaugam in dreapta id ul nou al prietenului
                else
                    idList.add(id2); //altfel, inseamna ca in stanga nu e idPrincipal si pe prietenul ala il luam
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return idList;
    }

    public List<Long> getFriendsIdsForFriendRequest(Long id)
    {
        List<Long> idList = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(
                    "select users.id, friend_request.id1, friend_request.id2 " +
                            "from users " +
                            "INNER JOIN friend_request on users.id = friend_request.id2 " +
                            "where (users.id = ? and friend_request.status = 'PENDING')");
        ){
            statement.setInt(1,id.intValue());
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                Long idBun = resultSet.getLong("id1");
                idList.add(idBun);
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

        return idList;

    }

}
