package edu.nbd.test.repositories;

import edu.nbd.model.Client;
import edu.nbd.model.Default;
import edu.nbd.model.Gold;
import edu.nbd.repositories.ClientRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

public class ClientRepositoryTest {
    private static EntityManagerFactory emf;
    private static ClientRepository clientRepository;

    @BeforeAll
    static void setUp() {
        emf = jakarta.persistence.Persistence.createEntityManagerFactory("edu.nbd.carRentalTest");
        clientRepository = new ClientRepository(emf);
    }

    @BeforeEach
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
        Assertions.assertEquals(clientRepository.findAll().size(), 2);
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
    public void delete_ClientInDB_ClientRemoved() {
        Client client = new Client("Firstname", "Lastname", "11111111113", new Gold());
        clientRepository.add(client);
        Assertions.assertNotNull(clientRepository.findById("11111111113"));
        clientRepository.delete(client);
        Assertions.assertNull(clientRepository.findById("11111111113"));
    }
}
