package com.ibiscus.shopnchek.domain.admin;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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

  public static final int SHOPPER = 1;
  public static final int PROVEEDOR = 2;

  @Id
  @Column(name="numero", nullable=false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long numero;

  @Column(name="proveedor_tipo", nullable=false)
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

  @Column(name = "cuit", length = 25)
  private String cuit;

  @Column(name = "banco", length = 200)
  private String banco;

  @Column(name = "cbu", length = 100)
  private String cbu;

  @Column(name = "account_number", length = 50)
  private String accountNumber;

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

  @Column(name="notified")
  private boolean notified;

  @Column(name="fecha_pago_confirmada")
  private boolean fechaPagoConfirmada;

  /** Default constructor for Hibernate. */
  OrdenPago() {
  }

  public OrdenPago(final int theTipoProveedor, final int theProveedor,
      final String theTipoFactura, final MedioPago medioPago, final Date thePayDate,
      final OrderState theState, final double theIva, final String numeroFactura,
      final String localidad, final String observaciones, final String observacionesShopper,
      final String cuit, final String banco, final String cbu, final String accountNumber) {
    this.tipoProveedor = theTipoProveedor;
    this.proveedor = theProveedor;
    this.tipoFactura = theTipoFactura;
    this.medioPago = medioPago;
    this.fechaPago = thePayDate;
    this.estado = theState;
    this.iva = theIva;
    this.numeroFactura = numeroFactura;
    this.localidad = localidad;
    this.observaciones = observaciones;
    this.observacionesShopper = observacionesShopper;
    this.cuit = cuit;
    this.banco = banco;
    this.cbu = cbu;
    this.accountNumber = accountNumber;
  }

  public void update(final int theTipoProveedor, final int theProveedor,
      final String theTipoFactura, final Date thePayDate,
      final double theIva, final String unNumeroFactura,
      final String unaLocalidad, final String unaObservacion,
      final String unaObservacionShopper, final String cuit,
      final String banco, final String cbu, final String accountNumber) {
    tipoProveedor = theTipoProveedor;
    proveedor = theProveedor;
    tipoFactura = theTipoFactura;
    fechaPago = thePayDate;
    iva = theIva;
    numeroFactura = unNumeroFactura;
    localidad = unaLocalidad;
    observaciones = unaObservacion;
    observacionesShopper = unaObservacionShopper;
    this.cuit = cuit;
    this.banco = banco;
    this.cbu = cbu;
    this.accountNumber = accountNumber;
  }

  public void pagar(final OrderState state, final MedioPago medioPago, final String idTransferencia,
        final String numeroChequera, final String numeroCheque, final Date fechaCheque,
        final String observacion, final String observacionShopper) {
    this.estado = state;
    this.medioPago = medioPago;
    this.idTransferencia = idTransferencia;
    this.numeroChequera = numeroChequera;
    this.numeroCheque = numeroCheque;
    this.fechaCheque = fechaCheque;
    this.observaciones = observacion;
    this.observacionesShopper = observacionShopper;
    for (ItemOrden item : items) {
      if (item.getDebt() != null) {
        item.getDebt().pagado();
      }
    }
    if (!fechaPagoConfirmada) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        fechaPago = calendar.getTime();
        fechaPagoConfirmada = true;
    }
  }

  public void transition(final OrderState state) {
    this.estado = state;
    if (state.getId() == OrderState.VERIFICADA) {
      for (ItemOrden item : items) {
        if (item.getDebt() != null) {
          item.getDebt().pagado();
        }
      }
    } else {
      for (ItemOrden item : items) {
        if (item.getDebt() != null) {
          item.getDebt().asignada();
        }
      }
    }
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

	public String getCuit() {
		return cuit;
	}
	
	public String getBanco() {
		return banco;
	}
	
	public String getCbu() {
		return cbu;
	}
	
	public String getAccountNumber() {
		return accountNumber;
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

  public boolean estaAbierta() {
    return estado.getId() == OrderState.ABIERTA;
  }

  public boolean estaSuspendida() {
    return estado.getId() == OrderState.EN_ESPERA;
  }

  public boolean estaPagada() {
    return estado.getId() == OrderState.PAGADA;
  }

  public boolean estaCerrada() {
    return estado.getId() == OrderState.CERRADA;
  }

  public boolean estaVerificada() {
    return estado.getId() == OrderState.VERIFICADA;
  }

  public boolean estaAnulada() {
    return estado.getId() == OrderState.ANULADA;
  }

  public boolean isNotified() {
    return notified;
  }

  public void markAsNotified() {
    notified = true;
  }
}
