package com.ibiscus.shopnchek.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

  @RequestMapping(value="")
  public String index(final ModelMap model) {
    return "index";
  }

  @RequestMapping(value="login")
  public String login(@ModelAttribute("model") final ModelMap model, String error) {
    model.addAttribute("error", error);
    return "login";
  }
}
