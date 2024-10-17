package edu.nbd.test.repositories;

import edu.nbd.model.Client;
import edu.nbd.model.Default;
import edu.nbd.model.Gold;
import edu.nbd.repositories.ClientRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.RollbackException;
import org.junit.jupiter.api.*;

import java.util.List;

public class ClientRepositoryTest {
    private static EntityManagerFactory emf;
    private static ClientRepository clientRepository;

    @BeforeAll
    static void setUp() {
        emf = jakarta.persistence.Persistence.createEntityManagerFactory("edu.nbd.carRental");
        clientRepository = new ClientRepository(emf);
    }

    @AfterEach
    void clearDatabase() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Client c").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @AfterAll
    static void tearDown() {
        emf.close();
    }

    @Test
    public void findById_ClientInDB_ClientReturned() {
        Client client = new Client("Firstname", "Lastname", "11111111110", new Default());
        clientRepository.add(client);
        Client foundClient;
        try (EntityManager em = emf.createEntityManager()) {
            foundClient = em.find(Client.class, "11111111110");
        }
        Assertions.assertEquals(clientRepository.findById("11111111110").getClientInfo(), foundClient.getClientInfo());
    }

    @Test
    public void findAll_TwoClientsInDB_TwoClientsListReturned() {
        Client client1 = new Client("Firstname", "Lastname", "11111111111", new Default());
        Client client2 = new Client("Firstname", "Lastname", "11111111112", new Default());
        clientRepository.add(client1);
        clientRepository.add(client2);
        List<Client> addedClients = List.of(client1, client2);
        List<Client> foundClients = clientRepository.findAll();
        Assertions.assertEquals(foundClients.size(), 2);
        boolean areClientsEqual = true;
        for (int i = 0; i < foundClients.size(); i++) {
            if (!foundClients.get(i).getClientInfo().equals(addedClients.get(i).getClientInfo())) {
                areClientsEqual = false;
                break;
            }
        }
        Assertions.assertTrue(areClientsEqual);
    }

    @Test
    public void add_ValidClient_ClientAdded() {
        Client client = new Client("Firstname", "Lastname", "11111111111", new Default());
        clientRepository.add(client);
        Assertions.assertEquals(clientRepository.findById("11111111111").getClientInfo(), client.getClientInfo());
    }

    @Test
    public void update_UpdatedClient_ClientUpdated() {
        Client client = new Client("Firstname", "Lastname", "11111111112", new Default());
        clientRepository.add(client);
        client.setFirstName("AltFirstname");
        clientRepository.update(client);
        Assertions.assertEquals(clientRepository.findById("11111111112").getFirstName(), "AltFirstname");
    }

    @Test
    public void update_SameClientTwiceSimultaneously_OptimisticLockExceptionThrown() {
        Client client = new Client("Firstname", "Lastname", "11111111110", new Default());
        clientRepository.add(client);
        Client client1;
        Client client2;
        boolean exceptionThrown = false;
        try (EntityManager em1 = emf.createEntityManager(); EntityManager em2 = emf.createEntityManager()) {
            client1 = em1.find(Client.class, "11111111110");
            client2 = em2.find(Client.class, "11111111110");
            em1.getTransaction().begin();
            em2.getTransaction().begin();
            client1.setFirstName("AltFirstname");
            client2.setFirstName("AltAltFirstname");
            em1.merge(client1);
            em2.merge(client2);
            em1.getTransaction().commit();
            em2.getTransaction().commit();
        } catch (Exception e) {
            exceptionThrown = true;
            Assertions.assertInstanceOf(RollbackException.class, e);
            Assertions.assertInstanceOf(OptimisticLockException.class, e.getCause());
        }
        Assertions.assertTrue(exceptionThrown);
    }

    @Test
    public void delete_ClientInDB_ClientRemoved() {
        Client client = new Client("Firstname", "Lastname", "11111111113", new Gold());
        clientRepository.add(client);
        Assertions.assertNotNull(clientRepository.findById("11111111113"));
        clientRepository.delete(client);
        Assertions.assertNull(clientRepository.findById("11111111113"));
    }
}
