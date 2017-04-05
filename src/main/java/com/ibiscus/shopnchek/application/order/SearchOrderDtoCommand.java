package com.ibiscus.shopnchek.application.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibiscus.shopnchek.application.SearchCommand;
import com.ibiscus.shopnchek.application.proveedor.TitularDTO;
import com.ibiscus.shopnchek.domain.admin.ItemOrden;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.OrderState;
import com.ibiscus.shopnchek.domain.admin.Proveedor;
import com.ibiscus.shopnchek.domain.admin.ProveedorRepository;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;

public class SearchOrderDtoCommand extends SearchCommand<OrderDto> {

    private OrderRepository orderRepository;

    private ShopperRepository shopperRepository;

    private ProveedorRepository proveedorRepository;

    private Integer tipoTitular;

    private Integer titularId;

    private String dniShopper;

    private String numeroCheque;

    private Long stateId;

    private Date fechaPagoDesde;

    private Date fechaPagoHasta;

    public SearchOrderDtoCommand() {
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

    public void setTipoTitular(final Integer tipoTitular) {
        this.tipoTitular = tipoTitular;
    }

    public void setTitularId(final Integer titularId) {
        this.titularId = titularId;
    }

    public void setDniShopper(final String dniShopper) {
        this.dniShopper = dniShopper;
    }

    public void setNumeroCheque(final String numeroCheque) {
        this.numeroCheque = numeroCheque;
    }

    public void setStateId(final Long stateId) {
        this.stateId = stateId;
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

        List<OrdenPago> orderItems = orderRepository.find(getStart(),
                getPageSize(), getOrderBy(), isAscending(), tipoTitular,
                titularId, dniShopper, numeroCheque, getState(), fechaPagoDesde, fechaPagoHasta);
        for (OrdenPago order : orderItems) {
            OrderDto orderDto = null;
            String titular = null;
            if (order.getTipoProveedor().intValue() == TitularDTO.SHOPPER) {
                Shopper shopper = shopperRepository.get(order.getProveedor());
                if (shopper != null) {
                    titular = shopper.getName();
                }
            } else {
                Proveedor proveedor = proveedorRepository.get(order.getProveedor());
                titular = proveedor.getDescription();
            }
            if (!StringUtils.isBlank(dniShopper)) {
                double importe = 0;
                for (ItemOrden itemOrden : order.getItems()) {
                    if (dniShopper.equals(itemOrden.getShopperDni())) {
                        importe = importe + itemOrden.getImporte();
                    }
                }
                orderDto = new OrderDto(order, titular, order.getCuit(), order.getBanco(), order.getCbu(), importe);
            } else {
                orderDto = new OrderDto(order, titular, order.getCuit(), order.getBanco(), order.getCbu());
            }
            orderDtoItems.add(orderDto);
        }
        return orderDtoItems;
    }

    @Override
    protected int getCount() {
        return orderRepository.getCount(tipoTitular, titularId, dniShopper,
                numeroCheque, getState(), null, null);
    }

    private OrderState getState() {
        OrderState state = null;
        if (stateId != null) {
            state = orderRepository.getOrderState(stateId);
        }
        return state;
    }

}
