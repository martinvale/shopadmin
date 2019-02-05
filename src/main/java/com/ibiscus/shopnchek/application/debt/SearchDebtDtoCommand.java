package com.ibiscus.shopnchek.application.debt;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import com.ibiscus.shopnchek.application.SearchCommand;
import com.ibiscus.shopnchek.domain.debt.Debt;
import com.ibiscus.shopnchek.domain.debt.TipoPago;
import com.ibiscus.shopnchek.domain.debt.Debt.State;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;

import static com.google.common.collect.Sets.newHashSet;
import static com.ibiscus.shopnchek.domain.debt.Debt.State.pendiente;

public class SearchDebtDtoCommand extends SearchCommand<DebtDto> {

	private DebtRepository debtRepository;

	private String shopperDni;

	private Date from;

	private Date to;

	private String tipoPago;

	public SearchDebtDtoCommand() {
	}

	public void setDebtRepository(final DebtRepository debtRepository) {
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

	public void setTipoPago(final String tipoPago) {
		this.tipoPago = tipoPago;
	}

	@Override
	protected List<DebtDto> getItems() {
		if (from != null && to != null) {
			Validate.isTrue(from.before(to), "The FROM date must be before the TO date");
		}
		Validate.notEmpty(shopperDni, "The Shopper DNI is mandatory");
		List<DebtDto> debtDtoItems = new LinkedList<DebtDto>();

		List<Debt> debtItems = debtRepository.find(getStart(), getPageSize(), getOrderBy(),
				isAscending(), shopperDni, newHashSet(pendiente), from, to, getTipoPago(), null, null, null);
		for (Debt debt : debtItems) {
			debtDtoItems.add(new DebtDto(debt));
		}
		return debtDtoItems;
	}

	@Override
	protected int getCount() {
		return debtRepository.getCount(shopperDni, newHashSet(pendiente), from, to,
				getTipoPago(), null, null, null);
	}

	private TipoPago getTipoPago() {
		TipoPago tipoPagoDeuda = null;
		if (!StringUtils.isBlank(tipoPago)) {
			tipoPagoDeuda = TipoPago.valueOf(tipoPago);
		}
		return tipoPagoDeuda;
	}
}
