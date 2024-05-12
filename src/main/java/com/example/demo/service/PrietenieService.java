package com.example.demo.service;

import com.example.demo.domain.Prietenie;
import com.example.demo.domain.Tuple;
import com.example.demo.domain.Utilizator;
import com.example.demo.repository.RepoDB.PrietenieRepoDB;
import com.example.demo.repository.RepoDB.UserRepoDB;
import com.example.demo.utils.events.UserTaskChangeEvent;
import com.example.demo.utils.observer.Observable;
import com.example.demo.utils.observer.Observer;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PrietenieService implements Service<Tuple<Long,Long>, Prietenie> , Observable<UserTaskChangeEvent> {

    PrietenieRepoDB repoPriet;
    UserRepoDB repoUtiliz;

    private List<Observer<UserTaskChangeEvent>> observers = new ArrayList<>();

    public PrietenieService(PrietenieRepoDB repoPriet, UserRepoDB repoUtiliz) {
        this.repoPriet = repoPriet;
        this.repoUtiliz = repoUtiliz;
    }


    @Override
    public Optional<Prietenie> add(Prietenie entity) {

        Long id1 = entity.getId().getLeft();
        Long id2 = entity.getId().getRight();

        //verific sa vad daca prietenia pe care vreau sa o leg,
        //adica niste id- uri, daca exista in lista de useri
        Optional<Utilizator> user1 = repoUtiliz.findOne(id1);
        if(user1.isEmpty())
            throw new IllegalArgumentException("Nu exista user cu id-ul "+ id1);

        Optional<Utilizator> user2 = repoUtiliz.findOne(id2);
        if(user2.isEmpty())
            throw new IllegalArgumentException("Nu exista user cu id-ul "+ id2);

        //altfel, daca exista, dam update tabelului de prietenie, adaugnd prietenia
        user1.get().addFriend(user2.get());
        user2.get().addFriend(user1.get());

        //acum dam update in lista utilizatorilor, in sensul ca
        //daca om1 si a facut un prieten pe om2, mai sus am adaugat prietenia
        //dar dam update listei utiliz, ca sa se stie ca la om1 i am adaugat un prieten
        repoUtiliz.update(user1.get());
        repoUtiliz.update(user2.get());
        return repoPriet.save(entity);
    }

    @Override
    public Optional<Prietenie> delete(Tuple<Long, Long> longLongTuple) {

        Long id1 = longLongTuple.getLeft();
        Long id2 = longLongTuple.getRight();

        Optional<Utilizator> user1 = repoUtiliz.findOne(id1);
        Optional<Utilizator> user2 = repoUtiliz.findOne(id2);

        if(user1.isEmpty() || user2.isEmpty())
            throw new IllegalArgumentException("User inexistent!");

        Optional<Prietenie> deSters = repoPriet.delete(longLongTuple);
        if(deSters.isPresent())
        {
            user1.get().removeFriend(user2.get());
            user2.get().removeFriend(user1.get());
        }
        return deSters;
    }

    @Override
    public Optional<Prietenie> getEntityById(Tuple<Long, Long> longLongTuple) {
        return repoPriet.findOne(longLongTuple);
    }

    @Override
    public Iterable<Prietenie> getAll() {
        return repoPriet.findAll();
    }

    //////////OBSEVER
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

    public List<String> friendsMadeInACertainMonth(Utilizator user, Integer luna)
    {
        List<Long> ids = repoUtiliz.getFriendsIds(user.getId());

        List<String>result = ids.stream()
                .map(x->repoPriet.findOne(new Tuple<Long,Long>(x, user.getId())).get())
                //transform din lista de Id-uri in lista de prietenii
                .map(x->{
                    Utilizator utilizator;
                    if(x.getId().getLeft() == user.getId())
                        utilizator = repoUtiliz.findOne(x.getId().getRight()).get();
                    else
                        utilizator = repoUtiliz.findOne(x.getId().getLeft()).get();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                    return utilizator.getLastName() + " | " + utilizator.getFirstName() + " | "
                            + x.getDate().format(formatter);
                })//transform din lista de prietenii in lista de stringuri dupa formatul cerut
                .filter(str -> //se filtreaza dupa o luna ceruta
                {
                    String[] parts = str.split("\\|");
                    //ia de la 3 - 5 (fara 5) deci 0 1 2 3 4 = d d - M M ,adica ia luna
                    String month = parts[2].trim().substring(3,5);
                    return Integer.parseInt(month) == luna;
                })
                .collect(Collectors.toList()); //se colecteaza toti userii din luna ceruta
        return result;

    }
}
