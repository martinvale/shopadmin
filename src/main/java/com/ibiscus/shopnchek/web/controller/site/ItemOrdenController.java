package com.ibiscus.shopnchek.web.controller.site;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibiscus.shopnchek.application.orden.ItemsOrdenService;
import com.ibiscus.shopnchek.domain.admin.Adicional;
import com.ibiscus.shopnchek.domain.admin.ItemOrden;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;
import com.ibiscus.shopnchek.domain.admin.TipoPago;
import com.ibiscus.shopnchek.domain.admin.Visita;
import com.ibiscus.shopnchek.domain.security.UserRepository;

@Controller
@RequestMapping("/item")
public class ItemOrdenController {

  /** Repository of shoppers. */
  @Autowired
  private ItemsOrdenService itemsOrdenService;

  /** Repository of orders. */
  @Autowired
  private OrderRepository orderRepository;

  /** Repository of users. */
  @Autowired
  private UserRepository userRepository;

  /** Repository of shoppers. */
  @Autowired
  private ShopperRepository shopperRepository;

  @RequestMapping(value="/mdc/{dniShopper}")
  public @ResponseBody List<Visita> index(
      @ModelAttribute("model") final ModelMap model,
      @PathVariable String dniShopper) {
    List<Visita> items = itemsOrdenService
        .getVisitasDisponiblesMCD(dniShopper);
    return items;
  }

  @RequestMapping(value="/adicionales/{dniShopper}")
  public @ResponseBody List<Adicional> listarAdicionales(
      @ModelAttribute("model") final ModelMap model,
      @PathVariable String dniShopper) {
    List<Adicional> items = itemsOrdenService
        .getAdicionalesDisponibles(dniShopper);
    return items;
  }

  @RequestMapping(value="/create", method = RequestMethod.POST)
  public @ResponseBody Boolean create(
      @ModelAttribute("model") final ModelMap model,
      long ordenNro, int tipoPago, String shopperDni, Integer asignacion,
      double importe, String cliente, String sucursal, int mes, int anio,
      String fecha) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    com.ibiscus.shopnchek.domain.security.User autorizador;
    autorizador = userRepository.findByUsername(user.getUsername());
    Shopper shopper = shopperRepository.findByDni(shopperDni);
    OrdenPago ordenPago = orderRepository.get(ordenNro);
    TipoPago unTipoPago = orderRepository.getTipoPago(tipoPago);
    Long newId = itemsOrdenService.getItemOrdenId();
    ItemOrden itemOrden = new ItemOrden(newId, ordenPago, autorizador.getId(),
        shopperDni, asignacion, 2, unTipoPago,
        cliente, sucursal, mes, anio, fecha, importe, null, 1);
    itemOrden.updateShopper(shopper);
    orderRepository.saveItem(itemOrden);
    return Boolean.TRUE;
  }

  @RequestMapping(value="/createAdicional", method = RequestMethod.POST)
  public @ResponseBody Boolean createAdicional(
      @ModelAttribute("model") final ModelMap model,
      long ordenNro, int tipoPago, String shopperDni, Integer asignacion,
      double importe, String cliente, String sucursal, int mes, int anio,
      String fecha, String descripcion) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    com.ibiscus.shopnchek.domain.security.User autorizador;
    autorizador = userRepository.findByUsername(user.getUsername());
    Shopper shopper = shopperRepository.findByDni(shopperDni);
    OrdenPago ordenPago = orderRepository.get(ordenNro);
    TipoPago unTipoPago = orderRepository.getTipoPago(tipoPago);
    Long newId = itemsOrdenService.getItemOrdenId();
    ItemOrden itemOrden = new ItemOrden(newId, ordenPago, autorizador.getId(),
        shopperDni, asignacion, 3, unTipoPago,
        cliente, sucursal, mes, anio, fecha, importe, descripcion, 1);
    itemOrden.updateShopper(shopper);
    orderRepository.saveItem(itemOrden);
    itemsOrdenService.linkAdicional(asignacion, ordenNro);
    return Boolean.TRUE;
  }
}
