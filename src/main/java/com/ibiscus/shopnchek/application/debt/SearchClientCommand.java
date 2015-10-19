package com.ibiscus.shopnchek.application.debt;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.debt.Client;
import com.ibiscus.shopnchek.domain.debt.ClientRepository;

public class SearchClientCommand implements Command<List<Client>> {

	private final ClientRepository clientRepository;

	private String name;

	public SearchClientCommand(final ClientRepository clientRepository) {
		Validate.notNull(clientRepository, "The client repository cannot be null");
		this.clientRepository = clientRepository;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public List<Client> execute() {
		return clientRepository.find(name);
	}

	public void setName(final String name) {
		this.name = name;
	}
}
