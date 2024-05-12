package com.example.demo.service;

import com.example.demo.domain.Utilizator;
import com.example.demo.repository.Page;
import com.example.demo.repository.Pageable;
import com.example.demo.repository.PagingRepository;
import com.example.demo.repository.RepoDB.UserRepoDB;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.*;

import com.example.demo.domain.Prietenie;
import com.example.demo.utils.events.UserTaskChangeEvent;
import com.example.demo.utils.observer.Observable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


import com.example.demo.utils.events.UserTaskChangeEvent;
import com.example.demo.utils.observer.Observer;
import java.security.MessageDigest;


public class UtilizatorService implements Service<Long, Utilizator>, Observable<UserTaskChangeEvent> {

    //////////////////////////////////
    //REPO PAGINAT
//    private PagingRepository<Long,Utilizator> repoUtilizPaginat;

//    public UtilizatorService(PagingRepository<Long,Utilizator> repoUtilizPaginat)
//    {
//        this.repoUtilizPaginat = repoUtilizPaginat;
//    }



    ///////////////////////////////////
    public String encrypt(String pass){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(pass.getBytes());
        byte [] digest = md.digest();
        StringBuffer hexString = new StringBuffer();
        for (int i=0;i<digest.length;i++){
            hexString.append(Integer.toHexString(0xFF & digest[i]));
        }
        return hexString.toString();
    }


    public void tryLogin(String id, String pass){
        Optional<Utilizator> u = getEntityById(Long.parseLong(id));
        if(u.isPresent()){
            if(u.get().getPassword().equals(encrypt(pass)))
            {

            }
            else{
                throw new RuntimeException("Parola incorecta!");
            }
        }
        else{
            throw new RuntimeException("Nu existad niciun user cu acest id!");
            }
        }
    ////////////////////////////////////


    private List<Observer<UserTaskChangeEvent>> observers = new ArrayList<>();
    private UserRepoDB repo;

    public Page<Utilizator> findAllPAGINAT(Pageable pageable)
    {
        return repo.findAllPAGINAT(pageable);
    }

    public UtilizatorService(UserRepoDB repo) {
                this.repo = repo;
            }

    @Override
    public Optional<Utilizator> add(Utilizator entity) {
        return repo.save(entity);
    }

    public Optional<Utilizator> cautareSerivce(String n, String p)
    {
        return repo.cautare(n,p);
    }

    public Optional<Utilizator> update(Utilizator entity)
    {
        return  repo.update(entity);
    }

    public List<Long> get_friends(Long id)
    {
        return repo.getFriendsIds(id);
    }

    @Override
    public Optional<Utilizator> delete(Long aLong) {
        return repo.delete(aLong);
    }

    @Override
    public Optional<Utilizator> getEntityById(Long aLong) {
        return repo.findOne(aLong);
    }

    @Override
    public Iterable<Utilizator> getAll() {
        return repo.findAll();
    }

    //PENTRU OBSERVER
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

    /////////////////////////////////////////////////////////////
    //codul meu vechi


    Integer lgDrumMax=0;
    public int CateComponenteConexe(Iterable<Prietenie> lista)
    {
        HashMap<Long, List<Long>> listaPrieteniiUtiliz = new HashMap<>();

        HashMap<Long,Boolean> lista_dfs = new HashMap<>();

        ArrayList<Long> lista_utilizatori = new ArrayList<>();

        lista.forEach(prietenie -> {
            // am creat o lista
            listaPrieteniiUtiliz.computeIfAbsent(prietenie.getId().getLeft(), k -> new ArrayList<>());
            if (!listaPrieteniiUtiliz.get(prietenie.getId().getLeft()).contains(prietenie.getId().getRight())) {
                listaPrieteniiUtiliz.get(prietenie.getId().getLeft()).add(prietenie.getId().getRight());
            }

            lista_utilizatori.add(prietenie.getId().getLeft());
            lista_utilizatori.add(prietenie.getId().getRight());

            listaPrieteniiUtiliz.computeIfAbsent(prietenie.getId().getRight(), k -> new ArrayList<>());
            if (!listaPrieteniiUtiliz.get(prietenie.getId().getRight()).contains(prietenie.getId().getLeft())) {
                listaPrieteniiUtiliz.get(prietenie.getId().getRight()).add(prietenie.getId().getLeft());
            }
        });

        int[] nr_comp = {0};
        lista_utilizatori.forEach(ID -> {
            if (!lista_dfs.containsKey(ID)) {
                nr_comp[0]++;
                dfs_rec(lista_dfs, ID, lista_utilizatori, listaPrieteniiUtiliz);
            }
        });

        return nr_comp[0];
    }


    public int LungCompMax(Iterable<Prietenie> lista)
    {
        HashMap<Long, List<Long>> listaPrieteniiUtiliz = new HashMap<>();

        HashMap<Long,Boolean> lista_dfs = new HashMap<>();

        ArrayList<Long> lista_utilizatori = new ArrayList<>();

        lista.forEach(prietenie -> {
            // Create a list
            listaPrieteniiUtiliz.computeIfAbsent(prietenie.getId().getLeft(), k -> new ArrayList<>());
            if (!listaPrieteniiUtiliz.get(prietenie.getId().getLeft()).contains(prietenie.getId().getRight())) {
                listaPrieteniiUtiliz.get(prietenie.getId().getLeft()).add(prietenie.getId().getRight());
            }

            lista_utilizatori.add(prietenie.getId().getLeft());
            lista_utilizatori.add(prietenie.getId().getRight());

            listaPrieteniiUtiliz.computeIfAbsent(prietenie.getId().getRight(), k -> new ArrayList<>());
            if (!listaPrieteniiUtiliz.get(prietenie.getId().getRight()).contains(prietenie.getId().getLeft())) {
                listaPrieteniiUtiliz.get(prietenie.getId().getRight()).add(prietenie.getId().getLeft());
            }
        });

        AtomicInteger lgDrumMax = new AtomicInteger(0);

        lista_utilizatori.forEach(ID -> {
            if (!lista_dfs.containsKey(ID)) {
                AtomicInteger currentLength = new AtomicInteger(0);
                dfs(lista_dfs, ID, 0, currentLength, lista_utilizatori, listaPrieteniiUtiliz);
                lgDrumMax.set(Math.max(lgDrumMax.get(), currentLength.get()));
            }
        });

        return lgDrumMax.get();
    }


    private void dfs_rec(HashMap<Long,Boolean> listaa, Long id, ArrayList<Long>lista_utilizatori,HashMap<Long, List<Long>> lista_prieteniilor )
    {

        listaa.put(id, true); // id ul vizitat
        lista_prieteniilor.get(id).forEach(i -> {
            if (!listaa.containsKey(i)) {
                dfs_rec(listaa, i, lista_utilizatori, lista_prieteniilor);
            }
        });
    }

    private void dfs(HashMap<Long, Boolean> listaa, Long id, int lgDrumCurent, AtomicInteger lgDrumMax,
                     ArrayList<Long> lista_utilizatori, HashMap<Long, List<Long>> lista_prieteniilor) {
        if (lgDrumCurent > lgDrumMax.get()) {
            lgDrumMax.set(lgDrumCurent);
        }

        listaa.put(id, true); // id ul vizitat
        lista_prieteniilor.get(id).forEach(i -> {
            if (!listaa.containsKey(i)) {
                dfs(listaa, i, lgDrumCurent + 1, lgDrumMax, lista_utilizatori, lista_prieteniilor);
                listaa.remove(i);
            }
        });
    }
}



