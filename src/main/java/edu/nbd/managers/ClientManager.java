package edu.nbd.managers;

import edu.nbd.model.Client;
import edu.nbd.repositories.ClientRepository;

import java.io.Serializable;

public class ClientManager implements Serializable {
    private ClientRepository clientRepository;

    public ClientManager(ClientRepository clientRepository) {
        if (clientRepository == null) {
            throw new NullPointerException("clientRepository is null");
        } else {
            this.clientRepository = clientRepository;
        }
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
