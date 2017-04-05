package com.ibiscus.shopnchek.application.proveedor;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.admin.Proveedor;
import com.ibiscus.shopnchek.domain.admin.ProveedorRepository;

public class SaveProveedorCommand implements Command<Proveedor> {

	private ProveedorRepository proveedorRepository;

	private Long id;

	private String description;

	private String email;

	public SaveProveedorCommand() {
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Proveedor execute() {
		Proveedor proveedor = null;
		if (id != null) {
			proveedor = proveedorRepository.get(id);
			proveedor.update(description, email);
		} else {
			proveedor = new Proveedor(description, email);
			proveedorRepository.save(proveedor);
		}
		return proveedor;
	}

	public void setProveedorRepository(final ProveedorRepository proveedorRepository) {
		this.proveedorRepository = proveedorRepository;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

    public void setEmail(final String email) {
        this.email = email;
    }
}
