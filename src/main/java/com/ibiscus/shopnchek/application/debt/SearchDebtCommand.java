package com.ibiscus.shopnchek.application.debt;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import com.ibiscus.shopnchek.application.SearchCommand;
import com.ibiscus.shopnchek.domain.debt.Debt;
import com.ibiscus.shopnchek.domain.debt.TipoPago;
import com.ibiscus.shopnchek.domain.debt.Debt.State;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;

public class SearchDebtCommand extends SearchCommand<Debt> {

	private DebtRepository debtRepository;

	private String shopperDni;

	private State state;

	private Date from;

	private Date to;

	private String tipoPago;

	public SearchDebtCommand() {
	}

	public void setDebtRepository(final DebtRepository debtRepository) {
		this.debtRepository = debtRepository;
	}

	private TipoPago getTipoPago() {
		TipoPago tipoPagoDeuda = null;
		if (!StringUtils.isBlank(tipoPago)) {
			tipoPagoDeuda = TipoPago.valueOf(tipoPago);
		}
		return tipoPagoDeuda;
	}

	public void setShopperDni(final String shopperDni) {
		this.shopperDni = shopperDni;
	}

	public void setState(final State state) {
		this.state = state;
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
	protected List<Debt> getItems() {
		if (from != null && to != null) {
			Validate.isTrue(from.before(to), "The FROM date must be before the TO date");
		}
		return debtRepository.find(getStart(), getPageSize(), getOrderBy(), isAscending(),
				shopperDni, state, from, to, getTipoPago());
	}

	@Override
	protected int getCount() {
		return debtRepository.getCount(shopperDni, state, from, to, getTipoPago());
	}
}
