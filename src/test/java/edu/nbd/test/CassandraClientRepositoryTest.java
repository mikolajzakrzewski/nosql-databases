package edu.nbd.test;

import edu.nbd.model.Client;
import edu.nbd.model.Default;
import edu.nbd.model.Gold;
import edu.nbd.repositories.CassandraClientRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CassandraClientRepositoryTest {

    private static CassandraClientRepository CASSANDRA_CLIENT_REPOSITORY;

    @BeforeAll
    public static void setup() {
        CASSANDRA_CLIENT_REPOSITORY = new CassandraClientRepository();
    }

    @AfterAll
    public static void tearDown() {
        CASSANDRA_CLIENT_REPOSITORY.close();
    }

    @Test
    public void findById_ClientInDB_ClientReturned() {
        Client client = new Client("11111111110", "Firstname", "Lastname", new Default());
        CASSANDRA_CLIENT_REPOSITORY.add(client);
        Client foundClient = CASSANDRA_CLIENT_REPOSITORY.findById("11111111110");
        Assertions.assertEquals(client.getClientInfo(), foundClient.getClientInfo());
    }

    @Test
    public void add_ValidClient_ClientAdded() {
        Client client = new Client("11111111113", "Firstname", "Lastname", new Gold());
        CASSANDRA_CLIENT_REPOSITORY.add(client);
        Client foundClient = CASSANDRA_CLIENT_REPOSITORY.findById("11111111113");
        Assertions.assertEquals(client.getClientInfo(), foundClient.getClientInfo());
    }

    @Test
    public void update_UpdateClient_ClientUpdated() {
        Client client = new Client("11111111114", "Firstname", "Lastname", new Default());
        CASSANDRA_CLIENT_REPOSITORY.add(client);
        client.setFirstName("AltFirstname");
        CASSANDRA_CLIENT_REPOSITORY.update(client);
        Assertions.assertEquals(client.getFirstName(), CASSANDRA_CLIENT_REPOSITORY.findById("11111111114").getFirstName());
    }

    @Test
    public void delete_ClientInDB_ClientDeleted() {
        Client client = new Client("11111111115", "Firstname", "Lastname", new Default());
        CASSANDRA_CLIENT_REPOSITORY.add(client);
        Assertions.assertNotNull(CASSANDRA_CLIENT_REPOSITORY.findById("11111111115"));
        CASSANDRA_CLIENT_REPOSITORY.delete(client);
        Assertions.assertNull(CASSANDRA_CLIENT_REPOSITORY.findById("11111111115"));
    }
}
