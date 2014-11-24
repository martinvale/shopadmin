package com.ibiscus.shopnchek.web.controller.site;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibiscus.shopnchek.domain.admin.AutorizacionAdicional;
import com.ibiscus.shopnchek.domain.admin.ClienteShopmetrics;
import com.ibiscus.shopnchek.domain.admin.ItemOrderRepository;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.ProgramRepository;
import com.ibiscus.shopnchek.domain.admin.Programa;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;
import com.ibiscus.shopnchek.domain.admin.SucursalMCD;
import com.ibiscus.shopnchek.domain.admin.SucursalMCDRepository;
import com.ibiscus.shopnchek.domain.admin.SucursalShopmetrics;
import com.ibiscus.shopnchek.domain.admin.SucursalShopmetricsRepository;
import com.ibiscus.shopnchek.domain.admin.TipoPago;
import com.ibiscus.shopnchek.domain.security.UserRepository;
import com.ibiscus.shopnchek.domain.util.ResultSet;

@Controller
@RequestMapping("/adicional")
public class AdicionalController {

  /** The maximum users to retrieve per page. */
  private final static int PAGE_SIZE = 20;

  /** Repository of orders. */
  @Autowired
  private OrderRepository orderRepository;

  /** Repository of sucursales MCD. */
  @Autowired
  private SucursalMCDRepository sucursalMCDRepository;

  /** Repository of sucursales Shopmetrics. */
  @Autowired
  private SucursalShopmetricsRepository sucursalShopmetricsRepository;

  /** Repository of shoppers. */
  @Autowired
  private ShopperRepository shopperRepository;

  /** Repository of programs. */
  @Autowired
  private ProgramRepository programRepository;

  /** Repository of item order. */
  @Autowired
  private ItemOrderRepository itemOrdenRepository;

  /** Repository of users. */
  @Autowired
  private UserRepository userRepository;

