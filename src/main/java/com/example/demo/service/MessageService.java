package com.example.demo.service;

import com.example.demo.domain.Message;
import com.example.demo.domain.Utilizator;
import com.example.demo.repository.RepoDB.MessageRepoDB;
import com.example.demo.repository.RepoDB.UserRepoDB;
import com.example.demo.utils.events.UserTaskChangeEvent;
import com.example.demo.utils.observer.Observable;
import com.example.demo.utils.observer.Observer;

import java.util.*;
import java.util.stream.Collectors;

public class MessageService implements Service<Long, Message>, Observable<UserTaskChangeEvent>
{
    MessageRepoDB repoMessage;
    UserRepoDB repoUtiliz;

    private List<Observer<UserTaskChangeEvent>> observers= new ArrayList<>();

    public MessageService(MessageRepoDB repoMessage, UserRepoDB repoUtiliz) {
        this.repoMessage = repoMessage;
        this.repoUtiliz = repoUtiliz;
    }

    public ArrayList<Message> getMessagesBetweenTwoUsers(Long userId1, Long userId2)
    {
        if(repoUtiliz.findOne(userId1).isEmpty() || repoUtiliz.findOne(userId2).isEmpty())
            return new ArrayList<>();

        Collection<Message> messagesList = (Collection<Message>) repoMessage.findAll();
        return messagesList.stream()
                .filter(x->(x.getFrom().getId().equals(userId1) && x.getTo().get(0).getId().equals(userId2))
                || (x.getFrom().getId().equals(userId2) && x.getTo().get(0).getId().equals(userId1)))
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean addMessage(Long fromUserId, Long toUserId, String message)
    {
        try
        {
            Utilizator from = repoUtiliz.findOne(fromUserId).orElse(null);
            Utilizator to = repoUtiliz.findOne(toUserId).orElse(null);

            if(from == null || to == null)
                throw new Exception("Utilizatorul nu exista!");
            if(Objects.equals(message,""))
                throw new Exception("Messagerul este gol");

            Message msg = new Message(from,Collections.singletonList(to),message);
            repoMessage.save(msg);

            List<Message> messageTwoUsers = getMessagesBetweenTwoUsers(toUserId,fromUserId);
            if(messageTwoUsers.size() > 1) //deci daca au mai conversat ceva pana acum
            {
                //AICI OARECUM ACTUALIZAM CARE E ULTIMUL MESAJ CONSTANT

                Message secondToLastMessage = messageTwoUsers.get(messageTwoUsers.size()-2);
                Message lastMessage = messageTwoUsers.get(messageTwoUsers.size()-1);
                lastMessage.setReply(secondToLastMessage);
                repoMessage.update(lastMessage);
            }

            return true;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<Message> add(Message entity) {

        if(addMessage(entity.getFrom().getId(),entity.getTo().get(0).getId(),entity.getMessage()))
            return Optional.of(entity);
        else
            return Optional.empty();
    }

    @Override
    public Optional<Message> delete(Long aLong) {
        return repoMessage.delete(aLong);
    }

    @Override
    public Optional<Message> getEntityById(Long aLong) {
        return repoMessage.findOne(aLong);
    }

    @Override
    public Iterable<Message> getAll() {
        return repoMessage.findAll();
    }

    @Override
    public void addObserver(Observer<UserTaskChangeEvent> e)
    {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<UserTaskChangeEvent> e)
    {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UserTaskChangeEvent t)
    {
        observers.forEach(x->x.update(t));
    }
}
