package com.ibiscus.shopnchek.application.debt;

import java.util.ArrayList;
import java.util.List;


public class VisitaDto {

	private String shopperDni;

	private Long clientId;

	private String clientDescription;

	private Long branchId;

	private String branchDescription;

	private String fecha;

	private List<DebtDto> items = new ArrayList<DebtDto>();

	public VisitaDto() {
	}

	public String getShopperDni() {
		return shopperDni;
	}

	public void setShopperDni(String shopperDni) {
		this.shopperDni = shopperDni;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public String getClientDescription() {
		return clientDescription;
	}

	public void setClientDescription(String clientDescription) {
		this.clientDescription = clientDescription;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public String getBranchDescription() {
		return branchDescription;
	}

	public void setBranchDescription(String branchDescription) {
		this.branchDescription = branchDescription;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(final String fecha) {
		this.fecha = fecha;
	}

	public List<DebtDto> getItems() {
		return items;
	}

	public void setItems(List<DebtDto> items) {
		this.items = items;
	}

}