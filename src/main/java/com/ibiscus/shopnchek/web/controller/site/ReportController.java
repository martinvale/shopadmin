package com.ibiscus.shopnchek.web.controller.site;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.ibiscus.shopnchek.application.debt.DebtService;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;
import com.ibiscus.shopnchek.domain.debt.Client;
import com.ibiscus.shopnchek.domain.debt.ClientRepository;
import com.ibiscus.shopnchek.domain.report.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

    /**
     * Service to request reports.
     */
    @Autowired
    private ReportService reportsService;

    @Autowired
    private DebtRepository debtRepository;

    @Autowired
    private DebtService debtService;

    @Autowired
    private ShopperRepository shopperRepository;

    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping(value = "/debtSummary")
    public String debtSummary(@ModelAttribute("model") final ModelMap model,
                              @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date desde,
                              @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date hasta,
                              @RequestParam(required = false) boolean includeEmpresa,
                              @RequestParam(required = false) boolean includeShopper) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        model.addAttribute("user", user);

        Report report = null;
        if (desde != null && hasta != null) {
            model.addAttribute("includeEmpresa", includeEmpresa);
            model.addAttribute("includeShopper", includeShopper);

            model.addAttribute("desde", desde);
            model.addAttribute("hasta", hasta);

            report = reportsService.getGeneralDebtReport(desde, hasta, includeEmpresa, includeShopper);
        }

        model.addAttribute("report", report);
        model.addAttribute("form", "debtSummary");
        model.addAttribute("title", "Resumen de deuda real");
        model.addAttribute("titulo1", "Produccion");
        model.addAttribute("total1", "Total Produccion");
        model.addAttribute("titulo2", "Pagado");
        model.addAttribute("total2", "Total Pagado");
        model.addAttribute("titulo3", "Deuda Real");
        model.addAttribute("total3", "Total Deuda");
        return "debtSummary";
    }

    @RequestMapping(value = "/presentedDebtSummary")
    public String presentedDebtSummary(@ModelAttribute("model") final ModelMap model,
                              @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date desde,
                              @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date hasta,
                              @RequestParam(required = false) boolean includeEmpresa,
                              @RequestParam(required = false) boolean includeShopper) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        model.addAttribute("user", user);

        Report report = null;
        if (desde != null && hasta != null) {
            model.addAttribute("includeEmpresa", includeEmpresa);
            model.addAttribute("includeShopper", includeShopper);

            model.addAttribute("desde", desde);
            model.addAttribute("hasta", hasta);

            report = reportsService.getPresentedDebtReport(desde, hasta, includeEmpresa, includeShopper);
        }

        model.addAttribute("report", report);
        model.addAttribute("form", "presentedDebtSummary");
        model.addAttribute("title", "Resumen de deuda de operaciones generadas");
        model.addAttribute("titulo1", "Presentado (OP Generadas)");
        model.addAttribute("total1", "Total Presentado");
        model.addAttribute("titulo2", "Pagado");
        model.addAttribute("total2", "Total Pagado");
        model.addAttribute("titulo3", "Deuda (OP Generadas)");
        model.addAttribute("total3", "Total Deuda");
        return "debtSummary";
    }

    @RequestMapping(value = "/pendingDebtSummary")
    public String pendingDebtSummary(@ModelAttribute("model") final ModelMap model,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date desde,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date hasta,
                                     @RequestParam(required = false) boolean includeEmpresa,
                                     @RequestParam(required = false) boolean includeShopper) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        model.addAttribute("user", user);

        Report report = null;
        if (desde != null && hasta != null) {
            model.addAttribute("includeEmpresa", includeEmpresa);
            model.addAttribute("includeShopper", includeShopper);

            model.addAttribute("desde", desde);
            model.addAttribute("hasta", hasta);

            report = reportsService.getPendingDebtReport(desde, hasta, includeEmpresa, includeShopper);
        }

        model.addAttribute("report", report);
        model.addAttribute("form", "pendingDebtSummary");
        model.addAttribute("title", "Resumen de deuda no presentada");
        model.addAttribute("titulo1", "Produccion");
        model.addAttribute("total1", "Total Produccion");
        model.addAttribute("titulo2", "Presentado (OP Generadas)");
        model.addAttribute("total2", "Total Presentado");
        model.addAttribute("titulo3", "Deuda No Presentada");
        model.addAttribute("total3", "Total Deuda");
        return "debtSummary";
    }

    @RequestMapping(value = "/additional")
    public String additionalSummary(@ModelAttribute("model") final ModelMap model,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date desde,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date hasta) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        model.addAttribute("user", user);

        if (desde != null && hasta != null) {
            model.addAttribute("desde", desde);
            model.addAttribute("hasta", hasta);

            List<Row> report = reportsService.getAdditionalReport(desde, hasta);
            model.addAttribute("report", report);
        }

        return "additionalSummary";
    }

    @RequestMapping(value = "/debtDetails")
    public String getDebtDetails(@ModelAttribute("model") final ModelMap model,
                                 String shopperDni, Long clientId,
                                 @DateTimeFormat(pattern = "dd/MM/yyyy") Date desde,
                                 @DateTimeFormat(pattern = "dd/MM/yyyy") Date hasta) {
        Shopper shopper = shopperRepository.findByDni(shopperDni);
        model.addAttribute("shopper", shopper);
        Client client = clientRepository.get(clientId);
        model.addAttribute("client", client);
        model.addAttribute("items", debtService.getDebt(shopperDni, client, desde, hasta));

        return "debtDetails";
    }

    @RequestMapping(value = "/prodSummary")
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

    @RequestMapping(value = "/prodSummary2")
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

    @RequestMapping(value = "/printProdSummary")
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

    @RequestMapping(value = "/paySummary2")
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

    @RequestMapping(value = "/paySummary")
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

    @RequestMapping(value = "/printPaySummary")
    public String printPaySummary(@ModelAttribute("model") final ModelMap model,
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
