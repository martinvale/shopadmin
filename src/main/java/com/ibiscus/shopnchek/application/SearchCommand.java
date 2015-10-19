package com.ibiscus.shopnchek.application;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public abstract class SearchCommand<T> implements Command<ResultSet<T>> {

	private String orderBy;

	private boolean ascending;

	private int page;

	private int pageSize = 25;

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public ResultSet<T> execute() {
		List<T> items = getItems();
		int count = getCount();
		return new ResultSet<T>(page, pageSize, items, count);
	}

	protected abstract List<T> getItems();

	protected abstract int getCount();

	public void setPage(int page) {
		this.page = page;
	}

	protected int getStart() {
		return (page - 1) * pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	protected int getPageSize() {
		return pageSize;
	}

	public void setOrderBy(final String orderBy, final boolean ascending) {
		this.orderBy = orderBy;
		this.ascending = ascending;
	}

	protected String getOrderBy() {
		return orderBy;
	}

	protected boolean isAscending() {
		return ascending;
	}

}
