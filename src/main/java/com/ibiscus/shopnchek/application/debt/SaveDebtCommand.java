package com.ibiscus.shopnchek.application.debt;

import java.util.Date;

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

public class SaveDebtCommand implements Command<Debt> {

	private DebtRepository debtRepository;

	private BranchRepository branchRepository;

	private ClientRepository clientRepository;

	private ShopperRepository shopperRepository;

	private Long debtId;

	private String tipoItemValue;

	private String tipoPagoValue;

	private String shopperDni;

	private double importe;

	private Date fecha;

	private String observaciones;

	private String survey;

	private Long clientId;

	private String clientDescription;

	private Long branchId;

	private String branchDescription;

	private String route;

	private Long externalId;

	private User operator;

	public SaveDebtCommand() {
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Debt execute() {
		Validate.notNull(shopperDni, "The shopper DNI cannot be null");
		TipoItem tipoItem = TipoItem.valueOf(tipoItemValue);
		TipoPago tipoPago = TipoPago.valueOf(tipoPagoValue);
		Client client = null;
		if (clientId != null) {
			client = clientRepository.get(clientId);
			clientDescription = client.getName();
		}
		Branch branch = null;
		if (branchId != null) {
			branch = branchRepository.get(branchId);
			branchDescription = branch.getAddress();
		}
		Debt debt;
		if (debtId == null) {
			debt = new Debt(tipoItem, tipoPago, shopperDni, importe, fecha, observaciones,
					survey, client, clientDescription, branch, branchDescription,
					route, externalId, operator.getUsername());
			debtRepository.save(debt);
		} else {
			debt = debtRepository.get(debtId);
			debt.update(tipoItem, tipoPago, shopperDni, importe, fecha, observaciones,
					survey, client, clientDescription, branch, branchDescription,
					route, externalId, operator.getUsername());
		}
		Shopper shopper = shopperRepository.findByDni(debt.getShopperDni());
		debt.updateShopper(shopper);
		return debt;
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

	public void setDebtId(final Long debtId) {
		this.debtId = debtId;
	}

	public void setShopperDni(final String shopperDni) {
		this.shopperDni = shopperDni;
	}

	public void setTipoItemValue(final String tipoItemValue) {
		this.tipoItemValue = tipoItemValue;
	}

	public void setTipoPagoValue(final String tipoPagoValue) {
		this.tipoPagoValue = tipoPagoValue;
	}

	public void setImporte(final double importe) {
		this.importe = importe;
	}

	public void setFecha(final Date fecha) {
		this.fecha = fecha;
	}

	public void setObservaciones(final String observaciones) {
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

    public void setRoute(final String route) {
        this.route = route;
    }

    public void setExternalId(final Long externalId) {
		this.externalId = externalId;
	}

	public void setOperator(final User operator) {
		this.operator = operator;
	}
}
