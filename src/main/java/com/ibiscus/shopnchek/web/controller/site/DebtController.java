package com.ibiscus.shopnchek.web.controller.site;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibiscus.shopnchek.application.ResultSet;
import com.ibiscus.shopnchek.application.debt.AssignDebtCommand;
import com.ibiscus.shopnchek.application.debt.DebtDto;
import com.ibiscus.shopnchek.application.debt.ExportDebtCommand;
import com.ibiscus.shopnchek.application.debt.GetDebtCommand;
import com.ibiscus.shopnchek.application.debt.SaveDebtCommand;
import com.ibiscus.shopnchek.application.debt.SearchDebtCommand;
import com.ibiscus.shopnchek.application.debt.SearchDebtDtoCommand;
import com.ibiscus.shopnchek.domain.debt.BranchRepository;
import com.ibiscus.shopnchek.domain.debt.ClientRepository;
import com.ibiscus.shopnchek.domain.debt.Debt;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;
import com.ibiscus.shopnchek.domain.debt.TipoPago;
import com.ibiscus.shopnchek.domain.security.User;

@Controller
@RequestMapping("/debt")
public class DebtController {

	@Autowired
	private DebtRepository debtRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private BranchRepository branchRepository;

	@Autowired
	private AssignDebtCommand assignDebtCommand;

	@RequestMapping(value="/list")
	public String searchDebt(@ModelAttribute("model") final ModelMap model,
			@RequestParam(required = false, defaultValue = "1") Integer page,
			@RequestParam(required = false, defaultValue = "fechaCreacion") String orderBy,
			@RequestParam(required = false, defaultValue = "false") Boolean ascending,
			String shopperDni,
			@DateTimeFormat(pattern="dd/MM/yyyy") Date from,
			@DateTimeFormat(pattern="dd/MM/yyyy") Date to) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		model.addAttribute("user", user);

