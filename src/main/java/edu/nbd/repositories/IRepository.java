package edu.nbd.repositories;

import java.util.ArrayList;

public interface IRepository<T> {

    T findById(Object id);

    ArrayList<T> findAll();

    void add(T obj);

    void update(T obj);

    void delete(T obj);
}
