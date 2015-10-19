package com.ibiscus.shopnchek.application.debt;

import java.util.Date;

import org.apache.commons.lang.Validate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.debt.Branch;
import com.ibiscus.shopnchek.domain.debt.BranchRepository;
import com.ibiscus.shopnchek.domain.debt.Client;
import com.ibiscus.shopnchek.domain.debt.ClientRepository;
import com.ibiscus.shopnchek.domain.debt.Debt;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;
import com.ibiscus.shopnchek.domain.debt.TipoItem;
import com.ibiscus.shopnchek.domain.debt.TipoPago;

public class SaveDebtCommand implements Command<Debt> {

	private final DebtRepository debtRepository;

	private final BranchRepository branchRepository;

	private final ClientRepository clientRepository;

	private final Long debtId;

	private String tipoItemValue;

	private String tipoPagoValue;

	private String shopperDni;

	private double importe;

	private Date fecha;

	private String observaciones;

	private long clientId;

	private long branchId;

	private Long externalId;
	
	public SaveDebtCommand(final DebtRepository debtRepository, final ClientRepository clientRepository,
			final BranchRepository branchRepository) {
		this(debtRepository, clientRepository, branchRepository, null);
	}

	public SaveDebtCommand(final DebtRepository debtRepository, final ClientRepository clientRepository,
			final BranchRepository branchRepository, final Long debtId) {
		Validate.notNull(debtRepository, "The debt repository cannot be null");
		Validate.notNull(clientRepository, "The client repository cannot be null");
		Validate.notNull(branchRepository, "The branch repository cannot be null");
		this.debtRepository = debtRepository;
		this.clientRepository = clientRepository;
		this.branchRepository = branchRepository;
		this.debtId = debtId;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Debt execute() {
		Validate.notNull(shopperDni, "The shopper DNI cannot be null");
		TipoItem tipoItem = TipoItem.valueOf(tipoItemValue);
		TipoPago tipoPago = TipoPago.valueOf(tipoPagoValue);
		Client client = clientRepository.get(clientId);
		Branch branch = branchRepository.get(branchId);
		Debt debt;
		if (debtId == null) {
			debt = new Debt(tipoItem, tipoPago, shopperDni, importe, fecha, observaciones,
					client, branch, externalId);
			debtRepository.save(debt);
		} else {
			debt = debtRepository.get(debtId);
			debt.update(tipoItem, tipoPago, shopperDni, importe, fecha, observaciones,
					client, branch, externalId);
			debtRepository.update(debt);
		}
		return debt;
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

	public void setClientId(final long clientId) {
		this.clientId = clientId;
	}

	public void setBranchId(final long branchId) {
		this.branchId = branchId;
	}

	public void setExternalId(final Long externalId) {
		this.externalId = externalId;
	}

}
