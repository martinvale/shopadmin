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
import javax.persistence.Transient;

import com.ibiscus.shopnchek.domain.admin.Shopper;

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
	@JoinColumn(name = "client_id")
	private Client client;

	@Column(name = "client_description", length = 255)
	private String clientDescription;

	@ManyToOne
	@JoinColumn(name = "branch_id")
	private Branch branch;

	@Column(name = "branch_description", length = 255)
	private String branchDescription;

	@Column(name = "external_id")
	private Long externalId;

	@Column(name = "estado", nullable = false)
	@Enumerated(EnumType.STRING)
	private State estado = State.pendiente;

	@Column(name = "fecha_creacion", nullable = false)
	private Date fechaCreacion;

	@Column(name = "fecha_modificacion", nullable = false)
	private Date fechaModificacion;

	@Column(name = "usuario", length = 50)
	private String usuario;

	@Transient
	private Shopper shopper;

	/** Default constructor for Hibernate. */
	Debt() {
	}

	public Debt(final TipoItem tipoItem, final TipoPago tipoPago, final String shopperDni,
			final double importe, final Date fecha, final String observaciones,
			final String survey, final Client client, final String clientDescription,
			final Branch branch, final String branchDescription, final Long externalId,
			final String operator) {
		this.tipoItem = tipoItem;
		this.tipoPago = tipoPago;
		this.shopperDni = shopperDni;
		this.importe = importe;
		this.fecha = fecha;
		this.observaciones = observaciones;
		this.survey = survey;
		this.client = client;
		this.clientDescription = clientDescription;
		this.branch = branch;
		this.branchDescription = branchDescription;
		this.externalId = externalId;
		this.fechaCreacion = new Date();
		this.fechaModificacion = new Date();
		this.usuario = operator;
	}

	public void update(final TipoItem tipoItem, final TipoPago tipoPago, final String shopperDni,
			final double importe, final Date fecha, final String observaciones,
			final String survey, final Client client, final String clientDescription,
			final Branch branch, final String branchDescription, final Long externalId,
			final String operator) {
		this.tipoItem = tipoItem;
		this.tipoPago = tipoPago;
		this.shopperDni = shopperDni;
		this.importe = importe;
		this.fecha = fecha;
		this.observaciones = observaciones;
		this.survey = survey;
		this.client = client;
		this.clientDescription = clientDescription;
		this.branch = branch;
		this.branchDescription = branchDescription;
		this.externalId = externalId;
		this.fechaModificacion = new Date();
		this.usuario = operator;
	}

	public void updateShopper(final Shopper shopper) {
		this.shopper = shopper;
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

	public String getClientDescription() {
		return clientDescription;
	}

	public Branch getBranch() {
		return branch;
	}

	public String getBranchDescription() {
		return branchDescription;
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

	public String getUsuario() {
		return usuario;
	}

	public Shopper getShopper() {
		return shopper;
	}

	public void pagado() {
		estado = State.pagada;
		fechaModificacion = new Date();
	}

	public void asignada() {
		estado = State.asignada;
		fechaModificacion = new Date();
	}

	public void release() {
		estado = State.pendiente;
		fechaModificacion = new Date();
	}
}
