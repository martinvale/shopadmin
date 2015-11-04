package com.ibiscus.shopnchek.application.debt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;
import com.ibiscus.shopnchek.domain.debt.Branch;
import com.ibiscus.shopnchek.domain.debt.BranchRepository;
import com.ibiscus.shopnchek.domain.debt.Client;
import com.ibiscus.shopnchek.domain.debt.ClientRepository;
import com.ibiscus.shopnchek.domain.debt.Debt;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;
import com.ibiscus.shopnchek.domain.debt.TipoItem;
import com.ibiscus.shopnchek.domain.debt.TipoPago;
import com.ibiscus.shopnchek.domain.security.User;

public class CreateDebtCommand implements Command<List<Debt>> {

	private DebtRepository debtRepository;

	private BranchRepository branchRepository;

	private ClientRepository clientRepository;

	private ShopperRepository shopperRepository;

	private String tipoItemValue;

	private String shopperDni;

	private Date fecha;

	private String survey;

	private List<String> tiposPagoValue;

	private List<Double> importes;

	private List<String> observaciones;

	private Long clientId;

	private String clientDescription;

	private Long branchId;

	private String branchDescription;

	private Long externalId;

	private User operator;

	public CreateDebtCommand() {
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<Debt> execute() {
		Validate.notNull(shopperDni, "The shopper DNI cannot be null");
		List<Debt> debts = new ArrayList<Debt>();

		TipoItem tipoItem = TipoItem.valueOf(tipoItemValue);
		Client client = null;
		if (clientId != null) {
			client = clientRepository.get(clientId);
		}
		Branch branch = null;
		if (branchId != null) {
			branch = branchRepository.get(branchId);
		}
		for (int i = 0; i < tiposPagoValue.size(); i++) {
			TipoPago tipoPago = TipoPago.valueOf(tiposPagoValue.get(i));
			Double importe = importes.get(i);
			String observacion = observaciones.get(i);
			Debt debt = new Debt(tipoItem, tipoPago, shopperDni, importe, fecha, observacion,
					survey, client, clientDescription, branch, branchDescription,
					externalId, operator.getUsername());
			debtRepository.save(debt);
			Shopper shopper = shopperRepository.findByDni(debt.getShopperDni());
			debt.updateShopper(shopper);
			debts.add(debt);
		}
		return debts;
	}

	public void setDebtRepository(final DebtRepository debtRepository) {
		this.debtRepository = debtRepository;
	}

	public void setClientRepository(final ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}

	public void setBranchRepository(final BranchRepository branchRepository) {
		this.branchRepository = branchRepository;
	}

	public void setShopperRepository(final ShopperRepository shopperRepository) {
		this.shopperRepository = shopperRepository;
	}

	public void setShopperDni(final String shopperDni) {
		this.shopperDni = shopperDni;
	}

	public void setTipoItemValue(final String tipoItemValue) {
		this.tipoItemValue = tipoItemValue;
	}

	public void setTiposPagoValue(final List<String> tiposPagoValue) {
		this.tiposPagoValue = tiposPagoValue;
	}

	public void setImportes(final List<Double> importes) {
		this.importes = importes;
	}

	public void setFecha(final Date fecha) {
		this.fecha = fecha;
	}

	public void setObservaciones(final List<String> observaciones) {
		this.observaciones = observaciones;
	}

	public void setSurvey(final String survey) {
		this.survey = survey;
	}

	public void setClientId(final Long clientId) {
		this.clientId = clientId;
	}

	public void setClientDescription(final String clientDescription) {
		this.clientDescription = clientDescription;
	}

	public void setBranchId(final Long branchId) {
		this.branchId = branchId;
	}

	public void setBranchDescription(final String branchDescription) {
		this.branchDescription = branchDescription;
	}

	public void setExternalId(final Long externalId) {
		this.externalId = externalId;
	}

	public void setOperator(final User operator) {
		this.operator = operator;
	}
}
