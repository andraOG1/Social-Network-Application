package com.example.demo.repository.RepoDB;

import com.example.demo.domain.Prietenie;
import com.example.demo.domain.RelatiePrietenie;
import com.example.demo.domain.StatusFriendRequest;
import com.example.demo.domain.Tuple;
import com.example.demo.domain.validators.FriendRequestValidator;
import com.example.demo.domain.validators.Validator;
import com.example.demo.repository.Repository;

import java.sql.*;
import java.util.Optional;

public class FriendRequestRepoDB implements Repository<Tuple<Long,Long>, RelatiePrietenie> {

    private String url;
    private String user;
    private String password;

    private Validator<RelatiePrietenie> validator;

    public FriendRequestRepoDB(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        validator = new FriendRequestValidator();
    }

    @Override
    public Optional<RelatiePrietenie> findOne(Tuple<Long, Long> longLongTuple) {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement = connection.prepareStatement("select * from friend_request " +
                    "where (id1 = ? and id2 = ?)");
            //where (id1 = ? and id2 = ?) or (id1 = ? and id2 = ?)
            //nu avem asa, pt ca daca id1 face request la id2, nu inseamna ca e si invers
        )
        {
            statement.setInt(1, Math.toIntExact(longLongTuple.getLeft()));
            statement.setInt(2, Math.toIntExact(longLongTuple.getRight()));

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next())
            {
                String status = resultSet.getString("status");
                StatusFriendRequest statusFriendRequest;
                if(status.equals("PENDING"))
                    statusFriendRequest = StatusFriendRequest.PENDING;
                else if(status.equals("APPROVED"))
                    statusFriendRequest = StatusFriendRequest.APPROVED;
                else statusFriendRequest = StatusFriendRequest.REJECTED;

                //acum din domain ii spunem de fapt care e concluzia, gen ca a acceptat sau nu
                RelatiePrietenie relatiePrietenie = new RelatiePrietenie(statusFriendRequest);
                relatiePrietenie.setId(new Tuple<Long,Long>(resultSet.getLong("id1"), resultSet.getLong("id2")));
                return Optional.of(relatiePrietenie);
            }

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<RelatiePrietenie> findAll() {
        return null;
    }

    @Override
    public Optional<RelatiePrietenie> save(RelatiePrietenie entity) {
        if(entity.getId() == null)
            throw new IllegalArgumentException("Eroare! ID-urile nu pot fi null.");
        validator.validate(entity);

        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement = connection.prepareStatement("insert into friend_request(id1,id2,status)" +
                    "values(?,?,?)");
        )
        {
            statement.setInt(1,entity.getId().getLeft().intValue());
            statement.setInt(2,entity.getId().getRight().intValue());
            statement.setString(3,entity.getStatus().toString());
            int affectedRows = statement.executeUpdate();
            return affectedRows == 0 ? Optional.of(entity) : Optional.empty();
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<RelatiePrietenie> delete(Tuple<Long, Long> longLongTuple) {
        //aici nu avem delete, pt ca nu se poate sterge o prietenie, ci doar sa se dea REJECT
        return Optional.empty();
    }

    @Override
    public Optional<RelatiePrietenie> update(RelatiePrietenie entity) {
        if(entity.getId()==null)
            throw new IllegalArgumentException("Eroare! Id-urilel nu pot fi null.");

        validator.validate(entity);

        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement = connection.prepareStatement("update friend_request set status = ? " +
                    "where (id1 = ? and id2 = ? )");
        )
        {
            statement.setString(1,entity.getStatus().toString());
            statement.setInt(2,entity.getId().getLeft().intValue());
            statement.setInt(3,entity.getId().getRight().intValue());
            int affectedRows = statement.executeUpdate();

            return affectedRows == 0 ? Optional.ofNullable(entity) : Optional.empty();
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
