package com.ibiscus.shopnchek.application.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.debt.Client;
import com.ibiscus.shopnchek.domain.debt.ClientRepository;

public class SearchClientCommand implements Command<List<ClientDto>> {

	private ClientRepository clientRepository;

	private String name;

	public SearchClientCommand() {
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public List<ClientDto> execute() {
		List<ClientDto> clientDtos = new ArrayList<ClientDto>();
		List<Client> clients = clientRepository.find(name);
		for (Client client : clients) {
			clientDtos.add(new ClientDto(client));
		}
		return clientDtos;
	}

	public void setClientRepository(final ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
