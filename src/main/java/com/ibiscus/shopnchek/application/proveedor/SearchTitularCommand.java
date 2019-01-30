package com.ibiscus.shopnchek.application.proveedor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ibiscus.shopnchek.application.SearchCommand;
import com.ibiscus.shopnchek.domain.account.Account;
import com.ibiscus.shopnchek.domain.account.AccountRepository;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.Proveedor;
import com.ibiscus.shopnchek.domain.admin.ProveedorRepository;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;

public class SearchTitularCommand extends SearchCommand<TitularDTO> {

    private ProveedorRepository proveedorRepository;

    private ShopperRepository shopperRepository;

    private AccountRepository accountRepository;

    private OrderRepository orderRepository;

    private String name;

    public SearchTitularCommand() {
    }

    @Override
    protected List<TitularDTO> getItems() {
        List<TitularDTO> titulares = new ArrayList<TitularDTO>();
        List<Proveedor> proveedores = proveedorRepository.find(getStart(),
                getPageSize(), name);
        for (Proveedor proveedor : proveedores) {
            titulares.add(getTitularDTO(proveedor.getId(), TitularDTO.PROVEEDOR,
                    proveedor.getDescription(), proveedor.getEmail(), null));
        }
        List<Shopper> shoppers = shopperRepository.find(name);
        for (Shopper shopper : shoppers) {
            titulares.add(getTitularDTO(shopper.getId(), TitularDTO.SHOPPER,
                    shopper.getName(), shopper.getEmail(), shopper.getLoginShopmetrics()));
        }
        Collections.sort(titulares, new Comparator<TitularDTO>() {

            @Override
            public int compare(TitularDTO o1, TitularDTO o2) {
                return o1.getName().compareTo(o2.getName());
            }

        });
        return titulares;
    }

    private TitularDTO getTitularDTO(final long titularId, final int titularTipo,
            final String name, final String email, final String loginShopmetrics) {
        String cuit = null;
        String factura = null;
        String banco = null;
        String cbu = null;
        String number = null;
        boolean linked = false;
        Long billingId = null;
        Integer billingTipo = null;
        String billingName = name;
        Account account = accountRepository.findByTitular(titularTipo, titularId);
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
        String ultimaFactura = null;
        OrdenPago order = orderRepository.getLastCreatedOrder(titularTipo, titularId);
        if (order != null) {
            ultimaFactura = order.getNumeroFactura();
        }
        return new TitularDTO(titularId, titularTipo, name, email,
                loginShopmetrics, cuit, factura, banco, cbu, number, linked, billingId,
                billingTipo, billingName, ultimaFactura);
    }

    @Override
    protected int getCount() {
        return proveedorRepository.getProveedoresCount(name);
    }

    public void setProveedorRepository(
            final ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public void setShopperRepository(ShopperRepository shopperRepository) {
        this.shopperRepository = shopperRepository;
    }

    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void setName(final String name) {
        this.name = name;
    }

}