package com.ibiscus.shopnchek.web.controller.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibiscus.shopnchek.application.debt.SearchClientCommand;
import com.ibiscus.shopnchek.domain.debt.Client;
import com.ibiscus.shopnchek.domain.debt.ClientRepository;

@Controller
@RequestMapping(value="/services/client")
public class ClientController {

	/** Repository of clients. */
	@Autowired
	private ClientRepository clientRepository;

	@RequestMapping(value = "/findByName", method = RequestMethod.GET)
	public @ResponseBody List<Client> findByName(@RequestParam String term) {
		SearchClientCommand command = new SearchClientCommand(clientRepository);
		command.setName(term);
		return command.execute();
	}

}
