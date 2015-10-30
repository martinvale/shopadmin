package com.ibiscus.shopnchek.application.debt;

import java.util.ArrayList;
import java.util.List;

import com.ibiscus.shopnchek.domain.debt.Branch;
import com.ibiscus.shopnchek.domain.debt.Client;

public class ClientDto {

	private long id;

	private String name;

	private List<BranchDto> branchs = new ArrayList<BranchDto>();

	public ClientDto(final Client client) {
		id = client.getId();
		name = client.getName();
		for (Branch branch : client.getBranchs()) {
			branchs.add(new BranchDto(branch));
		}
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<BranchDto> getBranchs() {
		return branchs;
	}

}
