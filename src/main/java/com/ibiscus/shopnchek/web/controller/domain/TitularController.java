package com.ibiscus.shopnchek.web.controller.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibiscus.shopnchek.application.ResultSet;
import com.ibiscus.shopnchek.application.proveedor.GetTitularCommand;
import com.ibiscus.shopnchek.application.proveedor.SaveTitularCommand;
import com.ibiscus.shopnchek.application.proveedor.SearchTitularCommand;
import com.ibiscus.shopnchek.application.proveedor.TitularDTO;
import com.ibiscus.shopnchek.domain.security.User;

@Controller
@RequestMapping(value="/titular")
public class TitularController {

  @Autowired
  private SaveTitularCommand saveTitularCommand;

  @Autowired
  private GetTitularCommand getTitularCommand;

  @Autowired
  private SearchTitularCommand searchTitularCommand;

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String search(@ModelAttribute("model") final ModelMap model) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
            .getPrincipal();
    model.addAttribute("user", user);
    return "buscadorTitulares";
  }

  @RequestMapping(value = "/suggest", method = RequestMethod.GET)
  public @ResponseBody List<TitularDTO> suggest(@RequestParam String term) {
    searchTitularCommand.setPage(1);
    searchTitularCommand.setPageSize(50);
    searchTitularCommand.setName(term);
    ResultSet<TitularDTO> rsTitulares = searchTitularCommand.execute();
    return rsTitulares.getItems();
  }

  @RequestMapping(value = "/view", method = RequestMethod.GET)
  public @ResponseBody TitularDTO view(@ModelAttribute("model") final ModelMap model, final long titularId, final int titularTipo) {
    getTitularCommand.setTitularId(titularId);
    getTitularCommand.setTitularTipo(titularTipo);
    return getTitularCommand.execute();
  }

  @RequestMapping(value = "/edit", method = RequestMethod.GET)
  public String edit(@ModelAttribute("model") final ModelMap model, final long titularId, final int titularTipo) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    getTitularCommand.setTitularId(titularId);
    getTitularCommand.setTitularTipo(titularTipo);
    TitularDTO titularDTO = getTitularCommand.execute();
    model.addAttribute("titular", titularDTO);
    return "editarTitular";
  }

  @RequestMapping(value = "/update", method = RequestMethod.POST)
  public String update(@ModelAttribute("model") final ModelMap model, final long titularId, final int titularTipo,
		String name, String email, String login, String cuit, String tipoFactura, String banco, String cbu, String number,
		boolean linked, Long billingId, Integer billingTipo) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
    		.getPrincipal();
    model.addAttribute("user", user);

    saveTitularCommand.setTitularTipo(titularTipo);
    saveTitularCommand.setTitularId(titularId);
    saveTitularCommand.setName(name);
    saveTitularCommand.setEmail(email);
    saveTitularCommand.setLoginShopmetrics(login);
    saveTitularCommand.setLinked(linked);
    saveTitularCommand.setCuit(cuit);
    saveTitularCommand.setFactura(tipoFactura);
    saveTitularCommand.setBanco(banco);
    saveTitularCommand.setCbu(cbu);
    saveTitularCommand.setNumber(number);
    saveTitularCommand.setBillingId(billingId);
    saveTitularCommand.setBillingTipo(billingTipo);
    saveTitularCommand.execute();

    return "redirect:.";
  }
}
