package com.ibiscus.shopnchek.application.debt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.debt.Debt.State;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;
import com.ibiscus.shopnchek.domain.util.Row;

public class GetDebtSummaryCommand implements Command<List<Row>> {

	private final DebtRepository debtRepository;

	private String shopperDni;

	private List<State> states;

	private Date from;

	private Date to;

	private List<String> orderBy = new ArrayList<String>();

	public GetDebtSummaryCommand(final DebtRepository debtRepository) {
		Validate.notNull(debtRepository, "The debt repository cannot be null");
		this.debtRepository = debtRepository;
	}

	public void setShopperDni(final String shopperDni) {
		this.shopperDni = shopperDni;
	}

	public void setStates(final List<State> states) {
		this.states = states;
	}

	public void setFrom(final Date from) {
		this.from = from;
	}

	public void setTo(final Date to) {
		this.to = to;
	}

	public void addGroupBy(final String propertyName) {
		orderBy.add(propertyName);
	}

	@Override
	public List<Row> execute() {
		if (from != null && to != null) {
			Validate.isTrue(from.before(to), "The FROM date must be before the TO date");
		}

		return debtRepository.getSummary(shopperDni, states, from, to, orderBy);
	}
}
