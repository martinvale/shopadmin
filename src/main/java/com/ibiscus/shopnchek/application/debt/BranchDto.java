package com.ibiscus.shopnchek.application.debt;

import com.ibiscus.shopnchek.domain.debt.Branch;

public class BranchDto {

	private long id;

	private String address;

	public BranchDto(final Branch branch) {
		id = branch.getId();
		address = branch.getAddress();
	}

	public long getId() {
		return id;
	}

	public String getAddress() {
		return address;
	}

}
