package com.ibiscus.shopnchek.web.controller.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ibiscus.shopnchek.application.shopper.SaveShopperCommand;
import com.ibiscus.shopnchek.domain.security.User;

@Controller
@RequestMapping(value="/shoppers")
public class ShoppersController {

  /** Update shopper command. */
  @Autowired
  private SaveShopperCommand saveShopperCommand;

  @RequestMapping(value = "/search", method = RequestMethod.GET)
  public String search(@ModelAttribute("model") final ModelMap model) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
            .getPrincipal();
    model.addAttribute("user", user);
    return "buscadorShoppers";
  }

  @RequestMapping(value = "/update", method = RequestMethod.POST)
  public String update(@ModelAttribute("model") final ModelMap model, String dni,
		  String login) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
    		.getPrincipal();
    model.addAttribute("user", user);

    saveShopperCommand.setShopperDni(dni);
    saveShopperCommand.setLogin(login);
    saveShopperCommand.execute();

    return "buscadorShoppers";
  }
}
