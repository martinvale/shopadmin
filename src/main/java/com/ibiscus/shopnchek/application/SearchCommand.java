package com.ibiscus.shopnchek.application;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public abstract class SearchCommand<T> implements Command<ResultSet<T>> {

	private String orderBy;

	private boolean ascending;

	private Integer page;

	private Integer pageSize;

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public ResultSet<T> execute() {
		List<T> items = getItems();
		int count = getCount();
		return new ResultSet<T>(page, pageSize, items, count);
	}

	protected abstract List<T> getItems();

	protected abstract int getCount();

	public void setPage(Integer page) {
		this.page = page;
	}

	protected Integer getStart() {
		Integer start = null;
		if (page != null && pageSize != null) {
			start = (page - 1) * pageSize;
		}
		return start;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	protected Integer getPageSize() {
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
