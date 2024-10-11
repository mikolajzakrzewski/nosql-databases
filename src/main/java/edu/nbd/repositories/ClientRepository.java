package edu.nbd.repositories;

import edu.nbd.model.Client;
import edu.nbd.model.ClientType;
import jakarta.persistence.*;

import java.util.List;

public class ClientRepository implements Repository<Client> {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("edu.nbd.carRental");

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public Client findById(Object id) {
        try (EntityManager em = getEntityManager()) {
            // Return the client with the given id or null if it does not exist
            return em.find(Client.class, id);
        }
    }

    @Override
    public List<Client> findAll() {
        try (EntityManager em = getEntityManager()) {
            TypedQuery<Client> query = em.createQuery("SELECT c FROM Client c", Client.class);
            return query.getResultList();
        }
    }

    @Override
    public Client add(Client client) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            // Retrieve the client type from the database if it exists, otherwise create a new one
            Query query = em.createNativeQuery("SELECT * FROM client_types WHERE client_type = ?1", ClientType.class);
            query.setParameter(1, client.getTypeInfo());
            ClientType clientType;
            try {
                clientType = (ClientType) query.getSingleResult();
            } catch (NoResultException e) {
                clientType = client.getClientType();
                em.persist(clientType);
            }
            client.setClientType(clientType);
            em.persist(client);
            transaction.commit();
            return client;
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
    public Client update(Client client) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(client);
            transaction.commit();
            return client;
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
    public Client delete(Client client) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            // Merge the client to the managed one before removing it
            Client managedClient = em.merge(client);
            em.remove(managedClient);
            transaction.commit();
            return client;
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
