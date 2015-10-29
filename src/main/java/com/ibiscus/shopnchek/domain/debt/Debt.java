package com.ibiscus.shopnchek.domain.debt;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="deuda")
public class Debt {

	public enum State {
		pendiente,
		asignada,
		pagada
	};

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "tipo_item", nullable = false)
	@Enumerated(EnumType.STRING)
	private TipoItem tipoItem;

	@Column(name = "tipo_pago", nullable = false)
	@Enumerated(EnumType.STRING)
	private TipoPago tipoPago;

	@Column(name = "shopper_dni", nullable = false, length = 50)
	private String shopperDni;

	@Column(name = "importe", nullable = false)
	private double importe;

	@Column(name = "fecha", nullable = false)
	private Date fecha;

	@Column(name = "observaciones", length = 200)
	private String observaciones;

	@Column(name = "survey", length = 100)
	private String survey;

	@ManyToOne
	@JoinColumn(name = "client_id", nullable = false)
	private Client client;

	@ManyToOne
	@JoinColumn(name = "branch_id")
	private Branch branch;

	@Column(name = "external_id")
	private Long externalId;

	@Column(name = "estado", nullable = false)
	@Enumerated(EnumType.STRING)
	private State estado = State.pendiente;

	@Column(name = "fecha_creacion", nullable = false)
	private Date fechaCreacion;

	@Column(name="fecha_modificacion", nullable = false)
	private Date fechaModificacion;

	/** Default constructor for Hibernate. */
	Debt() {
	}

	public Debt(final TipoItem tipoItem, final TipoPago tipoPago, final String shopperDni,
			final double importe, final Date fecha, final String observaciones,
			final String survey, final Client client, final Branch branch, final Long externalId) {
		this.tipoItem = tipoItem;
		this.tipoPago = tipoPago;
		this.shopperDni = shopperDni;
		this.importe = importe;
		this.fecha = fecha;
		this.observaciones = observaciones;
		this.survey = survey;
		this.client = client;
		this.branch = branch;
		this.externalId = externalId;
		this.fechaCreacion = new Date();
		this.fechaModificacion = new Date();
	}

	public void update(final TipoItem tipoItem, final TipoPago tipoPago, final String shopperDni,
			final double importe, final Date fecha, final String observaciones,
			final String survey, final Client client, final Branch branch, final Long externalId) {
		this.tipoItem = tipoItem;
		this.tipoPago = tipoPago;
		this.shopperDni = shopperDni;
		this.importe = importe;
		this.fecha = fecha;
		this.observaciones = observaciones;
		this.survey = survey;
		this.client = client;
		this.branch = branch;
		this.externalId = externalId;
		this.fechaModificacion = new Date();
	}

	public long getId() {
		return id;
	}

	public TipoItem getTipoItem() {
		return tipoItem;
	}

	public TipoPago getTipoPago() {
		return tipoPago;
	}

	public String getShopperDni() {
		return shopperDni;
	}

	public double getImporte() {
		return importe;
	}

	public Date getFecha() {
		return fecha;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public String getSurvey() {
		return survey;
	}

	public Client getClient() {
		return client;
	}

	public Branch getBranch() {
		return branch;
	}

	public Long getExternalId() {
		return externalId;
	}

	public State getEstado() {
		return estado;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public Date getFechaModificacion() {
		return fechaModificacion;
	}

}
