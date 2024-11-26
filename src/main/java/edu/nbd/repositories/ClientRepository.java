package edu.nbd.repositories;

import edu.nbd.model.Client;

import java.util.ArrayList;

public class ClientRepository implements IRepository<Client> {

    private final IRepository<Client> clientRepository;
    private final RedisClientRepository redisClientRepository;

    public ClientRepository(IRepository<Client> clientRepository, RedisClientRepository redisClientRepository) {
        this.clientRepository = clientRepository;
        this.redisClientRepository = redisClientRepository;
    }

    public Client findById(Object id) {
        Client cachedClient = redisClientRepository.findById(id);
        if (cachedClient != null) {
            return cachedClient;
        } else {
            Client client = clientRepository.findById(id);
            if (client != null) {
                redisClientRepository.add(client);
            }
            return client;
        }
    }

    public ArrayList<Client> findAll() {
        ArrayList<Client> clients = clientRepository.findAll();
        clients.forEach(client -> {
            Client cachedClient = redisClientRepository.findById(client.getPersonalID());
            if (cachedClient == null) {
                redisClientRepository.add(client);
            }
        });
        return clients;
    }

    public void add(Client client) {
        clientRepository.add(client);
        redisClientRepository.add(client);
    }

    public void update(Client client) {
        clientRepository.update(client);
        redisClientRepository.update(client);
    }

    public void delete(Client client) {
        clientRepository.delete(client);
        redisClientRepository.delete(client);
    }
}
