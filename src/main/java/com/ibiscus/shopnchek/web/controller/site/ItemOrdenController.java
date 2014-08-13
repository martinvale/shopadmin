package com.ibiscus.shopnchek.web.controller.site;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibiscus.shopnchek.application.orden.ItemsOrdenService;
import com.ibiscus.shopnchek.domain.admin.ItemOrden;

@Controller
@RequestMapping("/item")
public class ItemOrdenController {

  /** Repository of shoppers. */
  @Autowired
  private ItemsOrdenService itemsOrdenService;

  @RequestMapping(value="/mdc/{dniShopper}")
  public @ResponseBody List<ItemOrden> index(
      @ModelAttribute("model") final ModelMap model,
      @PathVariable String dniShopper) {
    List<ItemOrden> items = itemsOrdenService
        .getItemsOrdenDisponiblesMCD(dniShopper);
    return items;
  }

}
