package com.ibiscus.shopnchek.application.client;

import java.util.ArrayList;
import java.util.List;

import com.ibiscus.shopnchek.application.debt.BranchDto;
import com.ibiscus.shopnchek.domain.debt.Branch;
import com.ibiscus.shopnchek.domain.debt.Client;

public class ClientDto {

	private Long id;

	private String name;

	private String country;

	private List<BranchDto> branchs = new ArrayList<BranchDto>();

	public ClientDto(Long id, String name, String country) {
		this.id = id;
		this.name = name;
		this.country = country;
	}

	public ClientDto(final Client client) {
		id = client.getId();
		name = client.getName();
		country = client.getCountry();
		for (Branch branch : client.getBranchs()) {
			branchs.add(new BranchDto(branch));
		}
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<BranchDto> getBranchs() {
		return branchs;
	}

	public String getCountry() {
		return country;
	}
}
