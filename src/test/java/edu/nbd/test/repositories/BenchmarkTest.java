package edu.nbd.test.repositories;

import edu.nbd.model.Client;
import edu.nbd.model.Default;
import edu.nbd.repositories.ClientRepository;
import edu.nbd.repositories.MongoClientRepository;
import edu.nbd.repositories.RedisClientRepository;
import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
public class BenchmarkTest {
    private MongoClientRepository mongoClientRepository;
    private RedisClientRepository redisClientRepository;
    private ClientRepository clientRepository;

    private int numberOfClients;

    @Setup
    public void init() {
        mongoClientRepository = new MongoClientRepository();
        redisClientRepository = new RedisClientRepository();
        clientRepository = new ClientRepository(mongoClientRepository, redisClientRepository);
        numberOfClients = 10;
        for (int i = 1; i <= numberOfClients; i++) {
            Client client = new Client("1111111111" + i, "Firstname" + i, "Lastname" + i, new Default());
            clientRepository.add(client);
        }
    }

    @Benchmark
    @Warmup(iterations = 0)
    @Fork(value = 2)
    public void readFromCache(){
        for (int i = 1; i <= numberOfClients; i++) {
            clientRepository.findById("1111111111" + i);
        }
    }

    @Benchmark
    @Warmup(iterations = 0)
    @Fork(value = 2)
    public void readFromMongoUsingRepository() {
        redisClientRepository.clearCache();
        for (int i = 1; i <= numberOfClients; i++) {
            clientRepository.findById("1111111111" + i);
        }
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    @TearDown
    public void cleanUp() {
        mongoClientRepository.getDatabase().getCollection("clients", Client.class).drop();
        redisClientRepository.clearCache();
    }

}
