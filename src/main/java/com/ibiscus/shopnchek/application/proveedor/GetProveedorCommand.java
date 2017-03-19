package com.ibiscus.shopnchek.application.proveedor;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.admin.Proveedor;
import com.ibiscus.shopnchek.domain.admin.ProveedorRepository;

public class GetProveedorCommand implements Command<Proveedor> {

	private ProveedorRepository proveedorRepository;

	private long id;

	public GetProveedorCommand() {
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public Proveedor execute() {
		return proveedorRepository.get(id);
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setProveedorRepository(final ProveedorRepository proveedorRepository) {
		this.proveedorRepository = proveedorRepository;
	}

}
