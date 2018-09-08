package com.ibiscus.shopnchek.application.client;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;
import com.ibiscus.shopnchek.domain.debt.Client;
import com.ibiscus.shopnchek.domain.debt.ClientRepository;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class GetClientCommand implements Command<ClientDto> {

	private final ClientRepository clientRepository;

	private final long clientId;

	public GetClientCommand(ClientRepository clientRepository, long clientId) {
		this.clientRepository = clientRepository;
		this.clientId = clientId;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public ClientDto execute() {
		Client client = clientRepository.get(clientId);
		return new ClientDto(client);
	}

}
