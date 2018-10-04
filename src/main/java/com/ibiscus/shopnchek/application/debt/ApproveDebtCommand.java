package com.ibiscus.shopnchek.application.debt;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.debt.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ApproveDebtCommand implements Command<Void> {

	private DebtRepository debtRepository;

	private List<Long> debtIds;

	public ApproveDebtCommand() {
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Void execute() {
		for (Long debtId : debtIds) {
			Debt debt = debtRepository.get(debtId);
			debt.release();
		}
		return null;
	}

	public void setDebtRepository(final DebtRepository debtRepository) {
		this.debtRepository = debtRepository;
	}

	public void setDebtIds(List<Long> debtIds) {
		this.debtIds = debtIds;
	}

}
