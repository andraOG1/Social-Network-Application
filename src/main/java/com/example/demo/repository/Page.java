package com.example.demo.repository;

public class Page<E> {


    private Iterable<E> elementePePagina;
    private int totalNumarElemente;

    public Page(Iterable<E> elementePePagina, int totalNumarElemente) {
        this.elementePePagina = elementePePagina;
        this.totalNumarElemente = totalNumarElemente;
    }

    public Iterable<E> getElementePePagina() {
        return elementePePagina;
    }

    public int getTotalNumarElemente() {
        return totalNumarElemente;
    }
}
