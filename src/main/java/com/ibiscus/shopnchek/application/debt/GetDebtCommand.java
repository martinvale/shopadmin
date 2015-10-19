package com.ibiscus.shopnchek.application.debt;

import org.apache.commons.lang.Validate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.debt.Debt;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;

public class GetDebtCommand implements Command<Debt> {

	private final DebtRepository debtRepository;

	private long debtId;

	public GetDebtCommand(final DebtRepository debtRepository) {
		Validate.notNull(debtRepository, "The debt repository cannot be null");
		this.debtRepository = debtRepository;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public Debt execute() {
		return debtRepository.get(debtId);
	}

	public void setDebtId(final long debtId) {
		this.debtId = debtId;
	}

}
