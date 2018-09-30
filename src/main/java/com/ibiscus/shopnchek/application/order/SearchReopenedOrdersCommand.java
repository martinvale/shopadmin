package com.ibiscus.shopnchek.application.order;

import com.ibiscus.shopnchek.application.SearchCommand;
import com.ibiscus.shopnchek.application.order.OrderDto;
import com.ibiscus.shopnchek.application.proveedor.TitularDTO;
import com.ibiscus.shopnchek.domain.account.Account;
import com.ibiscus.shopnchek.domain.account.AccountRepository;
import com.ibiscus.shopnchek.domain.admin.*;

import java.util.*;

public class SearchReopenedOrdersCommand extends SearchCommand<OrderDto> {

    private OrderRepository orderRepository;

    private ShopperRepository shopperRepository;

    private ProveedorRepository proveedorRepository;

    private AccountRepository accountRepository;

    private Date fechaPagoDesde;

    private Date fechaPagoHasta;

    public SearchReopenedOrdersCommand() {
    }

    public void setOrderRepository(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void setShopperRepository(ShopperRepository shopperRepository) {
        this.shopperRepository = shopperRepository;
    }

    public void setProveedorRepository(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void setFechaPagoDesde(Date fechaPagoDesde) {
        this.fechaPagoDesde = fechaPagoDesde;
    }

    public void setFechaPagoHasta(Date fechaPagoHasta) {
        this.fechaPagoHasta = fechaPagoHasta;
    }

    @Override
    protected List<OrderDto> getItems() {
        List<OrderDto> orderDtoItems = new ArrayList<OrderDto>();

        List<OrdenPago> orderItems = orderRepository.getReopenedOrders(fechaPagoDesde, fechaPagoHasta);
        for (OrdenPago order : orderItems) {
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
            Account account = accountRepository.findByTitular(order.getTipoProveedor(),
                    order.getProveedor());
            if (account != null && account.getBillingId() != null && account.getBillingTipo() != null) {
                if (account.getBillingTipo() == 1) {
                    Shopper shopper = shopperRepository.get(account.getBillingId());
                    titularCuenta = shopper.getName();
                } else {
                    Proveedor proveedor = proveedorRepository.get(account.getBillingId());
                    titularCuenta = proveedor.getDescription();
                }
            }
            OrderDto orderDto = new OrderDto(order, titular, titularCuenta, order.getCuit(), order.getBanco(), order.getCbu(),
                    order.getIdTransferencia(), order.getObservaciones(), order.getObservacionesShopper(),
                    order.getTimesReopened());
            orderDtoItems.add(orderDto);
        }
        return orderDtoItems;
    }

    @Override
    protected int getCount() {
        return orderRepository.getCount(null, null, null, null, null,
                null, null);
    }

}
