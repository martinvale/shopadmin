package com.ibiscus.shopnchek.application.proveedor;

import java.util.List;

import com.ibiscus.shopnchek.application.SearchCommand;
import com.ibiscus.shopnchek.domain.admin.Proveedor;
import com.ibiscus.shopnchek.domain.admin.ProveedorRepository;

public class SearchProveedorCommand extends SearchCommand<Proveedor> {

	private ProveedorRepository proveedorRepository;

	private String name;

	public SearchProveedorCommand() {
	}

	@Override
	protected List<Proveedor> getItems() {
		return proveedorRepository.find(getStart(), getPageSize(), name);
	}

	@Override
	protected int getCount() {
		return proveedorRepository.getProveedoresCount(name);
	}

	public void setProveedorRepository(final ProveedorRepository proveedorRepository) {
		this.proveedorRepository = proveedorRepository;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
