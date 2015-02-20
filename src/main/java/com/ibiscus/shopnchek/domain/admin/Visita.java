package com.ibiscus.shopnchek.domain.admin;

public class Visita {

  private String shopperDni;

  private String empresa;

  private String programa;

  private String local;

  private String nombre;

  private Integer asignacion;

  private Integer mes;

  private Integer anio;

  private String fecha;

  private String descripcion;

  private String fechaCobro;

  private Float importe;

  private int tipoItem;

  private int tipoPago;

  private String usuario;

  private String observacion;

  public Visita() {
  }

  public Visita(final Integer unaAsignacion, final String unShopperDni,
      final String unaEmpresa,
      final String unPrograma, final String unLocal, final String unNombre,
      final int unMes, final int unAnio,
      final String unaFecha,
      final String unaDescripcion, final String unaFechaCobro,
      final Float unImporte, final Integer unTipoItem, final int unTipoPago) {
    asignacion = unaAsignacion;
    shopperDni = unShopperDni;
    empresa = unaEmpresa;
    programa = unPrograma;
    local = unLocal;
    nombre = unNombre;
    mes = unMes;
    anio = unAnio;
    fecha = unaFecha;
    descripcion = unaDescripcion;
    fechaCobro = unaFechaCobro;
    importe = unImporte;
    tipoPago = unTipoPago;
    tipoItem = unTipoItem;
  }

  public void updateObservacion(final String unUsuario, final String unaObservacion) {
    usuario = unUsuario;
    observacion = unaObservacion;
  }

  /**
   * @return the shopperDni
   */
  public String getShopperDni() {
    return shopperDni;
  }

  /**
   * @return the programa
   */
  public String getEmpresa() {
    return empresa;
  }

  /**
   * @return the programa
   */
  public String getPrograma() {
    return programa;
  }

  /**
   * @return the local
   */
  public String getLocal() {
    return local;
  }

  /**
   * @return the nombre
   */
  public String getNombre() {
    return nombre;
  }

  public Integer getAsignacion() {
    return asignacion;
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
  public String getFecha() {
    return fecha;
  }

  /**
   * @return the descripcion
   */
  public String getDescripcion() {
    return descripcion;
  }

  /**
   * @return the fechaCobro
   */
  public String getFechaCobro() {
    return fechaCobro;
  }

  /**
   * @return the importe
   */
  public Float getImporte() {
    return importe;
  }

  /**
   * @return the tipoItem
   */
  public int getTipoItem() {
    return tipoItem;
  }

  /**
   * @return the tipoPago
   */
  public int getTipoPago() {
    return tipoPago;
  }

  public String getUsuario() {
    return usuario;
  }

  public String getObservacion() {
    return observacion;
  }

  /**
   * @param shopperDni the shopperDni to set
   */
  public void setShopperDni(String shopperDni) {
    this.shopperDni = shopperDni;
  }

  /**
   * @param empresa the empresa to set
   */
  public void setEmpresa(String empresa) {
    this.empresa = empresa;
  }

  /**
   * @param programa the programa to set
   */
  public void setPrograma(String programa) {
    this.programa = programa;
  }

  /**
   * @param local the local to set
   */
  public void setLocal(String local) {
    this.local = local;
  }

  /**
   * @param nombre the nombre to set
   */
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  /**
   * @param asignacion the asignacion to set
   */
  public void setAsignacion(Integer asignacion) {
    this.asignacion = asignacion;
  }

  /**
   * @param mes the mes to set
   */
  public void setMes(Integer mes) {
    this.mes = mes;
  }

  /**
   * @param anio the anio to set
   */
  public void setAnio(Integer anio) {
    this.anio = anio;
  }

  /**
   * @param fecha the fecha to set
   */
  public void setFecha(String fecha) {
    this.fecha = fecha;
  }

  /**
   * @param descripcion the descripcion to set
   */
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  /**
   * @param fechaCobro the fechaCobro to set
   */
  public void setFechaCobro(String fechaCobro) {
    this.fechaCobro = fechaCobro;
  }

  /**
   * @param importe the importe to set
   */
  public void setImporte(Float importe) {
    this.importe = importe;
  }

  /**
   * @param tipoItem the tipoItem to set
   */
  public void setTipoItem(int tipoItem) {
    this.tipoItem = tipoItem;
  }

  /**
   * @param tipoPago the tipoPago to set
   */
  public void setTipoPago(int tipoPago) {
    this.tipoPago = tipoPago;
  }

  /**
   * @param usuario the usuario to set
   */
  public void setUsuario(String usuario) {
    this.usuario = usuario;
  }

  /**
   * @param observacion the observacion to set
   */
  public void setObservacion(String observacion) {
    this.observacion = observacion;
  }

}
