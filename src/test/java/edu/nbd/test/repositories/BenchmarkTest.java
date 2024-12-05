package edu.nbd.test.repositories;

import edu.nbd.model.Client;
import edu.nbd.model.Default;
import edu.nbd.repositories.ClientRepository;
import edu.nbd.repositories.MongoClientRepository;
import edu.nbd.repositories.RedisClientRepository;
import org.junit.jupiter.api.Nested;
import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
public class BenchmarkTest {
    private MongoClientRepository mongoClientRepository;
    private RedisClientRepository redisClientRepository;
    private ClientRepository clientRepository;

    @Setup
    public void init() {
        mongoClientRepository = new MongoClientRepository();
        redisClientRepository = new RedisClientRepository();
        clientRepository = new ClientRepository(mongoClientRepository, redisClientRepository);

        Client client = new Client("11111111110", "Firstname", "Lastname", new Default());
        clientRepository.add(client);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(java.util.concurrent.TimeUnit.MILLISECONDS)
    public void testFindyById_CacheHit() {
        clientRepository.findById("11111111110");
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(java.util.concurrent.TimeUnit.MILLISECONDS)
    public void testFindyById_CacheMiss() {
        redisClientRepository.clearCache();
        clientRepository.findById("11111111110");
    }

}
