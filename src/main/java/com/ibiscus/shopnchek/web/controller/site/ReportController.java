package com.ibiscus.shopnchek.web.controller.site;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibiscus.shopnchek.application.report.ReportService;
import com.ibiscus.shopnchek.domain.util.Row;

@Controller
@RequestMapping("/report")
public class ReportController {

  /** Service to request reports. */
  @Autowired
  private ReportService reportsService;

  @RequestMapping(value="/summary")
  public String summary(@ModelAttribute("model") final ModelMap model,
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

      rows = reportsService.getSummaryReport(desde, hasta);
    }

    model.addAttribute("rows", rows);
    return "summary";
  }

  @RequestMapping(value="/printSummary")
  public String printSummary(@ModelAttribute("model") final ModelMap model,
      Integer anioDesde, Integer mesDesde, Integer anioHasta,
      Integer mesHasta) {
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

    model.addAttribute("rows", reportsService.getSummaryReport(desde, hasta));
    return "printSummary";
  }

}
