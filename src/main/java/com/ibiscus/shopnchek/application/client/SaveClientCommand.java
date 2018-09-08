package com.ibiscus.shopnchek.application.client;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.debt.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class SaveClientCommand implements Command<ClientDto> {

	private final ClientRepository clientRepository;

	private final Long clienttId;

	private final String name;

	private final String country;

	public SaveClientCommand(ClientRepository clientRepository, Long clienttId, String name, String country) {
		this.clientRepository = clientRepository;
		this.clienttId = clienttId;
		this.name = name;
		this.country = country;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public ClientDto execute() {
		return null;
	}

}
