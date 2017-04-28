package com.ibiscus.shopnchek.application.order;

import java.util.Date;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.admin.MedioPago;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.OrderState;

public class SaveOrderCommand implements Command<OrdenPago> {

    private OrderRepository orderRepository;

    private Long numero;

    private int tipoProveedor;

    private int proveedor;

    private String tipoFactura;

    private int medioPagoId;

    private Date fechaPago;

    private String numeroFactura;

    private double iva;

    private String localidad;

    private String observaciones;

    private String observacionesShopper;

    private String cuit;

    private String banco;

    private String cbu;

    private String accountNumber;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public OrdenPago execute() {
        OrdenPago order = null;
        MedioPago medioPago = orderRepository.getMedioPago(medioPagoId);
        if (numero == null) {
            OrderState state = orderRepository.getOrderState(1);
            order = new OrdenPago(tipoProveedor, proveedor, tipoFactura,
                    medioPago, fechaPago, state, iva, numeroFactura, localidad,
                    observaciones, observacionesShopper, cuit, banco, cbu,
                    accountNumber);
            orderRepository.save(order);
        } else {
            order = orderRepository.get(numero);
            order.update(tipoProveedor, proveedor, tipoFactura, medioPago, fechaPago, iva,
                    numeroFactura, localidad, observaciones,
                    observacionesShopper, cuit, banco, cbu, accountNumber);
        }
        return order;
    }

    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void setNumero(final Long numero) {
        this.numero = numero;
    }

    public void setTipoProveedor(int tipoProveedor) {
        this.tipoProveedor = tipoProveedor;
    }

    public void setProveedor(int proveedor) {
        this.proveedor = proveedor;
    }

    public void setMedioPagoId(int medioPagoId) {
        this.medioPagoId = medioPagoId;
    }

    public void setTipoFactura(String tipoFactura) {
        this.tipoFactura = tipoFactura;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public void setNumeroFactura(final String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public void setObservacionesShopper(String observacionesShopper) {
        this.observacionesShopper = observacionesShopper;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public void setCbu(String cbu) {
        this.cbu = cbu;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

}
