package edu.nbd.test.repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import edu.nbd.model.Client;
import edu.nbd.model.Default;
import edu.nbd.model.Gold;
import edu.nbd.repositories.MongoClientRepository;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.*;

import java.util.List;

public class MongoClientRepositoryTest {

    private static final MongoClientRepository MONGO_CLIENT_REPOSITORY = new MongoClientRepository();

    @BeforeEach
    public void setUp() {
        MONGO_CLIENT_REPOSITORY.getDatabase().getCollection("clients", Client.class).deleteMany(new Document());
    }

    @AfterAll
    public static void tearDown() {
        MONGO_CLIENT_REPOSITORY.getDatabase().getCollection("clients", Client.class).deleteMany(new Document());
        MONGO_CLIENT_REPOSITORY.close();
    }

    @Test
    public void findById_ClientInDB_ClientReturned() {
        Client client = new Client("11111111110", "Firstname", "Lastname", new Default());
        MONGO_CLIENT_REPOSITORY.add(client);
        Client foundClient;
        Bson filter = Filters.eq("_id", "11111111110");
        MongoCollection<Client> collection = MONGO_CLIENT_REPOSITORY.getDatabase().getCollection("clients", Client.class);
        foundClient = collection.find(filter).first();
        Assertions.assertNotNull(foundClient);
        Assertions.assertEquals(MONGO_CLIENT_REPOSITORY.findById("11111111110").getClientInfo(), foundClient.getClientInfo());
    }

    @Test
    public void findAll_TwoClientsInDB_TwoClientsListReturned() {
        Client client1 = new Client("11111111111", "Firstname", "Lastname", new Default());
        Client client2 = new Client("11111111112", "Firstname", "Lastname", new Default());
        MONGO_CLIENT_REPOSITORY.add(client1);
        MONGO_CLIENT_REPOSITORY.add(client2);
        List<Client> addedClients = List.of(client1, client2);
        List<Client> foundClients = MONGO_CLIENT_REPOSITORY.findAll();
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
        Client client = new Client("11111111111", "Firstname", "Lastname", new Default());
        MONGO_CLIENT_REPOSITORY.add(client);
        Assertions.assertEquals(MONGO_CLIENT_REPOSITORY.findById("11111111111").getClientInfo(), client.getClientInfo());
    }

    @Test
    public void update_UpdatedClient_ClientUpdated() {
        Client client = new Client("11111111112", "Firstname", "Lastname", new Default());
        MONGO_CLIENT_REPOSITORY.add(client);
        client.setFirstName("AltFirstname");
        MONGO_CLIENT_REPOSITORY.update(client);
        Assertions.assertEquals("AltFirstname", MONGO_CLIENT_REPOSITORY.findById("11111111112").getFirstName());
    }

    @Test
    public void delete_ClientInDB_ClientRemoved() {
        Client client = new Client("11111111113", "Firstname", "Lastname", new Gold());
        MONGO_CLIENT_REPOSITORY.add(client);
        Assertions.assertNotNull(MONGO_CLIENT_REPOSITORY.findById("11111111113"));
        MONGO_CLIENT_REPOSITORY.delete(client);
        Assertions.assertNull(MONGO_CLIENT_REPOSITORY.findById("11111111113"));
    }
}
