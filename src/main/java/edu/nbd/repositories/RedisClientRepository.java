package edu.nbd.repositories;

import edu.nbd.model.Client;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.util.ArrayList;

public class RedisClientRepository extends AbstractRedisRepository implements IRepository<Client> {

    private final static String hashPrefix = "client:";
    private final Jsonb jsonb = JsonbBuilder.create();

    public Client findById(Object id) {
        String jsonClient = jsonb.toJson(getPool().jsonGet(hashPrefix + id));
        return jsonb.fromJson(jsonClient, Client.class);
    }

    public ArrayList<Client> findAll() {
        ArrayList<Client> clients = new ArrayList<>();
        getPool().keys(hashPrefix + "*").forEach(key -> {
            String jsonClient = jsonb.toJson(getPool().jsonGet(key));
            clients.add(jsonb.fromJson(jsonClient, Client.class));
        });
        return clients;
    }

    public void add(Client client) {
        String jsonClient = jsonb.toJson(client);
        getPool().jsonSet(hashPrefix + client.getPersonalID(), jsonClient);
        getPool().expire(hashPrefix + client.getPersonalID(), 60);
    }

    public void update(Client client) {
        String jsonClient = jsonb.toJson(client);
        getPool().jsonSet(hashPrefix + client.getPersonalID(), jsonClient);
    }

    public void delete(Client client) {
        getPool().del(hashPrefix + client.getPersonalID());
    }
}
