package com.example.demo.service;

import com.example.demo.domain.Prietenie;
import com.example.demo.domain.RelatiePrietenie;
import com.example.demo.domain.Tuple;
import com.example.demo.repository.RepoDB.FriendRequestRepoDB;
import com.example.demo.repository.RepoDB.PrietenieRepoDB;
import com.example.demo.repository.RepoDB.UserRepoDB;
import com.example.demo.utils.events.UserTaskChangeEvent;
import com.example.demo.utils.observer.Observable;
import com.example.demo.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendRequestService implements Service<Tuple<Long,Long>, RelatiePrietenie>, Observable<UserTaskChangeEvent> {

    private FriendRequestRepoDB repoFriendRequest;
    private UserRepoDB repoUser;
    private PrietenieRepoDB repoPriet;

    private List<Observer<UserTaskChangeEvent>> observers = new ArrayList<>();

    public FriendRequestService(FriendRequestRepoDB repoFriendRequest, UserRepoDB repoUser, PrietenieRepoDB repoPriet) {
        this.repoFriendRequest = repoFriendRequest;
        this.repoUser = repoUser;
        this.repoPriet = repoPriet;
    }

    @Override
    public Optional<RelatiePrietenie> add(RelatiePrietenie entity) {
        Optional<Prietenie> prietenie = repoPriet.findOne(entity.getId());

        if(prietenie.isPresent())
        {
            throw new IllegalArgumentException("Exista deja prietenia!");
        }
        return repoFriendRequest.save(entity);

    }

    @Override
    public Optional<RelatiePrietenie> delete(Tuple<Long, Long> longLongTuple) {
        return Optional.empty();
    }

    @Override
    public Optional<RelatiePrietenie> getEntityById(Tuple<Long, Long> longLongTuple) {
        return repoFriendRequest.findOne(longLongTuple);
    }

    @Override
    public Iterable<RelatiePrietenie> getAll() {
        return null;
    }

    public Optional<RelatiePrietenie> update(RelatiePrietenie entity)
    {
        return repoFriendRequest.update(entity);
    }

    //aici e lista de oameni care dau request prietenie la un utilizator anume
    public List<Long> getFriendRequestIds(Long id)
    {
        return repoUser.getFriendsIdsForFriendRequest(id);
    }

    ///////////////////////////
    //OBSERVER

    @Override
    public void addObserver(Observer<UserTaskChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<UserTaskChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UserTaskChangeEvent t) {
            observers.forEach(x->x.update(t));
    }
}