  @RequestMapping(value="/autorizacion")
  public String index(@ModelAttribute("model") final ModelMap model,
      @RequestParam(required = false) Integer groupId,
      @RequestParam(required = false) Integer itemId) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);

    List<Programa> programas = programRepository.find();
    model.addAttribute("programas", programas);

    List<ClienteShopmetrics> clientes = itemOrdenRepository.findClientes();
    model.addAttribute("clientes", clientes);

    List<TipoPago> tiposPago = itemOrdenRepository.findTiposDePago();
    model.addAttribute("tiposPago", tiposPago);

    List<Shopper> shoppers = shopperRepository.find(null);
    model.addAttribute("shoppers", shoppers);

    List<SucursalMCD> sucursalesMCD = sucursalMCDRepository.find();
    model.addAttribute("sucursalesMCD", sucursalesMCD);

    List<SucursalShopmetrics> sucursalesShopmetrics = sucursalShopmetricsRepository.find();
    model.addAttribute("sucursalesShopmetrics", sucursalesShopmetrics);

    if (groupId != null) {
      List<AutorizacionAdicional> adicionales = itemOrdenRepository
          .findAdicionalesByGroup(groupId);
      model.addAttribute("adicionales", adicionales);
      if (itemId != null) {
        for (AutorizacionAdicional adicional : adicionales) {
          if (adicional.getId() == itemId) {
            model.addAttribute("adicional", adicional);
          }
        }
      }
    }

    return "autorizarAdicional";
  }

  @RequestMapping(value="/autorizar", method=RequestMethod.POST)
  public String autorizar(@ModelAttribute("model") final ModelMap model,
      @RequestParam(required = false) Integer groupId,
      @RequestParam(required = false) Integer itemId,
      @RequestParam(required = false, defaultValue = "-1") Integer clienteId,
      String clienteNombre,
      @RequestParam(required = false, defaultValue = "-1") String sucursalId,
      String sucursalNombre, String shopperDni, int mes, int anio, Date fecha,
      Date fechaCobro, String observacion, double importe, int tipoPagoId,
      int tipoItem) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);

    List<Programa> programas = programRepository.find();
    model.addAttribute("programas", programas);

    List<ClienteShopmetrics> clientes = itemOrdenRepository.findClientes();
    model.addAttribute("clientes", clientes);

    List<TipoPago> tiposPago = itemOrdenRepository.findTiposDePago();
    model.addAttribute("tiposPago", tiposPago);

    List<Shopper> shoppers = shopperRepository.find(null);
    model.addAttribute("shoppers", shoppers);

    List<SucursalMCD> sucursalesMCD = sucursalMCDRepository.find();
    model.addAttribute("sucursalesMCD", sucursalesMCD);

    List<SucursalShopmetrics> sucursalesShopmetrics = sucursalShopmetricsRepository.find();
    model.addAttribute("sucursalesShopmetrics", sucursalesShopmetrics);

    if (clienteId == null) {
      clienteId = -1;
    }
    if (tipoItem == 2) {
      Programa programa = programRepository.get(clienteId);
      clienteNombre = programa.getNombre();
      SucursalMCD sucursal = sucursalMCDRepository.get(sucursalId);
      sucursalNombre = sucursal.getDescription();
    } else if (tipoItem == 5) {
      ClienteShopmetrics client = itemOrdenRepository.getCliente(clienteId);
      clienteNombre = client.getNombre();
      SucursalShopmetrics sucursal = sucursalShopmetricsRepository.get(sucursalId);
      sucursalNombre = sucursal.getDescription();
    }
    TipoPago tipoPago = orderRepository.getTipoPago(tipoPagoId);
    AutorizacionAdicional adicional;
    if (itemId == null) {
      adicional = new AutorizacionAdicional(groupId, clienteId, clienteNombre,
          sucursalId, sucursalNombre, shopperDni, mes, anio, fecha,
          fechaCobro, observacion, importe, tipoPago, tipoItem,
          user.getUsername());
      itemOrdenRepository.saveAdicionalAutorizado(adicional);
    } else {
      adicional = itemOrdenRepository.getAdicionalAutorizado(itemId);
      adicional.update(fechaCobro, observacion, importe, tipoPago);
      itemOrdenRepository.updateAdicionalAutorizado(adicional);
    }
    return "redirect:autorizacion?groupId=" + adicional.getGroup();
  }

  @RequestMapping(value="/delete")
  public String delete(@ModelAttribute("model") final ModelMap model,
      int groupId, int itemId) {

    itemOrdenRepository.deleteAdicionalAutorizado(itemId);
    return "redirect:autorizacion?groupId=" + groupId;
  }

  @RequestMapping(value="/search")
  public String search(@ModelAttribute("model") final ModelMap model,
      @RequestParam(required = false, defaultValue = "1") Integer page,
      String shopperDni, @RequestParam(required = false) Integer mes,
      @RequestParam(required = false) Integer anio,
      @RequestParam(required = false) Long usuarioId) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);

    List<com.ibiscus.shopnchek.domain.security.User> users;
    users = userRepository.find();
    model.addAttribute("users", users);

    if (shopperDni != null && !shopperDni.isEmpty()) {
      model.addAttribute("shopperDni", shopperDni);
      Shopper shopper = shopperRepository.findByDni(shopperDni);
      model.addAttribute("shopper", shopper);
    }
    if (mes != null) {
      model.addAttribute("mesVisita", mes);
    }
    if (anio != null) {
      model.addAttribute("anioVisita", anio);
    }
    com.ibiscus.shopnchek.domain.security.User autorizador = null;
    if (usuarioId != null) {
      model.addAttribute("usuarioId", usuarioId);
      autorizador = userRepository.get(usuarioId);
    }
    List<AutorizacionAdicional> adicionales;
    int size = 0;
    if (shopperDni != null || mes != null || anio != null || usuarioId != null) {
      int start = ((page - 1) * PAGE_SIZE) + 1;
      adicionales = itemOrdenRepository.findAdicionales(start, PAGE_SIZE,
          shopperDni, mes, anio, autorizador);
      for (AutorizacionAdicional adicional : adicionales) {
        Shopper shopper = shopperRepository.findByDni(adicional.getShopperDni());
        adicional.updateShopper(shopper);
      }
      size = itemOrdenRepository.findAdicionalesCount(shopperDni, mes, anio,
          autorizador);
    } else {
      adicionales = new ArrayList<AutorizacionAdicional>();
    }
    model.addAttribute("items",
        new ResultSet<AutorizacionAdicional>(adicionales, size));
    model.addAttribute("start", 1);
    model.addAttribute("page", 1);
    model.addAttribute("pageSize", PAGE_SIZE);

    return "buscadorAdicionales";
  }
}
