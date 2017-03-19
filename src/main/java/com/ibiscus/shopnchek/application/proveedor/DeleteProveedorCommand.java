package com.ibiscus.shopnchek.application.proveedor;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.admin.ProveedorRepository;

public class DeleteProveedorCommand implements Command<Boolean> {

	private ProveedorRepository proveedorRepository;

	private long id;

	public DeleteProveedorCommand() {
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Boolean execute() {
		proveedorRepository.delete(id);
		return true;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setProveedorRepository(final ProveedorRepository proveedorRepository) {
		this.proveedorRepository = proveedorRepository;
	}

}
