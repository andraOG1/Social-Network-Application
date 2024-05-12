package com.example.demo.repository.RepoDB;

import com.example.demo.domain.Message;
import com.example.demo.domain.Utilizator;
import com.example.demo.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

//                                    Message = domain clasa Mesaje
public class MessageRepoDB implements Repository<Long, Message> {

    private final String url;
    private final String user;
    private final String password;
    private Repository<Long, Utilizator> utilizRepo;

    public MessageRepoDB(String url, String user, String password, Repository<Long, Utilizator> utilizRepo) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.utilizRepo = utilizRepo;
    }

    public Optional<Message> findOneWithoutReply(Long longID)
    {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from message WHERE id=?"))
        {
            statement.setLong(1,longID);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next())
            {
                Long from_id = resultSet.getLong("from_id");
                Long to_id = resultSet.getLong("to_id");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                String message = resultSet.getString("message");
                Message msg = new Message(utilizRepo.findOne(from_id).get(), Collections.singletonList(utilizRepo.findOne(to_id).get()),message,date);
                msg.setId(longID);
                return Optional.of(msg);
            }
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Message> findOne(Long aLong) {

        Message msg;
        if(findOneWithoutReply(aLong).isPresent())
        {
            msg = findOneWithoutReply(aLong).get();
        }
        else return Optional.empty();

        try(Connection connection = DriverManager.getConnection(url,user,password);
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM message WHERE id=?"))
        {
            statement.setLong(1,aLong);
            ResultSet resultSet = statement.executeQuery();
            long reply_id = resultSet.getLong("reply_id");
            if(!resultSet.wasNull()) //adica daca se continua conversatia, ci nu acum incepe
            {
                Message reply;
                if(findOneWithoutReply(reply_id).isPresent())

                {
                    reply = findOneWithoutReply(reply_id).get();
                }
                else return Optional.empty();

                msg.setReply(findOneWithoutReply(reply_id).get());
                return Optional.of(msg);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Message> findAll() {

        List<Message> messagesList = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url,user,password);
        PreparedStatement statement = connection.prepareStatement("select * from message");
        ResultSet resultSet = statement.executeQuery())
        {
            while(resultSet.next())
            {
                Long id = resultSet.getLong("id");
                Long from_id = resultSet.getLong("from_id");
                Long to_id = resultSet.getLong("to_id");
                LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();
                String message = resultSet.getString("message");

                Message msg = new Message(utilizRepo.findOne(from_id).get(),Collections.singletonList(utilizRepo.findOne(to_id).get()),message,date);
                msg.setId(id);
                messagesList.add(msg);  //messages e lista de mesaje
            }
            return messagesList;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Message> save(Message entity) {

        try(Connection connection = DriverManager.getConnection(url,user,password);
        PreparedStatement statement = connection.prepareStatement("INSERT INTO message(from_id,to_id,data,message,reply_id) VALUES (?,?,?,?,?)"))
        {
            statement.setLong(1,entity.getFrom().getId());
            statement.setLong(2,entity.getTo().get(0).getId());
            statement.setTimestamp(3,Timestamp.valueOf(entity.getDate()));
            statement.setString(4, entity.getMessage());
            if(entity.getReply() != null)
                statement.setLong(5,entity.getReply().getId());
            else statement.setNull(5,java.sql.Types.NULL);

            int affectedRows = statement.executeUpdate();
            return affectedRows != 0 ? Optional.empty() : Optional.of(entity);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> delete(Long aLong) {

        try(Connection connection = DriverManager.getConnection(url,user,password);
        PreparedStatement statement = connection.prepareStatement("DELETE FROM message WHERE id=?");
        )
        {
            Optional<Message> cv = findOne(aLong);
            statement.setLong(1,aLong);
            int affectedRows = statement.executeUpdate();

            return affectedRows != 0 ? Optional.empty() : cv;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> update(Message entity) {

        try(Connection connection = DriverManager.getConnection(url,user,password);
        PreparedStatement statement = connection.prepareStatement("UPDATE message SET from_id = ?, to_id= ?, data = ?, message = ?, reply_id = ? WHERE id = ?"))
        {
            statement.setLong(1,entity.getFrom().getId());
            statement.setLong(2,entity.getTo().get(0).getId());
            statement.setTimestamp(3,Timestamp.valueOf(entity.getDate()));
            statement.setString(4, entity.getMessage());
            statement.setLong(5,entity.getReply().getId());
            statement.setLong(6,entity.getId());

            int affectedRows = statement.executeUpdate();
            return affectedRows != 0 ? Optional.empty() : Optional.of(entity);

        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}










