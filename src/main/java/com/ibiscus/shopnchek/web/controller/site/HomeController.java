package com.ibiscus.shopnchek.web.controller.site;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ibiscus.shopnchek.domain.admin.MedioPago;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.OrderState;

@Controller
@RequestMapping("/")
public class HomeController {

  /** Repository of orders. */
  @Autowired
  private OrderRepository orderRepository;

  @RequestMapping(value="")
  public String index(@ModelAttribute("model") final ModelMap model) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);
    model.addAttribute("orderStates", orderRepository.findOrderStates());
    model.addAttribute("mediosPago", orderRepository.findMediosPago());
    return "index";
  }

  @RequestMapping(value="login")
  public String login(@ModelAttribute("model") final ModelMap model,
      String error) {
    model.addAttribute("error", error);
    return "login";
  }

  @RequestMapping(value="orden", method=RequestMethod.POST)
  public String createOrden(@ModelAttribute("model") final ModelMap model,
      int tipoTitular, int titularId, String tipoFactura, Date fechaPago,
      long estadoId, long medioPagoId, double iva) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);
    model.addAttribute("orderStates", orderRepository.findOrderStates());
    model.addAttribute("mediosPago", orderRepository.findMediosPago());

    OrderState state = orderRepository.getOrderState(estadoId);
    MedioPago medioPago = orderRepository.getMedioPago(estadoId);
    OrdenPago ordenPago = new OrdenPago(tipoTitular, titularId,
        tipoFactura, fechaPago, state, medioPago, iva);

    ordenPago = orderRepository.save(ordenPago);
    model.addAttribute("ordenPago", ordenPago);
    return "index";
  }
}
