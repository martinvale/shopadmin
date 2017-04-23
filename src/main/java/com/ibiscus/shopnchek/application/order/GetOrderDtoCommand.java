package com.ibiscus.shopnchek.application.order;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.application.proveedor.TitularDTO;
import com.ibiscus.shopnchek.domain.account.Account;
import com.ibiscus.shopnchek.domain.account.AccountRepository;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.Proveedor;
import com.ibiscus.shopnchek.domain.admin.ProveedorRepository;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;

public class GetOrderDtoCommand implements Command<OrderDto> {

    private OrderRepository orderRepository;

    private ShopperRepository shopperRepository;

    private ProveedorRepository proveedorRepository;

    private AccountRepository accountRepository;

    private long numero;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public OrderDto execute() {
        OrdenPago order = orderRepository.get(numero);

        String titular = null;
        String titularCuenta = null;
        if (order.getTipoProveedor().intValue() == TitularDTO.SHOPPER) {
            Shopper shopper = shopperRepository.get(order.getProveedor());
            if (shopper != null) {
                titular = shopper.getName();
                titularCuenta = titular;
            }
        } else {
            Proveedor proveedor = proveedorRepository.get(order.getProveedor());
            titular = proveedor.getDescription();
            titularCuenta = titular;
        }
        Account account = accountRepository.findByTitular(
                order.getTipoProveedor(), order.getProveedor());
        if (account != null && account.getBillingId() != null
                && account.getBillingTipo() != null) {
            if (account.getBillingTipo() == 1) {
                Shopper shopper = shopperRepository.get(account.getBillingId());
                titularCuenta = shopper.getName();
            } else {
                Proveedor proveedor = proveedorRepository.get(account
                        .getBillingId());
                titularCuenta = proveedor.getDescription();
            }
        }
        OrderDto orderDto = new OrderDto(order, titular, titularCuenta,
                order.getCuit(), order.getBanco(), order.getCbu(),
                order.getObservaciones(), order.getObservacionesShopper());

        return orderDto;
    }

    public void setOrderRepository(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void setShopperRepository(final ShopperRepository shopperRepository) {
        this.shopperRepository = shopperRepository;
    }

    public void setProveedorRepository(final ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public void setAccountRepository(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void setNumero(final long numero) {
        this.numero = numero;
    }

}
