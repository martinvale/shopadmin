package com.ibiscus.shopnchek.web.controller.site;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

  @RequestMapping(value="")
  public String index(@ModelAttribute("model") final ModelMap model) {
    /*User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();*/
    return "redirect:orden/";
  }

  @RequestMapping(value="login")
  public String login(@ModelAttribute("model") final ModelMap model,
      String error) {
    model.addAttribute("error", error);
    return "login";
  }

}