		if (from == null) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, -3);
			from = calendar.getTime();
		}

		if (to == null) {
			to = new Date();
		}
		SearchDebtCommand searchDebtCommand = new SearchDebtCommand(debtRepository);
		searchDebtCommand.setPage(page);
		searchDebtCommand.setPageSize(25);
		searchDebtCommand.setOrderBy(orderBy, ascending);
		searchDebtCommand.setShopperDni(shopperDni);
		searchDebtCommand.setFrom(from);
		searchDebtCommand.setTo(to);
		ResultSet<Debt> resultSet = searchDebtCommand.execute();
		model.put("result", resultSet);
		model.put("page", page);
		model.put("pageSize", 25);
		model.put("shopperDni", shopperDni);
		model.put("from", from);
		model.put("to", to);
		return "listDebt";
	}

	@RequestMapping(value="/listjson")
	public @ResponseBody List<DebtDto> searchDebtAsJson(@ModelAttribute("model") final ModelMap model,
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false, defaultValue = "fechaCreacion") String orderBy,
			@RequestParam(required = false, defaultValue = "false") Boolean ascending,
			String shopperDni,
			@DateTimeFormat(pattern="dd/MM/yyyy") Date from,
			@DateTimeFormat(pattern="dd/MM/yyyy") Date to) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		model.addAttribute("user", user);

		SearchDebtDtoCommand searchDebtCommand = new SearchDebtDtoCommand(debtRepository);
		searchDebtCommand.setPage(null);
		searchDebtCommand.setPageSize(null);
		searchDebtCommand.setOrderBy(orderBy, ascending);
		searchDebtCommand.setShopperDni(shopperDni);
		searchDebtCommand.setFrom(from);
		searchDebtCommand.setTo(to);
		ResultSet<DebtDto> resultSet = searchDebtCommand.execute();
		model.put("result", resultSet);
		model.put("shopperDni", shopperDni);
		model.put("from", from);
		model.put("to", to);
		return resultSet.getItems();
	}

	@RequestMapping(value="/assign", method = RequestMethod.POST)
	public @ResponseBody Boolean assign(@RequestParam("nroOrden") long nroOrden,
			@RequestParam("items[]") List<Long> items) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		assignDebtCommand.setOperator(user);
		assignDebtCommand.setOrderId(nroOrden);
		assignDebtCommand.setDebtIds(items);
		return assignDebtCommand.execute();
	}

	@RequestMapping(value="/export")
	public void exportDebt(final HttpServletResponse response,
			String shopperDni,
			@DateTimeFormat(pattern="dd/MM/yyyy") Date from,
			@DateTimeFormat(pattern="dd/MM/yyyy") Date to) {
		ExportDebtCommand exportDebtCommand = new ExportDebtCommand(debtRepository);
		exportDebtCommand.setShopperDni(shopperDni);
		exportDebtCommand.setFrom(from);
		exportDebtCommand.setTo(to);
		Workbook workbook = exportDebtCommand.execute();

		String fileName = "deuda.xlsx";
		response.setContentType("application/vnd.openxmlformats-officedocument."
				+ "spreadsheetml.sheet");
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"",
				fileName);
		response.setHeader(headerKey, headerValue);
		try {
			workbook.write(response.getOutputStream());
		} catch (IOException e) {
			try {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Cannot write the Excel file");
			} catch (IOException e2) {
				//cannot write a response
			}
		}
	}

	@RequestMapping(value="/view/{id}")
	public String viewDebt(@ModelAttribute("model") final ModelMap model,
			@PathVariable long id) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		model.addAttribute("user", user);
		model.addAttribute("tiposPago",TipoPago.values());
		GetDebtCommand command = new GetDebtCommand(debtRepository);
		command.setDebtId(id);
		Debt debt = command.execute();
		model.addAttribute("debt", debt);
		model.addAttribute("readOnly", true);
		return "debt";
	}

	@RequestMapping(value="/edit/{id}")
	public String editDebt(@ModelAttribute("model") final ModelMap model,
			@PathVariable long id) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		model.addAttribute("user", user);
		model.addAttribute("tiposPago",TipoPago.values());
		GetDebtCommand command = new GetDebtCommand(debtRepository);
		command.setDebtId(id);
		Debt debt = command.execute();
		model.addAttribute("debt", debt);
		model.addAttribute("readOnly", false);
		return "debt";
	}

	@RequestMapping(value="/create")
	public String newDebt(@ModelAttribute("model") final ModelMap model) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		model.addAttribute("user", user);
		model.addAttribute("tiposPago", TipoPago.values());
		model.addAttribute("readOnly", false);
		return "debt";
	}

	@RequestMapping(value="/create", method = RequestMethod.POST)
	public String createDebt(@ModelAttribute("model") final ModelMap model, String shopperDni,
			long clientId, long branchId, String tipoItem, String tipoPago, Date fecha,
			double importe, String observaciones, String survey) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		SaveDebtCommand createDebtCommand = new SaveDebtCommand(debtRepository,
				clientRepository, branchRepository);
		createDebtCommand.setShopperDni(shopperDni);
		createDebtCommand.setClientId(clientId);
		createDebtCommand.setBranchId(branchId);
		createDebtCommand.setTipoItemValue(tipoItem);
		createDebtCommand.setTipoPagoValue(tipoPago);
		createDebtCommand.setFecha(fecha);
		createDebtCommand.setImporte(importe);
		createDebtCommand.setObservaciones(observaciones);
		createDebtCommand.setSurvey(survey);
		Debt debt = createDebtCommand.execute();

		model.addAttribute("user", user);
		model.addAttribute("tiposPago", TipoPago.values());
		model.addAttribute("item", debt);
		return "debt";
	}

	@RequestMapping(value="/update/{id}", method = RequestMethod.POST)
	public String updateDebt(@ModelAttribute("model") final ModelMap model, @PathVariable long id,
			String shopperDni, long clientId, long branchId, String tipoItem,
			String tipoPago, Date fecha, double importe, String observaciones,
			String survey) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		SaveDebtCommand createDebtCommand = new SaveDebtCommand(debtRepository,
				clientRepository, branchRepository, id);
		createDebtCommand.setShopperDni(shopperDni);
		createDebtCommand.setClientId(clientId);
		createDebtCommand.setBranchId(branchId);
		createDebtCommand.setTipoItemValue(tipoItem);
		createDebtCommand.setTipoPagoValue(tipoPago);
		createDebtCommand.setFecha(fecha);
		createDebtCommand.setImporte(importe);
		createDebtCommand.setObservaciones(observaciones);
		createDebtCommand.setSurvey(survey);
		Debt debt = createDebtCommand.execute();

		model.addAttribute("user", user);
		model.addAttribute("tiposPago", TipoPago.values());
		model.addAttribute("item", debt);
		return "redirect:../list";
	}
}
