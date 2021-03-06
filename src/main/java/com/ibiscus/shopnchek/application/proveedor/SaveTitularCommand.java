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

public class SaveTitularCommand implements Command<TitularDTO> {

	private ProveedorRepository proveedorRepository;

	private ShopperRepository shopperRepository;

	private AccountRepository accountRepository;

	private long titularId;

	private int titularTipo;

	private String name;

	private String email;

	private String loginShopmetrics;

	private boolean linked;

	private String cuit;

	private String factura;

	private String banco;

	private String cbu;

	private String number;

	private Long billingId;

	private Integer billingTipo;

	public SaveTitularCommand() {
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public TitularDTO execute() {
		Account account = accountRepository.findByTitular(titularTipo, titularId);
		if (account != null) {
			account.update(cuit, factura, banco, cbu, number, linked, billingId, billingTipo);
		} else {
			account = new Account(titularId, titularTipo, cuit, factura, banco, cbu, number,
			        linked, billingId, billingTipo);
			accountRepository.save(account);
		}
		if (titularTipo == 1) {
			Shopper shopper = shopperRepository.get(titularId);
			shopper.update(loginShopmetrics);
		} else {
			Proveedor proveedor = proveedorRepository.get(titularId);
			proveedor.update(name, email);
		}
		String billingName = null;
        if (billingId != null && billingTipo != null) {
            if (billingTipo == 1) {
                Shopper shopper = shopperRepository.get(billingId);
                billingName = shopper.getName();
            } else {
                Proveedor proveedor = proveedorRepository.get(billingId);
                billingName = proveedor.getDescription();
            }
        }
		return new TitularDTO(titularId, titularTipo, name, email, loginShopmetrics, cuit,
		        factura, banco, cbu, number, linked, billingId, billingTipo, billingName);
	}

	public void setTitularId(final long titularId) {
		this.titularId = titularId;
	}

	public void setTitularTipo(final int titularTipo) {
		this.titularTipo = titularTipo;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(final String email) {
	    this.email = email;
	}

    public void setLoginShopmetrics(String loginShopmetrics) {
		this.loginShopmetrics = loginShopmetrics;
	}

    public void setLinked(boolean linked) {
        this.linked = linked;
    }

    public void setBillingId(Long billingId) {
        this.billingId = billingId;
    }

    public void setBillingTipo(Integer billingTipo) {
        this.billingTipo = billingTipo;
    }

    public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public void setFactura(String factura) {
		this.factura = factura;
	}

	public void setBanco(String banco) {
		this.banco = banco;
	}

	public void setCbu(String cbu) {
		this.cbu = cbu;
	}

	public void setNumber(String number) {
		this.number = number;
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
