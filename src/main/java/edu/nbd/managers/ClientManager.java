package edu.nbd.managers;

import edu.nbd.model.Client;
import edu.nbd.repositories.IRepository;

import java.io.Serializable;
import java.util.Objects;

public class ClientManager implements Serializable {
    private final IRepository<Client> clientRepository;

    public ClientManager(IRepository<Client> clientRepository) {
        Objects.requireNonNull(clientRepository, "ClientRepository is null");

        this.clientRepository = clientRepository;
    }

    public Client registerClient(Client client) {
        Client newClient = clientRepository.findById(client.getId());
        if (newClient != null) {
            client.setArchived(false);
            clientRepository.update(client);
        } else {
            clientRepository.add(client);
        }
        return client;
    }

    public void unregisterClient(Client client) {
        if (client != null) {
            client.setArchived(true);
            clientRepository.update(client);
        }
    }
}
