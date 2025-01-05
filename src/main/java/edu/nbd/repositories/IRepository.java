package edu.nbd.repositories;

public interface IRepository<T> {

    T findById(Object id);

    void add(T obj);

    void update(T obj);

    void delete(T obj);
}
