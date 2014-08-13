package com.ibiscus.shopnchek.web.controller.site;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ibiscus.shopnchek.application.orden.ItemsOrdenService;
import com.ibiscus.shopnchek.domain.admin.MedioPago;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.OrderState;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;

@Controller
@RequestMapping("/orden")
public class OrdenPagoController {

  /** Repository of orders. */
  @Autowired
  private OrderRepository orderRepository;

  /** Repository of shoppers. */
  @Autowired
  private ShopperRepository shopperRepository;

  /** Repository of shoppers. */
  @Autowired
  private ItemsOrdenService itemsOrdenService;

  @RequestMapping(value="/")
  public String index(@ModelAttribute("model") final ModelMap model) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);
    model.addAttribute("orderStates", orderRepository.findOrderStates());
    model.addAttribute("mediosPago", orderRepository.findMediosPago());
    return "ordenPago";
  }

  @RequestMapping(value="/{orderId}")
  public String get(@ModelAttribute("model") final ModelMap model,
      @PathVariable long orderId) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);
    model.addAttribute("orderStates", orderRepository.findOrderStates());
    model.addAttribute("mediosPago", orderRepository.findMediosPago());
    OrdenPago ordenPago = orderRepository.get(orderId);
    model.addAttribute("ordenPago", ordenPago);
    Shopper shopper = shopperRepository.get(ordenPago.getProveedor());
    model.addAttribute("titular", shopper);
    return "ordenPago";
  }

  @RequestMapping(value="create", method=RequestMethod.POST)
  public String createOrden(@ModelAttribute("model") final ModelMap model,
      int tipoTitular, int titularId, String tipoFactura, Date fechaPago,
      long estadoId, long medioPagoId, double iva, String facturaNumero,
      Date fechaCheque, String chequeraNumero, String chequeNumero,
      String transferId, String localidad) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);
    model.addAttribute("orderStates", orderRepository.findOrderStates());
    model.addAttribute("mediosPago", orderRepository.findMediosPago());

    OrderState state = orderRepository.getOrderState(estadoId);
    MedioPago medioPago = orderRepository.getMedioPago(estadoId);
    OrdenPago ordenPago = new OrdenPago(tipoTitular, titularId,
        tipoFactura, fechaPago, state, medioPago, iva);
    ordenPago.update(tipoTitular, titularId, tipoFactura, fechaPago, state,
        medioPago, iva, facturaNumero, fechaCheque, chequeraNumero,
        chequeNumero, transferId, localidad);

    long numeroOrden = orderRepository.save(ordenPago);
    ordenPago = orderRepository.get(numeroOrden);
    model.addAttribute("ordenPago", ordenPago);
    Shopper shopper = shopperRepository.get(ordenPago.getProveedor());
    model.addAttribute("titular", shopper);
    return "ordenPago";
  }

  @RequestMapping(value="save", method=RequestMethod.POST)
  public String update(@ModelAttribute("model") final ModelMap model,
      int numeroOrden, int tipoTitular, int titularId, String tipoFactura,
      Date fechaPago, long estadoId, long medioPagoId, double iva,
      String numeroFactura, Date fechaCheque, String numeroChequera,
      String numeroCheque, String transferId, String localidad) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);
    model.addAttribute("orderStates", orderRepository.findOrderStates());
    model.addAttribute("mediosPago", orderRepository.findMediosPago());

    OrderState state = orderRepository.getOrderState(estadoId);
    MedioPago medioPago = orderRepository.getMedioPago(estadoId);

    OrdenPago ordenPago = orderRepository.get(numeroOrden);
    ordenPago.update(tipoTitular, titularId, tipoFactura, fechaPago,
        state, medioPago, iva, numeroFactura, fechaCheque, numeroChequera,
        numeroCheque, transferId, localidad);
    orderRepository.update(ordenPago);

    model.addAttribute("ordenPago", ordenPago);
    Shopper shopper = shopperRepository.get(ordenPago.getProveedor());
    model.addAttribute("titular", shopper);
    return "ordenPago";
  }
}
