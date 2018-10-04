package com.ibiscus.shopnchek.web.controller.site;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.ibiscus.shopnchek.application.client.SearchClientCommand;
import com.ibiscus.shopnchek.application.debt.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
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

import com.ibiscus.shopnchek.application.ResultSet;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;
import com.ibiscus.shopnchek.domain.debt.BranchRepository;
import com.ibiscus.shopnchek.domain.debt.ClientRepository;
import com.ibiscus.shopnchek.domain.debt.Debt;
import com.ibiscus.shopnchek.domain.debt.Debt.State;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;
import com.ibiscus.shopnchek.domain.debt.TipoItem;
import com.ibiscus.shopnchek.domain.debt.TipoPago;
import com.ibiscus.shopnchek.domain.security.User;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.ibiscus.shopnchek.domain.debt.Debt.State.creada;
import static com.ibiscus.shopnchek.domain.debt.Debt.State.pendiente;

@Controller
@RequestMapping("/debt")
public class DebtController {

	private static final String APPROVE_ADDITIONAL = "approve_additional";

	@Autowired
	private DebtRepository debtRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private BranchRepository branchRepository;

	@Autowired
	private ShopperRepository shopperRepository;

	@Autowired
	private CreateDebtCommand createDebtCommand;

	@Autowired
	private SaveDebtCommand saveDebtCommand;

	@Autowired
	private ApproveDebtCommand approveDebtCommand;

	@Autowired
	private UpdateImporteDebtCommand updateImporteDebtCommand;

	@Autowired
	private AssignDebtCommand assignDebtCommand;

	@Autowired
	private GetDebtCommand getDebtCommand;

	@Autowired
	private SearchDebtCommand searchDebtCommand;

	@Autowired
	private SearchDebtDtoCommand searchDebtDtoCommand;

	@Autowired
	private SearchClientCommand searchClientCommand;

	@RequestMapping(value="/list")
	public String searchDebt(@ModelAttribute("model") final ModelMap model,
			@RequestParam(required = false, defaultValue = "1") Integer page,
			@RequestParam(required = false, defaultValue = "fecha") String orderBy,
			@RequestParam(required = false, defaultValue = "false") Boolean ascending,
			String shopperDni, String tipoPago, String tipoItem,
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
		searchDebtCommand.setPage(page);
		searchDebtCommand.setPageSize(25);
		searchDebtCommand.setOrderBy(orderBy, ascending);
		searchDebtCommand.setShopperDni(shopperDni);
		searchDebtCommand.setFrom(from);
		searchDebtCommand.setTo(to);
		Set<State> states = newHashSet(pendiente);
		if (user.hasFeature(APPROVE_ADDITIONAL)) {
			states.add(creada);
		}
		searchDebtCommand.setStates(states);
		searchDebtCommand.setTipoPago(tipoPago);
		searchDebtCommand.setTipoItem(tipoItem);
		ResultSet<Debt> resultSet = searchDebtCommand.execute();
		model.put("result", resultSet);
		model.put("page", page);
		model.put("pageSize", 25);
		model.put("orderBy", orderBy);
		model.put("ascending", ascending);
		model.put("shopperDni", shopperDni);
		model.put("tipoPago", tipoPago);
		model.put("tipoItem", tipoItem);
		model.put("from", from);
		model.put("to", to);

		if (!StringUtils.isBlank(shopperDni)) {
			Shopper shopper = shopperRepository.findByDni(shopperDni);
			if (shopper != null) {
				model.put("shopper", shopper);
			}
		}
		model.put("tiposPago", TipoPago.values());
		model.put("tiposItem", TipoItem.values());
		return "listDebt";
	}

