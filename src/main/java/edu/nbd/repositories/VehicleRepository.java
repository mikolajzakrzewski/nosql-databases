package edu.nbd.repositories;

import edu.nbd.model.Vehicle;
import jakarta.persistence.*;

import java.util.List;

public class VehicleRepository implements Repository<Vehicle> {
    private final EntityManagerFactory emf;

    public VehicleRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public Vehicle findById(Object id) {
        try (EntityManager em = getEntityManager()) {
            // Return the vehicle with the given id or null if it does not exist
            return em.find(Vehicle.class, id);
        }
    }

    @Override
    public List<Vehicle> findAll() {
        try (EntityManager em = getEntityManager()) {
            TypedQuery<Vehicle> query = em.createQuery("SELECT v FROM Vehicle v", Vehicle.class);
            return query.getResultList();
        }
    }

    @Override
    public Vehicle add(Vehicle vehicle) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(vehicle);
            transaction.commit();
            return vehicle;
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
    public Vehicle update(Vehicle vehicle) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(vehicle);
            transaction.commit();
            return vehicle;
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
    public Vehicle delete(Vehicle vehicle) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            // Merge the vehicle to the managed one before removing it
            Vehicle managedVehicle = em.merge(vehicle);
            em.remove(managedVehicle);
            transaction.commit();
            return vehicle;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return null;
        } finally {
            em.close();
        }
    }
}
