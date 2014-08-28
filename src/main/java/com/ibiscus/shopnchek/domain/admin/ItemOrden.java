package com.ibiscus.shopnchek.domain.admin;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "items_orden")
public class ItemOrden {

  @Id
  @Column(name="id")
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
  private String fecha;

  @Column(name = "importe")
  private double importe;

  @Column(name = "shopper_dni")
  private String shopperDni;

  @Column(name = "asignacion")
  private Integer asignacion;

  @Column(name = "tipo_item")
  private int tipoItem;

  @ManyToOne
  @JoinColumn(name = "tipo_pago")
  private TipoPago tipoPago;

  @Column(name = "usuario_autorizacion_1")
  private long usuarioAutorizacion;

  @Column(name = "descripcion")
  private String descripcion;

  @Column(name = "estado")
  private int estado;

  @Column(name = "fecha_insercion")
  private Date fechaInsercion = new Date();

  @Transient
  private Shopper shopper;

  /** Constructor for Hibernate. */
  ItemOrden() {
  }

  public ItemOrden(final long theId, final OrdenPago unaOrden,
      final long usuario, final String theShopperDni, final Integer unaAsignacion,
      final Integer unTipoItem, final TipoPago unTipoPago, final String unCliente,
      final String unaSucursal, final int unMes, final int unAnio,
      final String unaFecha, final double unImporte, final String unaDescripcion,
      final int unEstado) {
    id = theId;
    ordenPago = unaOrden;
    usuarioAutorizacion = usuario;
    shopperDni = theShopperDni;
    asignacion = unaAsignacion;
    tipoItem = unTipoItem;
    tipoPago = unTipoPago;
    cliente = unCliente;
    sucursal = unaSucursal;
    mes = unMes;
    anio = unAnio;
    fecha = unaFecha;
    importe = unImporte;
    descripcion = unaDescripcion;
    estado = unEstado;
  }

  public void updateShopper(final Shopper unShopper) {
    shopper = unShopper;
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

  public String getFecha() {
    return fecha;
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

  public TipoPago getTipoPago() {
    return tipoPago;
  }

  public Shopper getShopper() {
    return shopper;
  }
}
