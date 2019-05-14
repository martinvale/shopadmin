package com.ibiscus.shopnchek.application.client;

import com.ibiscus.shopnchek.domain.debt.Client;
import com.ibiscus.shopnchek.domain.debt.ClientRepository;

public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public ClientDto get(long clientId) {
        Client client = clientRepository.get(clientId);
        return new ClientDto(client);
    }

    public boolean delete(long clientId) {
        try {
            Client client = clientRepository.get(clientId);
            clientRepository.delete(client);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void save(ClientDto clientDto) {
        if (clientDto.getId() != null) {
            Client client = clientRepository.get(clientDto.getId());
            client.update(clientDto.getName(), clientDto.getCountry());
            clientRepository.update(client);
        } else {
            Client client = new Client(clientDto.getName(), clientDto.getCountry());
            clientRepository.save(client);
        }
    }

    public void reassign(long clientId, String clientsToReassign) {
        String[] clientsIds = clientsToReassign.split(",");
        for (String clientIdToReassign : clientsIds) {
            Long clientIdValue = Long.valueOf(clientIdToReassign.trim());
            clientRepository.reassign(clientIdValue, clientId);
        }
    }
}