	@RequestMapping(value="/listjson")
	public @ResponseBody List<DebtDto> searchDebtAsJson(@ModelAttribute("model") final ModelMap model,
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false, defaultValue = "fechaCreacion") String orderBy,
			@RequestParam(required = false, defaultValue = "false") Boolean ascending,
			String shopperDni, String tipoPago,
			@DateTimeFormat(pattern="dd/MM/yyyy") Date from,
			@DateTimeFormat(pattern="dd/MM/yyyy") Date to) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		model.addAttribute("user", user);

		searchDebtDtoCommand.setPage(null);
		searchDebtDtoCommand.setPageSize(null);
		searchDebtDtoCommand.setOrderBy(orderBy, ascending);
		searchDebtDtoCommand.setShopperDni(shopperDni);
		searchDebtDtoCommand.setFrom(from);
		searchDebtDtoCommand.setTo(to);
		searchDebtDtoCommand.setTipoPago(tipoPago);
		ResultSet<DebtDto> resultSet = searchDebtDtoCommand.execute();
		model.put("result", resultSet);
		model.put("shopperDni", shopperDni);
		model.put("from", from);
		model.put("to", to);
		return resultSet.getItems();
	}

	@RequestMapping(value="/aditionals")
	public String searchAditionals(@ModelAttribute("model") final ModelMap model,
			@RequestParam(required = false, defaultValue = "1") Integer page,
			@RequestParam(required = false, defaultValue = "fecha") String orderBy,
			@RequestParam(required = false, defaultValue = "false") Boolean ascending,
			String shopperDni, String tipoPago,
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
		searchDebtCommand.setPage(page);
		searchDebtCommand.setPageSize(25);
		searchDebtCommand.setOrderBy(orderBy, ascending);
		searchDebtCommand.setShopperDni(shopperDni);
		searchDebtCommand.setFrom(from);
		searchDebtCommand.setTo(to);
		Set<State> states = newHashSet(pendiente);
		if (user.hasFeature(APPROVE_ADDITIONAL)) {
			states.add(creada);
		}
		searchDebtCommand.setStates(states);
		searchDebtCommand.setTipoPago(tipoPago);
		searchDebtCommand.setTipoItem(TipoItem.manual.toString());
		searchDebtCommand.setOwner(user);
		ResultSet<Debt> resultSet = searchDebtCommand.execute();
		model.put("result", resultSet);
		model.put("page", page);
		model.put("pageSize", 25);
		model.put("orderBy", orderBy);
		model.put("ascending", ascending);
		model.put("shopperDni", shopperDni);
		model.put("tipoPago", tipoPago);
		model.put("from", from);
		model.put("to", to);

		model.put("tiposPago", TipoPago.values());
		model.put("tiposItem", TipoItem.values());
		model.put("showItemType", false);
		return "listDebt";
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
			String shopperDni, String tipoPago, String tipoItem,
			@DateTimeFormat(pattern="dd/MM/yyyy") Date from,
			@DateTimeFormat(pattern="dd/MM/yyyy") Date to) {
		ExportDebtCommand exportDebtCommand = new ExportDebtCommand(debtRepository);
		exportDebtCommand.setShopperDni(shopperDni);
		exportDebtCommand.setTipoPago(tipoPago);
		exportDebtCommand.setTipoItem(tipoItem);
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
		getDebtCommand.setDebtId(id);
		Debt debt = getDebtCommand.execute();
		model.addAttribute("debt", debt);
		searchClientCommand.setName(null);
		model.addAttribute("clients", searchClientCommand.execute());
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
		getDebtCommand.setDebtId(id);
		Debt debt = getDebtCommand.execute();
		model.addAttribute("debt", debt);
		searchClientCommand.setName(null);
		model.addAttribute("clients", searchClientCommand.execute());
		model.addAttribute("readOnly", false);
		return "debt";
	}

	@RequestMapping(value="/create")
	public String newDebt(@ModelAttribute("model") final ModelMap model) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		model.addAttribute("user", user);
		model.addAttribute("tiposPago", TipoPago.values());
		searchClientCommand.setName(null);
		model.addAttribute("clients", searchClientCommand.execute());
		model.addAttribute("readOnly", false);
		return "createDebt";
	}

	@RequestMapping(value="/create", method=RequestMethod.POST)
	public @ResponseBody boolean createDebt(@RequestBody VisitaDto visitaDto) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		createDebtCommand.setVisitaDto(visitaDto);
		createDebtCommand.setOperator(user);
		createDebtCommand.execute();

		return true;
	}

	@RequestMapping(value="/update/{id}", method = RequestMethod.POST)
	public String updateDebt(@ModelAttribute("model") final ModelMap model,
			@PathVariable long id, String shopperDni,
			@RequestParam(required = false) Long clientId,
			@RequestParam(required = false) String clientDescription,
			@RequestParam(required = false) Long branchId,
			@RequestParam(required = false) String branchDescription, String route,
			String tipoItem, String tipoPago, @DateTimeFormat(pattern="dd/MM/yyyy") Date fecha,
			double importe, String observaciones, String survey) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		saveDebtCommand.setDebtId(id);
		saveDebtCommand.setShopperDni(shopperDni);
		saveDebtCommand.setClientId(clientId);
		saveDebtCommand.setClientDescription(clientDescription);
		saveDebtCommand.setBranchId(branchId);
		saveDebtCommand.setBranchDescription(branchDescription);
		saveDebtCommand.setRoute(route);
		saveDebtCommand.setTipoItemValue(tipoItem);
		saveDebtCommand.setTipoPagoValue(tipoPago);
		saveDebtCommand.setFecha(fecha);
		saveDebtCommand.setImporte(importe);
		saveDebtCommand.setObservaciones(observaciones);
		saveDebtCommand.setSurvey(survey);
		saveDebtCommand.setOperator(user);
		Debt debt = saveDebtCommand.execute();

		model.addAttribute("user", user);
		model.addAttribute("tiposPago", TipoPago.values());
		model.addAttribute("debt", debt);
		return "redirect:../list";
	}

	@RequestMapping(value="/approve", method = RequestMethod.POST)
	public @ResponseBody boolean approve(@ModelAttribute("model") final ModelMap model,
										 @RequestParam(value="additionalIds[]") Long[] additionalIds) {
		approveDebtCommand.setDebtIds(newArrayList(additionalIds));
		approveDebtCommand.execute();
		return true;
	}

	@RequestMapping(value="/updateImporte", method = RequestMethod.POST)
	public @ResponseBody boolean updateImporte(@ModelAttribute("model") final ModelMap model,
			long id, double importe) {
		updateImporteDebtCommand.setDebtId(id);
		updateImporteDebtCommand.setImporte(importe);
		updateImporteDebtCommand.execute();

		return true;
	}
}
