package com.ibiscus.shopnchek.domain.admin;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "items_orden")
public class ItemOrden {

  @Id
  @Column(name="id")
  @GeneratedValue
  private long id;

  @ManyToOne
  @JoinColumn(name = "orden_nro")
  private OrdenPago ordenPago;

  @Column(name = "cliente")
  private String cliente;

  @Column(name = "sucursal")
  private String sucursal;

  @Column(name = "mes")
  private Integer mes;

  @Column(name = "anio")
  private Integer anio;

  @Column(name = "fecha")
  private Date fecha;

  @Column(name = "importe")
  private double importe;

  @Column(name = "shopper_dni")
  private String shopperDni;

  @Column(name = "asignacion")
  private Integer asignacion;

  @Column(name = "tipo_item")
  private int tipoItem;

  @Column(name = "tipo_pago")
  private int tipoPago;

  @Column(name = "usuario_autorizacion_1")
  private long usuarioAutorizacion;

  @Column(name = "estado")
  private int estado;

  /** Constructor for Hibernate. */
  ItemOrden() {
  }

  public ItemOrden(final String theShopperDni, final Integer unaAsignacion,
      final Integer unTipoItem, final int unTipoPago, final String unCliente,
      final String unaSucursal, final int unMes, final int unAnio,
      final Date unaFecha, final double unImporte, final int unEstado) {
    shopperDni = theShopperDni;
    asignacion = unaAsignacion;
    tipoPago = unTipoPago;
    tipoItem = unTipoItem;
    cliente = unCliente;
    sucursal = unaSucursal;
    mes = unMes;
    anio = unAnio;
    fecha = unaFecha;
    importe = unImporte;
    estado = unEstado;
  }

  public long getId() {
    return id;
  }

  /**
   * @return the cliente
   */
  public String getCliente() {
    return cliente;
  }

  /**
   * @return the sucursal
   */
  public String getSucursal() {
    return sucursal;
  }

  /**
   * @return the mes
   */
  public Integer getMes() {
    return mes;
  }

  /**
   * @return the anio
   */
  public Integer getAnio() {
    return anio;
  }

  /**
   * @return the importe
   */
  public double getImporte() {
    return importe;
  }

  /**
   * @return the shopperDni
   */
  public String getShopperDni() {
    return shopperDni;
  }

  /**
   * @return the asignacion
   */
  public Integer getAsignacion() {
    return asignacion;
  }

  /**
   * @return the tipoItem
   */
  public int getTipoItem() {
    return tipoItem;
  }
}
