package edu.nbd.repositories;

import java.util.List;

public interface Repository<T> {
    T findById(Object id);

    List<T> findAll();

    T add(T obj);

    T update(T obj);

    T delete(T obj);
}