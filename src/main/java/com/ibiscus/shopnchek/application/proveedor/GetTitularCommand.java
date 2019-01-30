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
        String email = null;
        if (titularTipo == 1) {
            Shopper shopper = shopperRepository.get(titularId);
            titularNombre = shopper.getName();
            loginShopmetrics = shopper.getLoginShopmetrics();
            email = shopper.getEmail();
        } else {
            Proveedor proveedor = proveedorRepository.get(titularId);
            titularNombre = proveedor.getDescription();
            email = proveedor.getEmail();
        }
        String cuit = null;
        String factura = null;
        String banco = null;
        String cbu = null;
        String number = null;
        boolean linked = false;
        Long billingId = null;
        Integer billingTipo = null;
        String billingName = titularNombre;
        Account account = accountRepository.findByTitular(titularTipo,
                titularId);
        if (account != null) {
            linked = account.isLinked();
            if (linked && account.getBillingId() != null && account.getBillingTipo() != null) {
                Account billingAccount = accountRepository.findByTitular(account.getBillingTipo(), account.getBillingId());
                cuit = billingAccount.getCuit();
                factura = billingAccount.getFactura();
                banco = billingAccount.getBanco();
                cbu = billingAccount.getCbu();
                number = billingAccount.getNumber();
            } else {
                cuit = account.getCuit();
                factura = account.getFactura();
                banco = account.getBanco();
                cbu = account.getCbu();
                number = account.getNumber();
            }
            if (account.getBillingId() != null && account.getBillingTipo() != null) {
                billingId = account.getBillingId();
                billingTipo = account.getBillingTipo();
                if (billingTipo == 1) {
                    Shopper shopper = shopperRepository.get(billingId);
                    billingName = shopper.getName();
                } else {
                    Proveedor proveedor = proveedorRepository.get(billingId);
                    billingName = proveedor.getDescription();
                }
            }
        }
        TitularDTO titular = new TitularDTO(titularId, titularTipo,
                titularNombre, email, loginShopmetrics, cuit, factura, banco,
                cbu, number, linked, billingId, billingTipo, billingName);
        return titular;
    }

    public void setTitularId(final long titularId) {
        this.titularId = titularId;
    }

    public void setTitularTipo(final int titularTipo) {
        this.titularTipo = titularTipo;
    }

    public void setProveedorRepository(
            final ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public void setShopperRepository(final ShopperRepository shopperRepository) {
        this.shopperRepository = shopperRepository;
    }

    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}
