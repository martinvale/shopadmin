package com.ibiscus.shopnchek.web.controller.site;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibiscus.shopnchek.application.debt.GetDebtSummaryCommand;
import com.ibiscus.shopnchek.application.report.ReportService;
import com.ibiscus.shopnchek.domain.debt.Debt.State;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;
import com.ibiscus.shopnchek.domain.security.User;
import com.ibiscus.shopnchek.domain.util.Row;

@Controller
@RequestMapping("/report")
public class ReportController {

  /** Service to request reports. */
  @Autowired
  private ReportService reportsService;

  @Autowired
  private DebtRepository debtRepository;

  @RequestMapping(value="/debtSummary2")
  public String debtSummary2(@ModelAttribute("model") final ModelMap model,
      @RequestParam(required = false) Integer anioDesde,
      @RequestParam(required = false) Integer mesDesde,
      @RequestParam(required = false) Integer anioHasta,
      @RequestParam(required = false) Integer mesHasta,
      @RequestParam(required = false) boolean includeEmpresa) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);

    List<Row> rows = new LinkedList<Row>();
    if (anioDesde != null && mesDesde != null && anioHasta != null
        && mesHasta != null) {
      model.addAttribute("includeEmpresa", includeEmpresa);

      model.addAttribute("anioDesde", anioDesde);
      model.addAttribute("mesDesde", mesDesde);
      model.addAttribute("anioHasta", anioHasta);
      model.addAttribute("mesHasta", mesHasta);

      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.YEAR, anioDesde);
      calendar.set(Calendar.MONTH, mesDesde - 1);
      calendar.set(Calendar.DAY_OF_MONTH, 1);
      calendar.set(Calendar.HOUR, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      Date desde = calendar.getTime();

      calendar.set(Calendar.YEAR, anioHasta);
      calendar.set(Calendar.MONTH, mesHasta - 1);
      calendar.add(Calendar.MONTH, 1);
      calendar.add(Calendar.DAY_OF_MONTH, -1);
      Date hasta = calendar.getTime();

      rows = reportsService.getDebtSummaryReport(desde, hasta,
          false, true, true, true, includeEmpresa);
    }

    model.addAttribute("rows", rows);
    return "debtSummary2";
  }

  @RequestMapping(value="/debtSummary")
  public String debtSummary(@ModelAttribute("model") final ModelMap model,
      @RequestParam(required = false) Integer anioDesde,
      @RequestParam(required = false) Integer mesDesde,
      @RequestParam(required = false) Integer anioHasta,
      @RequestParam(required = false) Integer mesHasta,
      @RequestParam(required = false) boolean includeEmpresa) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);

    List<Row> rows = new LinkedList<Row>();
    if (anioDesde != null && mesDesde != null && anioHasta != null
        && mesHasta != null) {
      model.addAttribute("includeEmpresa", includeEmpresa);

      model.addAttribute("anioDesde", anioDesde);
      model.addAttribute("mesDesde", mesDesde);
      model.addAttribute("anioHasta", anioHasta);
      model.addAttribute("mesHasta", mesHasta);

      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.YEAR, anioDesde);
      calendar.set(Calendar.MONTH, mesDesde - 1);
      calendar.set(Calendar.DAY_OF_MONTH, 1);
      calendar.set(Calendar.HOUR, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      Date desde = calendar.getTime();

      calendar.set(Calendar.YEAR, anioHasta);
      calendar.set(Calendar.MONTH, mesHasta - 1);
      calendar.add(Calendar.MONTH, 1);
      calendar.add(Calendar.DAY_OF_MONTH, -1);
      Date hasta = calendar.getTime();

      GetDebtSummaryCommand command = new GetDebtSummaryCommand(debtRepository);
      command.setFrom(desde);
      command.setTo(hasta);
      List<State> states = new ArrayList<State>();
      states.add(State.pendiente);
      states.add(State.asignada);
      command.setStates(states);
      if (includeEmpresa) {
        command.addGroupBy("clients.name");
      }
      rows = command.execute();
    }

    model.addAttribute("rows", rows);
    return "debtSummary";
  }

  @RequestMapping(value="/printDebtSummary")
  public String printDebtSummary(@ModelAttribute("model") final ModelMap model,
      Integer anioDesde, Integer mesDesde, Integer anioHasta,
      Integer mesHasta, boolean includeEmpresa) {
    model.addAttribute("includeEmpresa", includeEmpresa);

    List<Row> rows = new LinkedList<Row>();
    if (anioDesde != null && mesDesde != null && anioHasta != null
        && mesHasta != null) {
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.YEAR, anioDesde);
      calendar.set(Calendar.MONTH, mesDesde - 1);
      calendar.set(Calendar.DAY_OF_MONTH, 1);
      calendar.set(Calendar.HOUR, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      Date desde = calendar.getTime();

      calendar.set(Calendar.YEAR, anioHasta);
      calendar.set(Calendar.MONTH, mesHasta - 1);
      calendar.add(Calendar.MONTH, 1);
      calendar.add(Calendar.DAY_OF_MONTH, -1);
      Date hasta = calendar.getTime();

      GetDebtSummaryCommand command = new GetDebtSummaryCommand(debtRepository);
      command.setFrom(desde);
      command.setTo(hasta);
      List<State> states = new ArrayList<State>();
      states.add(State.pendiente);
      states.add(State.asignada);
      command.setStates(states);
      if (includeEmpresa) {
        command.addGroupBy("clients.name");
      }
      rows = command.execute();
    }

    model.addAttribute("rows", rows);
    model.addAttribute("title", "Resumen de Deuda");
    return "printSummary";
  }

  @RequestMapping(value="/prodSummary")
  public String prodSummary(@ModelAttribute("model") final ModelMap model,
      @RequestParam(required = false) Integer anioDesde,
      @RequestParam(required = false) Integer mesDesde,
      @RequestParam(required = false) Integer anioHasta,
      @RequestParam(required = false) Integer mesHasta,
      @RequestParam(required = false) boolean includeEmpresa) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);

    List<Row> rows = new LinkedList<Row>();
    if (anioDesde != null && mesDesde != null && anioHasta != null
        && mesHasta != null) {
      model.addAttribute("includeEmpresa", includeEmpresa);

      model.addAttribute("anioDesde", anioDesde);
      model.addAttribute("mesDesde", mesDesde);
      model.addAttribute("anioHasta", anioHasta);
      model.addAttribute("mesHasta", mesHasta);

      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.YEAR, anioDesde);
      calendar.set(Calendar.MONTH, mesDesde - 1);
      calendar.set(Calendar.DAY_OF_MONTH, 1);
      calendar.set(Calendar.HOUR, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      Date desde = calendar.getTime();

      calendar.set(Calendar.YEAR, anioHasta);
      calendar.set(Calendar.MONTH, mesHasta - 1);
      calendar.add(Calendar.MONTH, 1);
      calendar.add(Calendar.DAY_OF_MONTH, -1);
      Date hasta = calendar.getTime();

      GetDebtSummaryCommand command = new GetDebtSummaryCommand(debtRepository);
      command.setFrom(desde);
      command.setTo(hasta);
      command.setStates(null);
      if (includeEmpresa) {
        command.addGroupBy("clients.name");
      }
      rows = command.execute();
    }

    model.addAttribute("rows", rows);
    return "prodSummary";
  }

  @RequestMapping(value="/prodSummary2")
  public String prodSummary2(@ModelAttribute("model") final ModelMap model,
      @RequestParam(required = false) Integer anioDesde,
      @RequestParam(required = false) Integer mesDesde,
      @RequestParam(required = false) Integer anioHasta,
      @RequestParam(required = false) Integer mesHasta,
      @RequestParam(required = false) boolean includeEmpresa) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);

    List<Row> rows = new LinkedList<Row>();
    if (anioDesde != null && mesDesde != null && anioHasta != null
        && mesHasta != null) {
      model.addAttribute("includeEmpresa", includeEmpresa);

      model.addAttribute("anioDesde", anioDesde);
      model.addAttribute("mesDesde", mesDesde);
      model.addAttribute("anioHasta", anioHasta);
      model.addAttribute("mesHasta", mesHasta);

      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.YEAR, anioDesde);
      calendar.set(Calendar.MONTH, mesDesde - 1);
      calendar.set(Calendar.DAY_OF_MONTH, 1);
      calendar.set(Calendar.HOUR, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      Date desde = calendar.getTime();

      calendar.set(Calendar.YEAR, anioHasta);
      calendar.set(Calendar.MONTH, mesHasta - 1);
      calendar.add(Calendar.MONTH, 1);
      calendar.add(Calendar.DAY_OF_MONTH, -1);
      Date hasta = calendar.getTime();
      rows = reportsService.getProdSummaryReport(desde, hasta,
          true, true, true, true, includeEmpresa);
    }

    model.addAttribute("rows", rows);
    return "prodSummary2";
  }

  @RequestMapping(value="/printProdSummary")
  public String printProdSummary(@ModelAttribute("model") final ModelMap model,
      Integer anioDesde, Integer mesDesde, Integer anioHasta,
      Integer mesHasta, boolean includeEmpresa) {
    model.addAttribute("includeEmpresa", includeEmpresa);

    List<Row> rows = new LinkedList<Row>();
    if (anioDesde != null && mesDesde != null && anioHasta != null
        && mesHasta != null) {
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.YEAR, anioDesde);
      calendar.set(Calendar.MONTH, mesDesde - 1);
      calendar.set(Calendar.DAY_OF_MONTH, 1);
      calendar.set(Calendar.HOUR, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      Date desde = calendar.getTime();

      calendar.set(Calendar.YEAR, anioHasta);
      calendar.set(Calendar.MONTH, mesHasta - 1);
      calendar.add(Calendar.MONTH, 1);
      calendar.add(Calendar.DAY_OF_MONTH, -1);
      Date hasta = calendar.getTime();

      GetDebtSummaryCommand command = new GetDebtSummaryCommand(debtRepository);
      command.setFrom(desde);
      command.setTo(hasta);
      command.setStates(null);
      if (includeEmpresa) {
        command.addGroupBy("clients.name");
      }
      rows = command.execute();
    }

    model.addAttribute("rows", rows);
    model.addAttribute("title", "Resumen de Producción");
    return "printSummary";
  }

  @RequestMapping(value="/paySummary2")
  public String paySummary2(@ModelAttribute("model") final ModelMap model,
      @RequestParam(required = false) Integer anioDesde,
      @RequestParam(required = false) Integer mesDesde,
      @RequestParam(required = false) Integer anioHasta,
      @RequestParam(required = false) Integer mesHasta) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);

    List<Row> rows = new LinkedList<Row>();
    if (anioDesde != null && mesDesde != null && anioHasta != null
        && mesHasta != null) {
      model.addAttribute("anioDesde", anioDesde);
      model.addAttribute("mesDesde", mesDesde);
      model.addAttribute("anioHasta", anioHasta);
      model.addAttribute("mesHasta", mesHasta);

      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.YEAR, anioDesde);
      calendar.set(Calendar.MONTH, mesDesde - 1);
      calendar.set(Calendar.DAY_OF_MONTH, 1);
      calendar.set(Calendar.HOUR, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      Date desde = calendar.getTime();

      calendar.set(Calendar.YEAR, anioHasta);
      calendar.set(Calendar.MONTH, mesHasta - 1);
      calendar.add(Calendar.MONTH, 1);
      calendar.add(Calendar.DAY_OF_MONTH, -1);
      Date hasta = calendar.getTime();

      GetDebtSummaryCommand command = new GetDebtSummaryCommand(debtRepository);
      command.setFrom(desde);
      command.setTo(hasta);
      List<State> states = new ArrayList<State>();
      states.add(State.pagada);
      command.setStates(states);
      rows = command.execute();
    }

    model.addAttribute("rows", rows);
    return "paySummary";
  }

  @RequestMapping(value="/paySummary")
  public String paySummary(@ModelAttribute("model") final ModelMap model,
      @RequestParam(required = false) Integer anioDesde,
      @RequestParam(required = false) Integer mesDesde,
      @RequestParam(required = false) Integer anioHasta,
      @RequestParam(required = false) Integer mesHasta) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);

    List<Row> rows = new LinkedList<Row>();
    if (anioDesde != null && mesDesde != null && anioHasta != null
        && mesHasta != null) {
      model.addAttribute("anioDesde", anioDesde);
      model.addAttribute("mesDesde", mesDesde);
      model.addAttribute("anioHasta", anioHasta);
      model.addAttribute("mesHasta", mesHasta);

      SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
      String fechaDesde = "01-" + mesDesde + "-" + anioDesde;
      String fechaHasta = "01-" + (mesHasta + 1) + "-" + anioHasta;
      Date desde;
      Date hasta;
      try {
        desde = dateFormat.parse(fechaDesde);
        hasta = dateFormat.parse(fechaHasta);
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }

      Calendar calendar = Calendar.getInstance();
      calendar.setTime(hasta);
      calendar.add(Calendar.DAY_OF_MONTH, -1);
      hasta = calendar.getTime();

      if (mesHasta < 12) {
        mesHasta++;
      } else {
        mesHasta = 1;
        anioHasta++;
      }
      rows = reportsService.getPaySummaryReport(desde, hasta);
    }

    model.addAttribute("rows", rows);
    return "paySummary";
  }

  @RequestMapping(value="/printPaySummary")
  public String printPaySummary(@ModelAttribute("model") final ModelMap model,
      Integer anioDesde, Integer mesDesde, Integer anioHasta,
      Integer mesHasta) {
/*    List<Row> rows = new LinkedList<Row>();
    if (anioDesde != null && mesDesde != null && anioHasta != null
        && mesHasta != null) {
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.YEAR, anioDesde);
      calendar.set(Calendar.MONTH, mesDesde - 1);
      calendar.set(Calendar.DAY_OF_MONTH, 1);
      calendar.set(Calendar.HOUR, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      Date desde = calendar.getTime();

      calendar.set(Calendar.YEAR, anioHasta);
      calendar.set(Calendar.MONTH, mesHasta - 1);
      calendar.add(Calendar.MONTH, 1);
      calendar.add(Calendar.DAY_OF_MONTH, -1);
      Date hasta = calendar.getTime();

      GetDebtSummaryCommand command = new GetDebtSummaryCommand(debtRepository);
      command.setFrom(desde);
      command.setTo(hasta);
      List<State> states = new ArrayList<State>();
      states.add(State.pagada);
      command.setStates(states);
      rows = command.execute();
    }

    model.addAttribute("rows", rows);
    model.addAttribute("title", "Resumen de Pagos");
    return "printSummary";*/

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    String fechaDesde = "01-" + mesDesde + "-" + anioDesde;
    String fechaHasta = "01-" + (mesHasta + 1) + "-" + anioHasta;
    Date desde;
    Date hasta;
    try {
      desde = dateFormat.parse(fechaDesde);
      hasta = dateFormat.parse(fechaHasta);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(hasta);
    calendar.add(Calendar.DAY_OF_MONTH, -1);
    hasta = calendar.getTime();

    if (mesHasta < 12) {
      mesHasta++;
    } else {
      mesHasta = 1;
      anioHasta++;
    }
    model.addAttribute("rows", reportsService.getPaySummaryReport(desde,
        hasta));
    model.addAttribute("title", "Resumen de Pagos");
    return "printSummary";
  }

}
