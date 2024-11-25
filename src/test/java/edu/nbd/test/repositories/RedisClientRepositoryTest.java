package edu.nbd.test.repositories;

import edu.nbd.model.Client;
import edu.nbd.model.Default;
import edu.nbd.repositories.RedisClientRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RedisClientRepositoryTest {
    private static final RedisClientRepository redisClientRepository = new RedisClientRepository();

    @BeforeEach
    public void setUp() {
        redisClientRepository.clearCache();
    }

    @AfterAll
    public static void tearDown() {
        redisClientRepository.clearCache();
        redisClientRepository.close();
    }

    @Test
    public void findById_ClientInDB_ClientReturned() {
        Client client = new Client("11111111110", "Firstname", "Lastname", new Default());
        redisClientRepository.add(client);
        Client foundClient = redisClientRepository.findById("11111111110");
        Assertions.assertEquals(client.getClientInfo(), foundClient.getClientInfo());
    }

    @Test
    public void findAll_TwoClientsInDB_TwoClientsListReturned() {
        Client client1 = new Client("11111111111", "Firstname", "Lastname", new Default());
        Client client2 = new Client("11111111112", "Firstname", "Lastname", new Default());
        redisClientRepository.add(client1);
        redisClientRepository.add(client2);
        List<Client> addedClients = List.of(client1, client2);
        List<Client> foundClients = redisClientRepository.findAll();
        Assertions.assertEquals(2, foundClients.size());
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
        Client client = new Client("11111111113", "Firstname", "Lastname", new Default());
        redisClientRepository.add(client);
        Client foundClient = redisClientRepository.findById("11111111113");
        Assertions.assertEquals(client.getClientInfo(), foundClient.getClientInfo());
    }

    @Test
    public void update_UpdateClient_ClientUpdated() {
        Client client = new Client("11111111114", "Firstname", "Lastname", new Default());
        redisClientRepository.add(client);
        client.setFirstName("AltFirstname");
        redisClientRepository.update(client);
        Assertions.assertEquals(client.getFirstName(), redisClientRepository.findById("11111111114").getFirstName());
    }

    @Test
    public void delete_ClientInDB_ClientDeleted() {
        Client client = new Client("11111111115", "Firstname", "Lastname", new Default());
        redisClientRepository.add(client);
        Assertions.assertNotNull(redisClientRepository.findById("11111111115"));
        redisClientRepository.delete(client);
        Assertions.assertNull(redisClientRepository.findById("11111111115"));
    }
}
