package com.ibiscus.shopnchek.application;

import java.util.List;

public class ResultSet<T> {

	private final int page;

	private final int pageSize;

	private final List<T> items;

	private final int count;

	public ResultSet(final int page, final int pageSize, final List<T> items,
			final int count) {
		this.page = page;
		this.pageSize = pageSize;
		this.items = items;
		this.count = count;
	}

	public int getPage() {
		return page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public List<T> getItems() {
		return items;
	}

	public int getCount() {
		return count;
	}
}
