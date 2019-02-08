package com.ibiscus.shopnchek.web.controller.domain;

import java.io.IOException;
import java.util.List;

import com.ibiscus.shopnchek.application.shopper.*;
import com.ibiscus.shopnchek.domain.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.SimpleFormController;

import static java.util.Collections.emptyList;

@Controller
@RequestMapping(value="/shoppers")
public class ShopperController extends SimpleFormController{

  @Autowired
  private ShopperRepository shopperRepository;

  @Autowired
  private GetShopperCommand getShopperCommand;

  @Autowired
  private SearchShopperCommand searchShopperCommand;

  @Autowired
  private CreateShopperCommand createShopperCommand;

  @Autowired
  private SaveShopperCommand saveShopperCommand;

  @Autowired
  private ImportShoppersFileCommand importShoppersFileCommand;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String list(@ModelAttribute("model") ModelMap model, String name) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", user);
        model.put("name", name);

        List<Shopper> shoppers = searchShopperCommand.execute(name);
        model.put("shoppers", shoppers);
        return "listShoppers";
    }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public String edit(@ModelAttribute("model") ModelMap model, @PathVariable Long id) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    Shopper shopper = getShopperCommand.execute(id);
    model.put("shopper", shopper);
    return "shopper";
  }

  @RequestMapping(value = "/create", method = RequestMethod.GET)
  public String newShopper(@ModelAttribute("model") ModelMap model) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);
    return "shopper";
  }

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public String create(@ModelAttribute("model") ModelMap model, @ModelAttribute("shopper") NewShopper newShopper) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    createShopperCommand.execute(newShopper);
    return "redirect:.";
  }

  @RequestMapping(value = "/update", method = RequestMethod.POST)
  public String update(@ModelAttribute("model") ModelMap model, EditedShopper editedShopper) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    saveShopperCommand.execute(editedShopper);
    return "redirect:.";
  }

  @RequestMapping(value = "/suggest", method = RequestMethod.GET)
  public @ResponseBody List<Shopper> suggest(@RequestParam String term) {
    return shopperRepository.find(term, true);
  }

  @RequestMapping(value = "/import", method = RequestMethod.GET)
  public String prepareToImport(@ModelAttribute("model") ModelMap model) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);
    model.addAttribute("items", emptyList());
    return "importShoppers";
  }

  @RequestMapping(value="/import", method = RequestMethod.POST)
  public String importFile(@ModelAttribute("model") ModelMap model, MultipartFile file) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);
    try {
      List<ImportItemResult> result = importShoppersFileCommand.execute(file);
      model.addAttribute("items", result);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return "importShoppers";
  }

}
