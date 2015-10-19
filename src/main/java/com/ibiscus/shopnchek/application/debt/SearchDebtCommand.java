package com.ibiscus.shopnchek.application.debt;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.ibiscus.shopnchek.application.SearchCommand;
import com.ibiscus.shopnchek.domain.debt.Debt;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;

public class SearchDebtCommand extends SearchCommand<Debt> {

	private final DebtRepository debtRepository;

	private String shopperDni;

	private Date from;

	private Date to;

	public SearchDebtCommand(final DebtRepository debtRepository) {
		Validate.notNull(debtRepository, "The debt repository cannot be null");
		this.debtRepository = debtRepository;
	}

	public void setShopperDni(final String shopperDni) {
		this.shopperDni = shopperDni;
	}

	public void setFrom(final Date from) {
		this.from = from;
	}

	public void setTo(final Date to) {
		this.to = to;
	}

	@Override
	protected List<Debt> getItems() {
		if (from != null && to != null) {
			Validate.isTrue(from.before(to), "The FROM date must be before the TO date");
		}
		return debtRepository.find(getStart(), getPageSize(), getOrderBy(), isAscending(),
				shopperDni, from, to);
	}

	@Override
	protected int getCount() {
		return debtRepository.getCount(shopperDni, from, to);
	}
}
