package com.example.demo.domain;

import java.util.*;

public class Utilizator extends Entity<Long> {
    private String firstName;
    private String lastName;
    private ArrayList<Utilizator> friends;

    private String password;

    public Utilizator(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        friends = new ArrayList<>();
    }

    public Utilizator(String firstName, String lastName,String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        friends = new ArrayList<>();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ArrayList<Utilizator> getFriends() {
        return friends;
    }

    public void addFriend(Utilizator e) throws IllegalArgumentException{
        if(friends.contains(e))
            throw new IllegalArgumentException("Eroare! Utilizatorii sunt deja prieteni!");
        friends.add(e);
    }

    public void removeFriend(Utilizator e) throws IllegalArgumentException{
        if(!friends.contains(e))
            throw new IllegalArgumentException("Eroare! Utilizatorii nu sunt prieteni!");
        friends.remove(e);
    }

    @Override
    public String toString() {
        return "Utilizator{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName());
    }
}