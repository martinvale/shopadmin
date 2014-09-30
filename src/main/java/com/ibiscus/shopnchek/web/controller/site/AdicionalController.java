package com.ibiscus.shopnchek.web.controller.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ibiscus.shopnchek.application.orden.ItemsOrdenService;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.ProveedorRepository;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;

@Controller
@RequestMapping("/adicional")
public class AdicionalController {

  /** Repository of orders. */
  @Autowired
  private OrderRepository orderRepository;

  /** Repository of shoppers. */
  @Autowired
  private ShopperRepository shopperRepository;

  /** Repository of proveedores. */
  @Autowired
  private ProveedorRepository proveedorRepository;

  /** Repository of items. */
  @Autowired
  private ItemsOrdenService itemsOrdenService;

  @RequestMapping(value="/autorizar")
  public String index(@ModelAttribute("model") final ModelMap model) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);
    return "autorizarAdicional";
  }

}
