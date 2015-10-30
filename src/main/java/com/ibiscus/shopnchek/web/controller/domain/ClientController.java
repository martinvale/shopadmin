package com.ibiscus.shopnchek.web.controller.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibiscus.shopnchek.application.debt.ClientDto;
import com.ibiscus.shopnchek.application.debt.SearchClientDtoCommand;

@Controller
@RequestMapping(value="/services/client")
public class ClientController {

	@Autowired
	private SearchClientDtoCommand searchClientDtoCommand;

	@RequestMapping(value = "/findByName", method = RequestMethod.GET)
	public @ResponseBody List<ClientDto> findByName(@RequestParam String term) {
		searchClientDtoCommand.setName(term);
		return searchClientDtoCommand.execute();
	}

}
