package com.ibiscus.shopnchek.web.controller.site;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibiscus.shopnchek.application.order.AsociarMedioPagoCommand;
import com.ibiscus.shopnchek.application.order.GetOrderCommand;
import com.ibiscus.shopnchek.application.order.ItemsOrdenService;
import com.ibiscus.shopnchek.application.order.PayOrderCommand;
import com.ibiscus.shopnchek.application.order.RemoveItemOrderCommand;
import com.ibiscus.shopnchek.application.order.SaveOrderCommand;
import com.ibiscus.shopnchek.application.order.TransitionOrderCommand;
import com.ibiscus.shopnchek.application.shopmetrics.ImportService;
import com.ibiscus.shopnchek.domain.admin.AsociacionMedioPago;
import com.ibiscus.shopnchek.domain.admin.ItemOrden;
import com.ibiscus.shopnchek.domain.admin.MedioPago;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.OrderState;
import com.ibiscus.shopnchek.domain.admin.Proveedor;
import com.ibiscus.shopnchek.domain.admin.ProveedorRepository;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;
import com.ibiscus.shopnchek.domain.security.User;

@Controller
@RequestMapping("/orden")
public class OrdenPagoController {

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

  /** Service to import external data. */
  @Autowired
  private ImportService importService;

  @Autowired
  private SaveOrderCommand saveOrderCommand;

  @Autowired
  private GetOrderCommand getOrderCommand;

  @Autowired
  private PayOrderCommand payOrderCommand;

  @Autowired
  private TransitionOrderCommand transitionOrderCommand;

  @Autowired
  private RemoveItemOrderCommand removeItemOrderCommand;

  @Autowired
  private AsociarMedioPagoCommand asociarMedioPagoCommand;

