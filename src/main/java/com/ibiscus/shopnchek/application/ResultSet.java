package com.ibiscus.shopnchek.application;

import java.util.List;

public class ResultSet<T> {

	private final Integer page;

	private final Integer pageSize;

	private final List<T> items;

	private final int count;

	public ResultSet(final Integer page, final Integer pageSize, final List<T> items,
			final int count) {
		this.page = page;
		this.pageSize = pageSize;
		this.items = items;
		this.count = count;
	}

	public Integer getPage() {
		return page;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public List<T> getItems() {
		return items;
	}

	public int getCount() {
		return count;
	}
}
