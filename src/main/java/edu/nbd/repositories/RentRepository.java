package edu.nbd.repositories;

import edu.nbd.model.Client;
import edu.nbd.model.Rent;
import edu.nbd.model.Vehicle;
import jakarta.persistence.*;

import java.util.List;

public class RentRepository implements Repository<Rent> {
    private final EntityManagerFactory emf;

    public RentRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public Rent findById(Object id) {
        try (EntityManager em = getEntityManager()) {
            // Return the rent with the given id or null if it does not exist
            return em.find(Rent.class, id);
        }
    }

    @Override
    public List<Rent> findAll() {
        try (EntityManager em = getEntityManager()) {
            TypedQuery<Rent> query = em.createQuery("SELECT r FROM Rent r", Rent.class);
            return query.getResultList();
        }
    }

    @Override
    public Rent add(Rent rent) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        Client client = rent.getClient();
        if (client.getMaxVehicles() <= countActiveRentsByClient(client)) {
            throw new IllegalArgumentException("Client already has the maximum number of active rents.");
        }
        if (isVehicleRented(rent.getVehicle())) {
            throw new IllegalArgumentException("Vehicle is already rented.");
        }

        try {
            transaction.begin();
            em.persist(rent);
            transaction.commit();
            return rent;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public Rent update(Rent rent) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(rent);
            transaction.commit();
            return rent;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public Rent delete(Rent rent) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            // Merge the rent to the managed one before removing it
            Rent managedRent = em.merge(rent);
            em.remove(managedRent);
            transaction.commit();
            return rent;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return null;
        } finally {
            em.close();
        }
    }

    private long countActiveRentsByClient(Client client) {
        try (EntityManager em = getEntityManager()) {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(r) FROM Rent r WHERE r.client = :client AND r.endTime IS NULL", Long.class);
            query.setParameter("client", client);
            return query.getSingleResult();
        }
    }

    private boolean isVehicleRented(Vehicle vehicle) {
        try (EntityManager em = getEntityManager()) {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(r) FROM Rent r WHERE r.vehicle = :vehicle AND r.endTime IS NULL", Long.class);
            query.setParameter("vehicle", vehicle);
            return query.getSingleResult() > 0;
        }
    }
}