  @RequestMapping(value="/")
  public String index(@ModelAttribute("model") final ModelMap model) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);
    model.addAttribute("orderStates", orderRepository.findOrderStates());
    model.addAttribute("mediosPago", orderRepository.findMediosPago());
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, -4);
    model.addAttribute("fechaDesde", calendar.getTime());
    return "ordenPago";
  }

  @RequestMapping(value="/create")
  public String create(@ModelAttribute("model") final ModelMap model) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);
    return "createOrder";
  }

  @RequestMapping(value="/{orderId}")
  public String get(@ModelAttribute("model") final ModelMap model,
      @PathVariable long orderId) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);

    getOrderCommand.setNumero(orderId);
    OrdenPago ordenPago = getOrderCommand.execute();
    model.addAttribute("ordenPago", ordenPago);

    if (ordenPago.getTipoProveedor() == 1) {
      Shopper shopper = shopperRepository.get(ordenPago.getProveedor());
      model.addAttribute("titularNombre", shopper.getName());
    } else {
      Proveedor proveedor = proveedorRepository.get(ordenPago.getProveedor());
      model.addAttribute("titularNombre", proveedor.getDescription());
    }

    if (ordenPago.getEstado().getId() == OrderState.ABIERTA) {
      return "redirect:items/" + orderId;
    } else {
      return "redirect:view/" + orderId;
    }
  }

  @RequestMapping(value="/items/{orderId}")
  public String editItems(@ModelAttribute("model") final ModelMap model,
      @PathVariable long orderId) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);

    getOrderCommand.setNumero(orderId);
    OrdenPago ordenPago = getOrderCommand.execute();
    model.addAttribute("ordenPago", ordenPago);

    if (ordenPago.getTipoProveedor() == 1) {
      Shopper shopper = shopperRepository.get(ordenPago.getProveedor());
      model.addAttribute("titularNombre", shopper.getName());
    } else {
      Proveedor proveedor = proveedorRepository.get(ordenPago.getProveedor());
      model.addAttribute("titularNombre", proveedor.getDescription());
    }

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, -4);
    model.addAttribute("fechaDesde", calendar.getTime());
    return "orderItems";
  }

  @RequestMapping(value="create", method=RequestMethod.POST)
  public String createOrden(@ModelAttribute("model") final ModelMap model,
      int tipoTitular, int titularId, String tipoFactura,
      @DateTimeFormat(pattern="dd/MM/yyyy") Date fechaPago,
      @NumberFormat(style=Style.NUMBER, pattern="#,##") double iva, String numeroFactura,
      String localidad, String observaciones, String observacionesShopper) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);

    saveOrderCommand.setNumero(null);
    saveOrderCommand.setTipoProveedor(tipoTitular);
    saveOrderCommand.setProveedor(titularId);
    saveOrderCommand.setTipoFactura(tipoFactura);
    saveOrderCommand.setFechaPago(fechaPago);
    saveOrderCommand.setIva(iva);
    saveOrderCommand.setNumeroFactura(numeroFactura);
    saveOrderCommand.setLocalidad(localidad);
    saveOrderCommand.setObservaciones(observaciones);
    saveOrderCommand.setObservacionesShopper(observacionesShopper);

    OrdenPago ordenPago = saveOrderCommand.execute();
    model.addAttribute("ordenPago", ordenPago);

    Shopper shopper = shopperRepository.get(ordenPago.getProveedor());
    model.addAttribute("titular", shopper);
    return "redirect:items/" + ordenPago.getNumero();
  }

  @RequestMapping(value="createAnt", method=RequestMethod.POST)
  public String createOrdenAnt(@ModelAttribute("model") final ModelMap model,
      int tipoTitular, int titularId, String tipoFactura,
      @DateTimeFormat(pattern="dd/MM/yyyy") Date fechaPago,
      long estadoId, long medioPagoId,
      @NumberFormat(style=Style.NUMBER, pattern="#,##") double iva,
      String numeroFactura,
      String fechaCheque, String chequeraNumero,
      String chequeNumero,
      String transferId, String localidad, String observaciones,
      String observacionesShopper) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);
    model.addAttribute("orderStates", orderRepository.findOrderStates());
    model.addAttribute("mediosPago", orderRepository.findMediosPago());
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, -4);
    model.addAttribute("fechaDesde", calendar.getTime());

    OrderState state = orderRepository.getOrderState(estadoId);
    MedioPago medioPago = orderRepository.getMedioPago(medioPagoId);
    OrdenPago ordenPago = null;
    Date fechaChequeValue = null;
    if (fechaCheque != null && !fechaCheque.isEmpty()) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
      try {
        fechaChequeValue = dateFormat.parse(fechaCheque);
      } catch (ParseException e) {
        throw new RuntimeException("Cannot parse the following date: "
            + fechaCheque, e);
      }
    }
    /*ordenPago.update(tipoTitular, titularId, tipoFactura, fechaPago, state,
        medioPago, iva, numeroFactura, fechaChequeValue, chequeraNumero,
        chequeNumero, transferId, localidad, observaciones,
        observacionesShopper);*/

    long numeroOrden = orderRepository.save(ordenPago);
    ordenPago = orderRepository.get(numeroOrden);
    model.addAttribute("ordenPago", ordenPago);
    Shopper shopper = shopperRepository.get(ordenPago.getProveedor());
    model.addAttribute("titular", shopper);
    return "redirect:" + numeroOrden;
  }

  @RequestMapping(value="save", method=RequestMethod.POST)
  public String update(@ModelAttribute("model") final ModelMap model,
      long numeroOrden, int tipoTitular, int titularId, String tipoFactura,
      @DateTimeFormat(pattern="dd/MM/yyyy") Date fechaPago,
      @NumberFormat(style=Style.NUMBER, pattern="#,##") double iva,
      String numeroFactura, String localidad, String observaciones,
      String observacionesShopper) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);

    saveOrderCommand.setNumero(numeroOrden);
    saveOrderCommand.setTipoProveedor(tipoTitular);
    saveOrderCommand.setProveedor(titularId);
    saveOrderCommand.setTipoFactura(tipoFactura);
    saveOrderCommand.setFechaPago(fechaPago);
    saveOrderCommand.setIva(iva);
    saveOrderCommand.setNumeroFactura(numeroFactura);
    saveOrderCommand.setLocalidad(localidad);
    saveOrderCommand.setObservaciones(observaciones);
    saveOrderCommand.setObservacionesShopper(observacionesShopper);

    OrdenPago ordenPago = saveOrderCommand.execute();
    model.addAttribute("ordenPago", ordenPago);

    return "redirect:items/" + numeroOrden;
  }

  @RequestMapping(value="/{orderId}/item/{itemId}", method=RequestMethod.DELETE)
  public @ResponseBody boolean deleteItem(@PathVariable long orderId, @PathVariable long itemId) {
    removeItemOrderCommand.setNumero(orderId);
    removeItemOrderCommand.setItemId(itemId);
    return removeItemOrderCommand.execute();
  }

  @RequestMapping(value="asociarMedioPago", method=RequestMethod.POST)
  public @ResponseBody String asociarMedioPago(@ModelAttribute("model") final ModelMap model,
      long numeroOrden, int medioPagoId) {

    asociarMedioPagoCommand.setNumeroOrden(numeroOrden);
    asociarMedioPagoCommand.setMedioPagoId(medioPagoId);
    MedioPago medioPago = asociarMedioPagoCommand.execute();
    return medioPago.getDescription();
  }

  @RequestMapping(value="/pay/{orderId}")
  public String toPay(@ModelAttribute("model") final ModelMap model,
      @PathVariable long orderId) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);

    getOrderCommand.setNumero(orderId);
    OrdenPago ordenPago = getOrderCommand.execute();
    model.addAttribute("ordenPago", ordenPago);

    if (ordenPago.getTipoProveedor() == 1) {
      Shopper shopper = shopperRepository.get(ordenPago.getProveedor());
      model.addAttribute("titularNombre", shopper.getName());
    } else {
      Proveedor proveedor = proveedorRepository.get(ordenPago.getProveedor());
      model.addAttribute("titularNombre", proveedor.getDescription());
    }

    model.addAttribute("mediosPago", orderRepository.findMediosPago());
    return "payOrder";
  }

  @RequestMapping(value="/pay/{orderId}", method = RequestMethod.POST)
  public String payOrder(@ModelAttribute("model") final ModelMap model,
      @PathVariable long orderId, long medioPagoId,
      @RequestParam(required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date fechaCheque,
      @RequestParam(required = false) String numeroChequera,
      @RequestParam(required = false) String numeroCheque,
      @RequestParam(required = false) String transferId,
      String observaciones, String observacionesShopper) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);

    payOrderCommand.setNumero(orderId);
    payOrderCommand.setMedioPagoId(medioPagoId);
    payOrderCommand.setNumeroChequera(numeroChequera);
    payOrderCommand.setNumeroCheque(numeroCheque);
    payOrderCommand.setFechaCheque(fechaCheque);
    payOrderCommand.setIdTransferencia(transferId);
    payOrderCommand.setObservaciones(observaciones);
    payOrderCommand.setObservacionesShopper(observacionesShopper);
    OrdenPago ordenPago = payOrderCommand.execute();
    model.addAttribute("ordenPago", ordenPago);

    return "redirect:../view/" + orderId;
  }

  @RequestMapping(value="/transition/{orderId}", method = RequestMethod.POST)
  public @ResponseBody boolean transition(@ModelAttribute("model") final ModelMap model,
      @PathVariable long orderId, long stateId) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);

    transitionOrderCommand.setNumero(orderId);
    transitionOrderCommand.setStateId(stateId);
    transitionOrderCommand.execute();

    return true;
  }

  @RequestMapping(value="/view/{numeroOrden}")
  public String viewOrder(@ModelAttribute("model") final ModelMap model,
      @PathVariable long numeroOrden) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);

    getOrderCommand.setNumero(numeroOrden);
    OrdenPago ordenPago = getOrderCommand.execute();
    model.addAttribute("ordenPago", ordenPago);

    if (ordenPago.getTipoProveedor() == 1) {
      Shopper shopper = shopperRepository.get(ordenPago.getProveedor());
      model.addAttribute("titularNombre", shopper.getName());
    } else {
      Proveedor proveedor = proveedorRepository.get(ordenPago.getProveedor());
      model.addAttribute("titularNombre", proveedor.getDescription());
    }

    return "orderDetail";
  }

  @RequestMapping(value="getAsociacionMedioPago")
  public @ResponseBody AsociacionMedioPago getAsociacionMedioPago(
      @ModelAttribute("model") final ModelMap model,
      int tipoProveedor, int titularId) {

    AsociacionMedioPago asociacion = orderRepository.findAsociacion(
        tipoProveedor, titularId);
    return asociacion;
  }

  @RequestMapping(value="/search")
  public String search(@ModelAttribute("model") final ModelMap model,
      Long numeroOrden, Integer tipoTitular, Integer titularId,
      String dniShopper, String numeroCheque, Long estadoId) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);
    model.addAttribute("orderStates", orderRepository.findOrderStates());
    if (numeroOrden != null) {
      model.addAttribute("numeroOrden", numeroOrden);
    }
    model.addAttribute("tipoTitular", tipoTitular);
    if (titularId != null) {
      model.addAttribute("titularId", titularId);
      if (tipoTitular.equals(1)) {
        Shopper shopper = shopperRepository.get(titularId);
        dniShopper = shopper.getDni();
        model.addAttribute("titularNombre", shopper.getName());
      } else {
        Proveedor proveedor = proveedorRepository.get(titularId);
        model.addAttribute("titularNombre", proveedor.getDescription());
      }
    }
    if (numeroOrden != null) {
      model.addAttribute("numeroOrden", numeroOrden);
    }
    if (dniShopper != null && !dniShopper.isEmpty()) {
      model.addAttribute("dniShopper", dniShopper);
    }
    if (numeroCheque != null && !numeroCheque.isEmpty()) {
      model.addAttribute("numeroCheque", numeroCheque);
    }

    List<OrdenPago> ordenes = new ArrayList<OrdenPago>();
    if (numeroOrden != null) {
      OrdenPago ordenPago = orderRepository.get(numeroOrden);
      if (ordenPago != null) {
        ordenes.add(ordenPago);
      }
    } else if (titularId != null
        || (dniShopper != null && !dniShopper.isEmpty())
        || (numeroCheque != null && !numeroCheque.isEmpty())
        || estadoId != null) {
      OrderState estado = null;
      if (estadoId != null) {
        estado = orderRepository.getOrderState(estadoId);
        model.addAttribute("state", estadoId);
      }
      ordenes = orderRepository.findOrdenes(tipoTitular, titularId, dniShopper,
          numeroCheque, estado);
    }
    model.addAttribute("ordenes", ordenes);
    return "buscadorOrdenPago";
  }

  @RequestMapping(value="/caratula/{orderId}")
  public String getCaratula(@ModelAttribute("model") final ModelMap model,
      @PathVariable long orderId) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);
    Set<String> clients = new HashSet<String>();
    OrdenPago ordenPago = orderRepository.get(orderId);
    for (ItemOrden itemOrden : ordenPago.getItems()) {
      clients.add(itemOrden.getCliente());
    }
    model.addAttribute("ordenPago", ordenPago);
    model.addAttribute("clients", clients);
    if (ordenPago.getTipoProveedor() == 1) {
      Shopper shopper = shopperRepository.get(ordenPago.getProveedor());
      model.addAttribute("titularNombre", shopper.getName() + " "
          + shopper.getUsername());
    } else {
      Proveedor proveedor = proveedorRepository.get(ordenPago.getProveedor());
      model.addAttribute("titularNombre", proveedor.getDescription());
    }
    return "caratula";
  }

  @RequestMapping(value="/remito/{orderId}")
  public String getRemito(@ModelAttribute("model") final ModelMap model,
      @PathVariable long orderId) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);
    OrdenPago ordenPago = orderRepository.get(orderId);
    model.addAttribute("ordenPago", ordenPago);

    if (ordenPago.getTipoProveedor() == 1) {
      Shopper shopper = shopperRepository.get(ordenPago.getProveedor());
      model.addAttribute("titularNombre", shopper.getName());
    } else {
      Proveedor proveedor = proveedorRepository.get(ordenPago.getProveedor());
      model.addAttribute("titularNombre", proveedor.getDescription());
    }
    return "remito";
  }

  @RequestMapping(value="/printdetail/{orderId}")
  public String printDetail(@ModelAttribute("model") final ModelMap model,
      @PathVariable long orderId) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);
    OrdenPago ordenPago = orderRepository.get(orderId);
    for (ItemOrden itemOrden : ordenPago.getItems()) {
      Shopper itemShopper = shopperRepository.findByDni(itemOrden.getShopperDni());
      itemOrden.updateShopper(itemShopper);
    }
    model.addAttribute("ordenPago", ordenPago);

    if (ordenPago.getTipoProveedor() == 1) {
      Shopper shopper = shopperRepository.get(ordenPago.getProveedor());
      model.addAttribute("titularNombre", shopper.getName());
    } else {
      Proveedor proveedor = proveedorRepository.get(ordenPago.getProveedor());
      model.addAttribute("titularNombre", proveedor.getDescription());
    }
    return "printDetail";
  }

  @RequestMapping(value="/printshopper/{orderId}")
  public String printShopper(@ModelAttribute("model") final ModelMap model,
      @PathVariable long orderId) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);
    OrdenPago ordenPago = orderRepository.get(orderId);
    for (ItemOrden itemOrden : ordenPago.getItems()) {
      Shopper itemShopper = shopperRepository.findByDni(itemOrden.getShopperDni());
      itemOrden.updateShopper(itemShopper);
    }
    model.addAttribute("ordenPago", ordenPago);

    return "printShopper";
  }

  @RequestMapping(value="/export")
  public String export(@ModelAttribute("model") final ModelMap model) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);
    return "exportOrden";
  }

  @RequestMapping(value="/download")
  public void download(HttpServletResponse response, Date desde, Date hasta) {
    String fileName = "ordenesDePago.xls";
    response.setContentType("application/vnd.openxmlformats-officedocument."
        + "spreadsheetml.sheet");
    String headerKey = "Content-Disposition";
    String headerValue = String.format("attachment; filename=\"%s\"",
            fileName);
    response.setHeader(headerKey, headerValue);

    try {
      importService.exportOrdenes(response.getOutputStream(), desde, hasta);
    } catch (IOException e) {
      throw new RuntimeException("Cannot write the XLS file", e);
    }
  }
}
