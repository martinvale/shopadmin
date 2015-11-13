package com.ibiscus.shopnchek.web.controller.site;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibiscus.shopnchek.application.debt.VisitaDto;
import com.ibiscus.shopnchek.application.debt.VisitasDto;
import com.ibiscus.shopnchek.application.order.ItemsOrdenService;
import com.ibiscus.shopnchek.application.order.UpdateItemOrderCommand;
import com.ibiscus.shopnchek.application.shopmetrics.ImportService;
import com.ibiscus.shopnchek.domain.admin.Adicional;
import com.ibiscus.shopnchek.domain.admin.ItemOrden;
import com.ibiscus.shopnchek.domain.admin.ItemOrderRepository;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;
import com.ibiscus.shopnchek.domain.admin.TipoPago;
import com.ibiscus.shopnchek.domain.admin.Visita;
import com.ibiscus.shopnchek.domain.security.User;
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

  /** Repository of items of the orders. */
  @Autowired
  private ItemOrderRepository itemOrderRepository;

  /** Repository of users. */
  @Autowired
  private UserRepository userRepository;

  /** Repository of shoppers. */
  @Autowired
  private ShopperRepository shopperRepository;

  /** Service to import external data. */
  @Autowired
  private ImportService importService;

  @Autowired
  private UpdateItemOrderCommand updateItemOrderCommand;

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

  @RequestMapping(value="/deuda/{dniShopper}")
  public @ResponseBody List<Visita> listarDeuda(
      @ModelAttribute("model") final ModelMap model,
      @PathVariable String dniShopper,
      @RequestParam(defaultValue = "false") Boolean includeIplan,
      @RequestParam(defaultValue = "false") Boolean includeMcd,
      @RequestParam(defaultValue = "false") Boolean includeGac,
      @RequestParam(defaultValue = "false") Boolean includeAdicionales,
      @RequestParam(defaultValue = "false") Boolean includeShopmetrics,
      Boolean applyDate, @DateTimeFormat(pattern="dd/MM/yyyy") Date desde,
      @DateTimeFormat(pattern="dd/MM/yyyy") Date hasta) {
    if (!applyDate) {
      desde = null;
      hasta = null;
    }
    List<Visita> items = itemsOrdenService
        .getDeudaShopper(dniShopper, includeIplan, includeMcd, includeGac,
            includeAdicionales, includeShopmetrics, desde, hasta);
    return items;
  }

  @RequestMapping(value="/debtReportFull")
  public void getDebtReportFull(HttpServletResponse response) {
    String fileName = "deudafull.xlsx";
    response.setContentType("application/vnd.openxmlformats-officedocument."
        + "spreadsheetml.sheet");
    String headerKey = "Content-Disposition";
    String headerValue = String.format("attachment; filename=\"%s\"",
            fileName);
    response.setHeader(headerKey, headerValue);

    try {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2006);
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date from = calendar.getTime();
	
		calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date to = calendar.getTime();
	    importService.reportDeuda(response.getOutputStream(), from, to);
    } catch (IOException e) {
    	throw new RuntimeException("Cannot write the XLS file", e);
    }
  }

  @RequestMapping(value="/debtReport")
  public void getDebtReport(HttpServletResponse response) {
    String fileName = "deuda.xlsx";
    response.setContentType("application/vnd.openxmlformats-officedocument."
        + "spreadsheetml.sheet");
    String headerKey = "Content-Disposition";
    String headerValue = String.format("attachment; filename=\"%s\"",
            fileName);
    response.setHeader(headerKey, headerValue);

    try {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2014);
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date from = calendar.getTime();
	
		calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date to = calendar.getTime();
	    importService.reportDeuda(response.getOutputStream(), from, to);
    } catch (IOException e) {
    	throw new RuntimeException("Cannot write the XLS file", e);
    }
  }

  @RequestMapping(value="/adicionalesPagosReport")
  public void getAdicionalesPagosReport(HttpServletResponse response) {
    String fileName = "adicionalesPagos.xlsx";
    response.setContentType("application/vnd.openxmlformats-officedocument."
        + "spreadsheetml.sheet");
    String headerKey = "Content-Disposition";
    String headerValue = String.format("attachment; filename=\"%s\"",
            fileName);
    response.setHeader(headerKey, headerValue);

    try {
		/*Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2014);
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date from = calendar.getTime();
	
		calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date to = calendar.getTime();*/
	    importService.reportAdicionales(response.getOutputStream());
    } catch (IOException e) {
    	throw new RuntimeException("Cannot write the XLS file", e);
    }
  }

  @RequestMapping(value="/exportDeuda")
  public void exportDeuda(HttpServletResponse response,
      String dniShopper,
      @RequestParam(defaultValue = "false") Boolean includeMcd,
      @RequestParam(defaultValue = "false") Boolean includeGac,
      @RequestParam(defaultValue = "false") Boolean includeAdicionales,
      @RequestParam(defaultValue = "false") Boolean includeShopmetrics,
      Boolean applyDate, Date desde, Date hasta) {
    String fileName = "deudaShopper.xlsx";
    response.setContentType("application/vnd.openxmlformats-officedocument."
        + "spreadsheetml.sheet");
    String headerKey = "Content-Disposition";
    String headerValue = String.format("attachment; filename=\"%s\"",
            fileName);
    response.setHeader(headerKey, headerValue);

    try {
      importService.exportDeuda(response.getOutputStream(), dniShopper,
          includeMcd, includeGac, includeAdicionales, includeShopmetrics, desde,
          hasta);
    } catch (IOException e) {
      throw new RuntimeException("Cannot write the XLS file", e);
    }
  }

  @RequestMapping(value="/debtSearch")
  public String listDebt(@ModelAttribute("model") final ModelMap model) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, -4);
    model.addAttribute("fechaDesde", calendar.getTime());
    return "buscadorDeuda";
  }

  @RequestMapping(value="/printDebt")
  public String printDebt(
      @ModelAttribute("model") final ModelMap model,
      String dniShopper,
      Boolean includeMcd,
      Boolean includeGac,
      Boolean includeAdicionales,
      Boolean includeShopmetrics,
      Boolean applyDate, Date desde, Date hasta) {
    if (!applyDate) {
      desde = null;
      hasta = null;
    }
    List<Visita> items = itemsOrdenService
        .getDeudaShopper(dniShopper, false, includeMcd, includeGac,
            includeAdicionales, includeShopmetrics, desde, hasta);
    model.addAttribute("items", items);
    return "printDebt";
  }

  /*@RequestMapping(value="/create", method = RequestMethod.POST)
  public @ResponseBody Boolean create(
      @ModelAttribute("model") final ModelMap model, final long ordenNro,
      @ModelAttribute("visitas") VisitasDto visitas) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    com.ibiscus.shopnchek.domain.security.User autorizador;
    autorizador = userRepository.findByUsername(user.getUsername());
    OrdenPago ordenPago = orderRepository.get(ordenNro);
    for (VisitaDto visita : visitas.getVisitas()) {
      Shopper shopper = shopperRepository.findByDni(visita.getShopperDni());
      TipoPago unTipoPago = orderRepository.getTipoPago(visita.getTipoPago());
      Long newId = itemsOrdenService.getItemOrdenId();
      ItemOrden itemOrden = new ItemOrden(newId, ordenPago, autorizador.getId(),
          visita.getShopperDni(), visita.getAsignacion(), visita.getTipoItem(),
          unTipoPago, visita.getCliente(), visita.getSucursal(),
          visita.getMes(), visita.getAnio(), visita.getFecha(),
          visita.getImporte(), null, 1);
      itemOrden.updateShopper(shopper);
      orderRepository.saveItem(itemOrden);
    }
    return Boolean.TRUE;
  }*/

  @RequestMapping(value="/createVisitas", method = RequestMethod.POST)
  public @ResponseBody Boolean createVisitas(@RequestBody VisitasDto visitas) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    /*com.ibiscus.shopnchek.domain.security.User autorizador;
    autorizador = userRepository.findByUsername(user.getUsername());
    OrdenPago ordenPago = orderRepository.get(visitas.getNroOrden());
    for (VisitaDto visita : visitas.getItems()) {
      Shopper shopper = shopperRepository.findByDni(visita.getShopperDni());
      TipoPago unTipoPago = orderRepository.getTipoPago(visita.getTipoPago());
      Long newId = itemsOrdenService.getItemOrdenId();
      String cliente = visita.getCliente();
      if (cliente != null && cliente.length() > ItemOrden.CLIENTE_FIELD_LENGTH) {
        cliente = cliente.substring(0, ItemOrden.CLIENTE_FIELD_LENGTH);
      }
      String sucursal = visita.getSucursal();
      if (sucursal != null
          && sucursal.length() > ItemOrden.SUCURSAL_FIELD_LENGTH) {
        sucursal = sucursal.substring(0, ItemOrden.SUCURSAL_FIELD_LENGTH);
      }
      String description = null;
      if (visita.getTipoItem() == 3) {
        description = itemsOrdenService.getObservacionAdicional(
            visita.getAsignacion());
      }
      ItemOrden itemOrden = new ItemOrden(newId, ordenPago, autorizador.getId(),
          visita.getShopperDni(), visita.getAsignacion(), visita.getTipoItem(),
          unTipoPago, cliente, sucursal, visita.getMes(), visita.getAnio(),
          visita.getFecha(), visita.getImporte(), description, 1);
      itemOrden.updateShopper(shopper);
      orderRepository.saveItem(itemOrden);
      if (visita.getTipoItem() == 3) {
        itemsOrdenService.linkAdicional(visita.getAsignacion(),
            ordenPago.getNumero());
      }
    }*/
    return Boolean.TRUE;
  }

  @RequestMapping(value="/create", method = RequestMethod.POST)
  public @ResponseBody Boolean create(
      @ModelAttribute("model") final ModelMap model,
      long ordenNro, int tipoPago, String shopperDni, Integer asignacion,
      double importe, String cliente, String sucursal, int mes, int anio,
      String fecha, int tipoItem) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    System.out.println("general [" + tipoPago + "]: " + importe);
    com.ibiscus.shopnchek.domain.security.User autorizador;
    autorizador = userRepository.findByUsername(user.getUsername());
    Shopper shopper = shopperRepository.findByDni(shopperDni);
    OrdenPago ordenPago = orderRepository.get(ordenNro);
    TipoPago unTipoPago = orderRepository.getTipoPago(tipoPago);
    Long newId = itemsOrdenService.getItemOrdenId();
    if (cliente != null && cliente.length() > ItemOrden.CLIENTE_FIELD_LENGTH) {
      cliente = cliente.substring(0, ItemOrden.CLIENTE_FIELD_LENGTH);
    }
    if (sucursal != null
        && sucursal.length() > ItemOrden.SUCURSAL_FIELD_LENGTH) {
      sucursal = sucursal.substring(0, ItemOrden.SUCURSAL_FIELD_LENGTH);
    }
    ItemOrden itemOrden = new ItemOrden(newId, ordenPago, autorizador.getId(),
        shopperDni, asignacion, tipoItem, unTipoPago,
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
      String fecha) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    System.out.println("adicional [" + tipoPago + "]: " + importe);
    com.ibiscus.shopnchek.domain.security.User autorizador;
    autorizador = userRepository.findByUsername(user.getUsername());
    Shopper shopper = shopperRepository.findByDni(shopperDni);
    OrdenPago ordenPago = orderRepository.get(ordenNro);
    TipoPago unTipoPago = orderRepository.getTipoPago(tipoPago);
    String descripcion = itemsOrdenService.getObservacionAdicional(asignacion);
    Long newId = itemsOrdenService.getItemOrdenId();
    if (cliente != null && cliente.length() > ItemOrden.CLIENTE_FIELD_LENGTH) {
      cliente = cliente.substring(0, ItemOrden.CLIENTE_FIELD_LENGTH);
    }
    if (sucursal != null
        && sucursal.length() > ItemOrden.SUCURSAL_FIELD_LENGTH) {
      sucursal = sucursal.substring(0, ItemOrden.SUCURSAL_FIELD_LENGTH);
    }

    ItemOrden itemOrden = new ItemOrden(newId, ordenPago, autorizador.getId(),
        shopperDni, asignacion, 3, unTipoPago,
        cliente, sucursal, mes, anio, fecha, importe, descripcion, 1);
    itemOrden.updateShopper(shopper);
    orderRepository.saveItem(itemOrden);
    itemsOrdenService.linkAdicional(asignacion, ordenNro);
    return Boolean.TRUE;
  }

  @RequestMapping(value="/search")
  public String list(@ModelAttribute("model") final ModelMap model) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);
    model.addAttribute("items", new ArrayList<ItemOrden>());
    return "buscadorItems";
  }

  @RequestMapping(value="/search", method = RequestMethod.POST)
  public String search(@ModelAttribute("model") final ModelMap model,
      String shopperDni, String shopper) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);
    model.addAttribute("shopper", shopper);
    model.addAttribute("shopperDni", shopperDni);
    List<ItemOrden> items = null;
    if (shopperDni != null) {
      items = itemOrderRepository.find(shopperDni);
    } else {
      items = new ArrayList<ItemOrden>();
    }
    model.addAttribute("items", items);
    return "buscadorItems";
  }

  @RequestMapping(value="/updateImporte", method = RequestMethod.POST)
  public @ResponseBody Boolean updateImporte(@ModelAttribute("model") final ModelMap model,
      long itemId, double importe) {
    updateItemOrderCommand.setItemOrderId(itemId);
    updateItemOrderCommand.setImporte(importe);
    return updateItemOrderCommand.execute();
  }
}
