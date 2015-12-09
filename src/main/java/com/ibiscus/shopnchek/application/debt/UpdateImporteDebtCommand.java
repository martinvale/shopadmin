package com.ibiscus.shopnchek.application.debt;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.debt.Debt;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;

public class UpdateImporteDebtCommand implements Command<Debt> {

	private DebtRepository debtRepository;

	private long debtId;

	private double importe;

	public UpdateImporteDebtCommand() {
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Debt execute() {
		Debt debt = debtRepository.get(debtId);
		debt.updateImporte(importe);
		return debt;
	}

	public void setDebtRepository(final DebtRepository debtRepository) {
		this.debtRepository = debtRepository;
	}

	public void setDebtId(final long debtId) {
		this.debtId = debtId;
	}

	public void setImporte(final double importe) {
		this.importe = importe;
	}

}
