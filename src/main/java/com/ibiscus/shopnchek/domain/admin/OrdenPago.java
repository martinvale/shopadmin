package com.ibiscus.shopnchek.domain.admin;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name="ordenes")
public class OrdenPago {

  @Id
  @Column(name="numero")
  private long numero;

  @Column(name="proveedor_tipo")
  private Integer tipoProveedor;

  @Column(name="proveedor")
  private Integer proveedor;

  @Column(name="chequera_nro")
  private String numeroChequera;

  @Column(name="cheque_nro")
  private String numeroCheque;

  @Column(name="fecha_cheque")
  private Date fechaCheque;

  @Column(name="fecha_pago")
  private Date fechaPago;

  @ManyToOne
  @JoinColumn(name = "estado")
  private OrderState estado;

  @Column(name="factura", length=3)
  private String tipoFactura;

  @Column(name="factura_nro", length=50)
  private String numeroFactura;

  @Column(name="iva_honorarios")
  private double iva;

  @Column(name="localidad", length=15)
  private String localidad;

  @Column(name="transferenciap")
  private boolean transferencia;

  @Column(name="transferencia_id", length=15)
  private String idTransferencia;

  @Column(name="observaciones")
  private String observaciones;

  @ManyToOne
  @JoinColumn(name = "medio_pago")
  private MedioPago medioPago;

  @Column(name="obspshopper")
  private String observacionesShopper;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "orden_nro")
  @Fetch(value = FetchMode.SELECT)
  private List<ItemOrden> items = new LinkedList<ItemOrden>();

  /** Default constructor for Hibernate. */
  OrdenPago() {
  }

  public OrdenPago(final int theTipoProveedor, final int theProveedor,
      final String theTipoFactura, final Date thePayDate,
      final OrderState theState, final MedioPago theMedioPago,
      final double theIva) {
    tipoProveedor = theTipoProveedor;
    proveedor = theProveedor;
    tipoFactura = theTipoFactura;
    fechaPago = thePayDate;
    estado = theState;
    medioPago = theMedioPago;
    iva = theIva;
  }

  public void update(final int theTipoProveedor, final int theProveedor,
      final String theTipoFactura, final Date thePayDate,
      final OrderState theState, final MedioPago theMedioPago,
      final double theIva, final String unNumeroFactura,
      final Date unaFechaCheque, final String unNumeroChequera,
      final String unChequeNumero, final String unTransferId,
      final String unaLocalidad, final String unaObservacion,
      final String unaObservacionShopper) {
    tipoProveedor = theTipoProveedor;
    proveedor = theProveedor;
    tipoFactura = theTipoFactura;
    fechaPago = thePayDate;
    estado = theState;
    medioPago = theMedioPago;
    numeroFactura = unNumeroFactura;
    fechaCheque = unaFechaCheque;
    numeroChequera = unNumeroChequera;
    numeroCheque = unChequeNumero;
    idTransferencia = unTransferId;
    localidad = unaLocalidad;
    observaciones = unaObservacion;
    observacionesShopper = unaObservacionShopper;
  }

  public void updateNumero(final long unNumero) {
    numero = unNumero;
  }

  /**
   * @return the numero
   */
  public long getNumero() {
    return numero;
  }

  /**
   * @return the tipoProveedor
   */
  public Integer getTipoProveedor() {
    return tipoProveedor;
  }

  /**
   * @return the proveedor
   */
  public Integer getProveedor() {
    return proveedor;
  }

  /**
   * @return the numeroChequera
   */
  public String getNumeroChequera() {
    return numeroChequera;
  }

  /**
   * @return the numeroCheque
   */
  public String getNumeroCheque() {
    return numeroCheque;
  }

  /**
   * @return the fechaCheque
   */
  public Date getFechaCheque() {
    return fechaCheque;
  }

  /**
   * @return the fechaPago
   */
  public Date getFechaPago() {
    return fechaPago;
  }

  /**
   * @return the estado
   */
  public OrderState getEstado() {
    return estado;
  }

  /**
   * @return the tipoFactura
   */
  public String getTipoFactura() {
    return tipoFactura;
  }

  /**
   * @return the numeroFactura
   */
  public String getNumeroFactura() {
    return numeroFactura;
  }

  /**
   * @return the iva
   */
  public double getIva() {
    return iva;
  }

  /**
   * @return the localidad
   */
  public String getLocalidad() {
    return localidad;
  }

  /**
   * @return the transferencia
   */
  public boolean isTransferencia() {
    return transferencia;
  }

  /**
   * @return the idTransferencia
   */
  public String getIdTransferencia() {
    return idTransferencia;
  }

  /**
   * @return the observaciones
   */
  public String getObservaciones() {
    return observaciones;
  }

  /**
   * @return the medioPago
   */
  public MedioPago getMedioPago() {
    return medioPago;
  }

  /**
   * @return the observacionesShopper
   */
  public String getObservacionesShopper() {
    return observacionesShopper;
  }

  public List<ItemOrden> getItems() {
    return items;
  }

  void updateNumber(final int theNumber) {
    numero = theNumber;
  }

  public double getImporte() {
    double importe = 0;
    for (ItemOrden unItemOrden : items) {
      importe += unItemOrden.getImporte();
    }
    return importe;
  }

  public double getHonorarios() {
    double honorarios = 0;
    for (ItemOrden unItemOrden : items) {
      if (unItemOrden.getTipoPago().getDescription().equals("HONORARIOS")) {
        honorarios += unItemOrden.getImporte();
      }
    }
    return honorarios;
  }

  public double getReintegros() {
    double reintegros = 0;
    for (ItemOrden unItemOrden : items) {
      if (unItemOrden.getTipoPago().getDescription().equals("REINTEGROS")) {
        reintegros += unItemOrden.getImporte();
      }
    }
    return reintegros;
  }

  public double getOtrosGastos() {
    double otrosGastos = 0;
    for (ItemOrden unItemOrden : items) {
      if (unItemOrden.getTipoPago().getDescription().equals("OTROS GASTOS")) {
        otrosGastos += unItemOrden.getImporte();
      }
    }
    return otrosGastos;
  }
}
