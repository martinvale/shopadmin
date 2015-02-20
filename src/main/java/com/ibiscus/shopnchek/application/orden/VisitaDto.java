package com.ibiscus.shopnchek.application.orden;


public class VisitaDto {

  private int tipoPago;

  private String shopperDni;

  private Integer asignacion;

  private double importe;

  private String cliente;

  private String sucursal;

  private int mes;

  private int anio;

  private String fecha;

  private int tipoItem;

  public VisitaDto() {
  }

  /**
   * @return the tipoPago
   */
  public int getTipoPago() {
    return tipoPago;
  }

  /**
   * @param tipoPago the tipoPago to set
   */
  public void setTipoPago(int tipoPago) {
    this.tipoPago = tipoPago;
  }

  /**
   * @return the shopperDni
   */
  public String getShopperDni() {
    return shopperDni;
  }

  /**
   * @param shopperDni the shopperDni to set
   */
  public void setShopperDni(String shopperDni) {
    this.shopperDni = shopperDni;
  }

  /**
   * @return the asignacion
   */
  public Integer getAsignacion() {
    return asignacion;
  }

  /**
   * @param asignacion the asignacion to set
   */
  public void setAsignacion(Integer asignacion) {
    this.asignacion = asignacion;
  }

  /**
   * @return the importe
   */
  public double getImporte() {
    return importe;
  }

  /**
   * @param importe the importe to set
   */
  public void setImporte(double importe) {
    this.importe = importe;
  }

  /**
   * @return the cliente
   */
  public String getCliente() {
    return cliente;
  }

  /**
   * @param cliente the cliente to set
   */
  public void setCliente(String cliente) {
    this.cliente = cliente;
  }

  /**
   * @return the sucursal
   */
  public String getSucursal() {
    return sucursal;
  }

  /**
   * @param sucursal the sucursal to set
   */
  public void setSucursal(String sucursal) {
    this.sucursal = sucursal;
  }

  /**
   * @return the mes
   */
  public int getMes() {
    return mes;
  }

  /**
   * @param mes the mes to set
   */
  public void setMes(int mes) {
    this.mes = mes;
  }

  /**
   * @return the anio
   */
  public int getAnio() {
    return anio;
  }

  /**
   * @param anio the anio to set
   */
  public void setAnio(int anio) {
    this.anio = anio;
  }

  /**
   * @return the fecha
   */
  public String getFecha() {
    return fecha;
  }

  /**
   * @param fecha the fecha to set
   */
  public void setFecha(String fecha) {
    this.fecha = fecha;
  }

  public int getTipoItem() {
    return tipoItem;
  }

  public void setTipoItem(int tipoItem) {
    this.tipoItem = tipoItem;
  }

}