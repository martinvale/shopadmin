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
@Table(name="items_adicionales_autorizados")
public class AutorizacionAdicional {

  @Id
  @Column(name="id")
  private int id;

  @Column(name="grupo")
  private int group;

  @Column(name="cliente")
  private int clienteId;

  @Column(name="cliente_nombre")
  private String clienteNombre;

  @Column(name="sucursal")
  private String sucursalId;

  @Column(name="sucursal_nombre")
  private String sucursalNombre;

  @Column(name="shopper_dni")
  private String shopperDni;

  @Column(name="mes_trabajo")
  private Integer mes;

  @Column(name="anio_trabajo")
  private Integer anio;

  @Column(name="fecha_visita")
  private Date fecha;

  @Column(name="fecha_cobro")
  private Date fechaCobro;

  @Column(name="observaciones")
  private String observacion;

  @Column(name="importe")
  private double importe;

  @Column(name="estado")
  private Integer estado = 1;

  @ManyToOne
  @JoinColumn(name = "tipo_pago")
  private TipoPago tipoPago;

  @Column(name="tipo_item")
  private int tipoItem;

  @Column(name="opnro")
  private Integer nroOperacion;

  @Column(name="usuario_autorizacion")
  private String username;

  @Transient
  private Shopper shopper;

  public AutorizacionAdicional() {
  }

  public AutorizacionAdicional(final Integer unGrupo, final int unClientId,
      final String unClienteNombre,
      final String unaSucursalId, final String unaSucursalNombre,
      final String unShopperDni,
      final int unMes, final int unAnio,
      final Date unaFecha, final Date unaFechaCobro,
      final String unaObservacion,
      final double unImporte, final TipoPago unTipoPago,
      final int unTipoItem, final String unUsername) {
    group = unGrupo;
    clienteId = unClientId;
    clienteNombre = unClienteNombre;
    sucursalId = unaSucursalId;
    sucursalNombre = unaSucursalNombre;
    shopperDni = unShopperDni;
    mes = unMes;
    anio = unAnio;
    fecha = unaFecha;
    fechaCobro = unaFechaCobro;
    importe = unImporte;
    observacion = unaObservacion;
    tipoPago = unTipoPago;
    tipoItem = unTipoItem;
    username = unUsername;
  }

  public void update(final Date unaFechaCobro, final String unaObservacion,
      final double unImporte, final TipoPago unTipoPago) {
    fechaCobro = unaFechaCobro;
    importe = unImporte;
    observacion = unaObservacion;
    tipoPago = unTipoPago;
  }

  public void updateId(final int unId) {
    id = unId;
  }

  public void updateGroup(final int unGrupo) {
    group = unGrupo;
  }

  public void updateShopper(final Shopper unShopper) {
    shopper = unShopper;
  }

  public int getId() {
    return id;
  }

  public Integer getGroup() {
    return group;
  }

  public int getClienteId() {
    return clienteId;
  }

  public String getClienteNombre() {
    return clienteNombre;
  }

  public String getSucursalId() {
    return sucursalId;
  }

  public String getSucursalNombre() {
    return sucursalNombre;
  }

  public String getShopperDni() {
    return shopperDni;
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
   * @return the fecha
   */
  public Date getFecha() {
    return fecha;
  }

  public Date getFechaCobro() {
    return fechaCobro;
  }

  /**
   * @return the importe
   */
  public double getImporte() {
    return importe;
  }

  public String getObservacion() {
    return observacion;
  }

  /**
   * @return the tipoPago
   */
  public TipoPago getTipoPago() {
    return tipoPago;
  }

  public int getTipoItem() {
    return tipoItem;
  }

  public Integer getNroOperacion() {
    return nroOperacion;
  }

  public String getUsername() {
    return username;
  }

  public Shopper getShopper() {
    return shopper;
  }
}
