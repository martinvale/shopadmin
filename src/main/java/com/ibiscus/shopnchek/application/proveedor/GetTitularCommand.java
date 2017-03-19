package com.ibiscus.shopnchek.application.proveedor;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.account.Account;
import com.ibiscus.shopnchek.domain.account.AccountRepository;
import com.ibiscus.shopnchek.domain.admin.Proveedor;
import com.ibiscus.shopnchek.domain.admin.ProveedorRepository;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;

public class GetTitularCommand implements Command<TitularDTO> {

	private ProveedorRepository proveedorRepository;

	private ShopperRepository shopperRepository;

	private AccountRepository accountRepository;

	private long titularId;

	private int titularTipo;

	public GetTitularCommand() {
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public TitularDTO execute() {
		String titularNombre = null;
		String loginShopmetrics = null;
		if (titularTipo == 1) {
			Shopper shopper = shopperRepository.get(titularId);
			titularNombre = shopper.getName();
			loginShopmetrics = shopper.getLogin();
		} else {
			Proveedor proveedor = proveedorRepository.get(titularId);
			titularNombre = proveedor.getDescription();
		}
		String cuit = null;
		String factura = null;
		String banco = null;
		String cbu = null;
		String number = null;
		Account account = accountRepository.findByTitular(titularTipo, titularId);
		if (account != null) {
			cuit = account.getCuit();
			factura = account.getFactura();
			banco = account.getBanco();
			cbu = account.getCbu();
			number = account.getNumber();
		}
		TitularDTO titular = new TitularDTO(titularId, titularTipo, titularNombre, loginShopmetrics, cuit, factura,
				banco, cbu, number);
		return titular;
	}

	public void setTitularId(final long titularId) {
		this.titularId = titularId;
	}

	public void setTitularTipo(final int titularTipo) {
		this.titularTipo = titularTipo;
	}

	public void setProveedorRepository(final ProveedorRepository proveedorRepository) {
		this.proveedorRepository = proveedorRepository;
	}

	public void setShopperRepository(final ShopperRepository shopperRepository) {
		this.shopperRepository = shopperRepository;
	}

	public void setAccountRepository(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}
}
