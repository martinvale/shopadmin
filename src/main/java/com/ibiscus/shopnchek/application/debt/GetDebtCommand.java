package com.ibiscus.shopnchek.application.debt;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;
import com.ibiscus.shopnchek.domain.debt.Debt;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;

public class GetDebtCommand implements Command<Debt> {

	private DebtRepository debtRepository;

	private ShopperRepository shopperRepository;

	private long debtId;

	public GetDebtCommand() {
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public Debt execute() {
		Debt debt = debtRepository.get(debtId);
		Shopper shopper = shopperRepository.findByDni(debt.getShopperDni());
		debt.updateShopper(shopper);
		return debt;
	}

	public void setDebtId(final long debtId) {
		this.debtId = debtId;
	}

	public void setDebtRepository(final DebtRepository debtRepository) {
		this.debtRepository = debtRepository;
	}

	public void setShopperRepository(final ShopperRepository shopperRepository) {
		this.shopperRepository = shopperRepository;
	}
}
