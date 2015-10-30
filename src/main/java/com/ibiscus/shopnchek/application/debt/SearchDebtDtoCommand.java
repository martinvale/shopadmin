package com.ibiscus.shopnchek.application.debt;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.ibiscus.shopnchek.application.SearchCommand;
import com.ibiscus.shopnchek.domain.debt.Debt;
import com.ibiscus.shopnchek.domain.debt.Debt.State;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;

public class SearchDebtDtoCommand extends SearchCommand<DebtDto> {

	private final DebtRepository debtRepository;

	private String shopperDni;

	private Date from;

	private Date to;

	public SearchDebtDtoCommand(final DebtRepository debtRepository) {
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
	protected List<DebtDto> getItems() {
		if (from != null && to != null) {
			Validate.isTrue(from.before(to), "The FROM date must be before the TO date");
		}
		List<DebtDto> debtDtoItems = new LinkedList<DebtDto>();
		List<Debt> debtItems = debtRepository.find(getStart(), getPageSize(), getOrderBy(), isAscending(),
				shopperDni, State.pendiente, from, to);
		for (Debt debt : debtItems) {
			debtDtoItems.add(new DebtDto(debt));
		}
		return debtDtoItems;
	}

	@Override
	protected int getCount() {
		return debtRepository.getCount(shopperDni, State.pendiente, from, to);
	}
}
