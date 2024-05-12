package com.example.demo.repository.RepoDB;

//import com.example.demo.Main;
import com.example.demo.domain.Tuple;
import com.example.demo.domain.Prietenie;
import com.example.demo.domain.Utilizator;
import com.example.demo.domain.validators.PrietenieValidator;
import com.example.demo.domain.validators.Validator;
import com.example.demo.repository.Repository;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class PrietenieRepoDB implements Repository<com.example.demo.domain.Tuple<Long,Long>, Prietenie>{

    private String url;
    private String user;
    private String password;
    private Validator<Prietenie> validator;

    public PrietenieRepoDB(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        validator = new PrietenieValidator();
    }

    @Override
    public Optional<Prietenie> findOne(com.example.demo.domain.Tuple<Long, Long> longLongTuple) {

        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement = connection.prepareStatement("select * from prietenii " +
                    "where (id1 = ? and id2 = ?) or (id1 = ? and id2 = ?)");
        )
        {
            statement.setInt(1, Math.toIntExact(longLongTuple.getLeft()));
            statement.setInt(2,Math.toIntExact(longLongTuple.getRight()));
            statement.setInt(3,Math.toIntExact(longLongTuple.getRight()));
            statement.setInt(4,Math.toIntExact(longLongTuple.getLeft()));

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next())
            {
                Timestamp friends_from = resultSet.getTimestamp("friends_from");
                Prietenie prietenie = new Prietenie(friends_from.toLocalDateTime());
                prietenie.setId(new Tuple<>(resultSet.getLong("id1"), resultSet.getLong("id2")));
                return Optional.of(prietenie);
            }

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Prietenie> findAll() {

        Set<Prietenie> prietenii = new HashSet<>();
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement = connection.prepareStatement("select * from prietenii");
            ResultSet resultSet = statement.executeQuery()
        )
        {
            while(resultSet.next())
            {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");

                Timestamp friends_from = resultSet.getTimestamp("friends_from");
                Prietenie prietenie = new Prietenie(friends_from.toLocalDateTime());
                prietenie.setId(new Tuple<>(id1,id2));

                prietenii.add(prietenie);
            }
            return prietenii;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Prietenie> save(Prietenie entity) {

        if(entity.getId() == null)
            throw new IllegalArgumentException("Eroare! ID-urile nu pot fi null.");
        validator.validate(entity);

        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement = connection.prepareStatement("insert into prietenii(id1,id2,friends_from)" +
                    "values(?,?,?)");
        )
        {
            statement.setInt(1,entity.getId().getLeft().intValue());
            statement.setInt(2,entity.getId().getRight().intValue());
            statement.setTimestamp(3,Timestamp.valueOf(entity.getDate()));
            int affectedRows = statement.executeUpdate();
            return affectedRows == 0 ? Optional.ofNullable(entity) : Optional.empty();
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Prietenie> delete(com.example.demo.domain.Tuple<Long, Long> longLongTuple) {

        if (longLongTuple == null)
            throw new IllegalArgumentException("Eroare! ID-ul nu ppoate sa fie null.");

        try(Connection connection = DriverManager.getConnection(url,user,password);
        PreparedStatement statement = connection.prepareStatement("DELETE FROM prietenii " +
               "where (id1 = ? AND id2 = ?) OR (id1=? AND id2=?)" );
        ){

            Optional<Prietenie> prietenie = findOne(longLongTuple);
            statement.setInt(1,longLongTuple.getLeft().intValue());
            statement.setInt(2,longLongTuple.getRight().intValue());
            statement.setInt(3,longLongTuple.getRight().intValue());
            statement.setInt(4,longLongTuple.getLeft().intValue());

            int affectedRows = statement.executeUpdate();
            return affectedRows == 0? Optional.empty() : Optional.ofNullable(prietenie.get());
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Prietenie> update(Prietenie entity) {
        if(entity.getId()==null)
            throw new IllegalArgumentException("Eroare! Id-ul nu poate fi null.");

        validator.validate(entity);

        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement = connection.prepareStatement("update prietenii set friends_from = ? " +
                    "where (id1 = ? and id2 = ? ) or (id1 = ? and id2 = ?)");
        )
        {
            statement.setTimestamp(1,Timestamp.valueOf(entity.getDate()));
            statement.setInt(2,entity.getId().getLeft().intValue());
            statement.setInt(3,entity.getId().getRight().intValue());
            statement.setInt(4,entity.getId().getRight().intValue());
            statement.setInt(5,entity.getId().getLeft().intValue());
            int affectedRows = statement.executeUpdate();

            return affectedRows == 0 ? Optional.ofNullable(entity) : Optional.empty();
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }

    }
}
