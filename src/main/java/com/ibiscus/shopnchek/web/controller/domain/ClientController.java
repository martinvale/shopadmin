package com.ibiscus.shopnchek.web.controller.domain;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.ibiscus.shopnchek.application.client.ClientService;
import com.ibiscus.shopnchek.domain.debt.Client;
import com.ibiscus.shopnchek.domain.debt.ClientRepository;
import com.ibiscus.shopnchek.domain.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import com.ibiscus.shopnchek.application.client.ClientDto;
import com.ibiscus.shopnchek.application.client.SearchClientCommand;

@Controller
@RequestMapping(value="/services/client")
public class ClientController {

	@Autowired
	private SearchClientCommand searchClientCommand;

	@Autowired
	private ClientRepository clientRepository;

    @Autowired
    private ClientService clientService;

	@RequestMapping(value = "/findByName", method = RequestMethod.GET)
	public @ResponseBody List<ClientDto> findByName(@RequestParam String term) {
		searchClientCommand.setName(term);
		return searchClientCommand.execute();
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String list(@ModelAttribute("model") final ModelMap model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", user);

		searchClientCommand.setName(null);
		model.put("clients", searchClientCommand.execute());
		return "listClients";
	}

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String get(@ModelAttribute("model") final ModelMap model, @PathVariable Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", user);

        ClientDto client = clientService.get(id);
        model.put("client", client);
        List<String> countries = ImmutableList.of("US", "AR");
        model.put("countries", countries);
        return "client";
    }

    @RequestMapping(value = "/reassign/{id}", method = RequestMethod.GET)
    public String toReassign(@ModelAttribute("model") final ModelMap model, @PathVariable Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", user);

        ClientDto client = clientService.get(id);
        model.put("client", client);

        searchClientCommand.setName(null);
        model.put("clients", searchClientCommand.execute());

        return "clientReassign";
    }

    @RequestMapping(value = "/reassign", method = RequestMethod.POST)
    public String reassign(@ModelAttribute("model") final ModelMap model, Long clientId, Long newClientId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", user);

        clientService.reassign(clientId, newClientId);
        return "redirect:../";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("model") final ModelMap model, Long id, String name, String country) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", user);

        ClientDto client = new ClientDto(id, name, country);
        clientService.save(client);
        return "redirect:.";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody Boolean delete(@ModelAttribute("model") final ModelMap model, @PathVariable Long id) {
        return clientService.delete(id);
    }
}
