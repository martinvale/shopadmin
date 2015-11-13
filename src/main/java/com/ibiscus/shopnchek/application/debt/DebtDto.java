package com.ibiscus.shopnchek.application.debt;

import java.text.SimpleDateFormat;

import com.ibiscus.shopnchek.domain.debt.Debt;

public class DebtDto {

	private long id;

	private String shopperDni;

	private String empresa;

	private String programa;

	private String local;

	private String nombre;

	private Long asignacion;

	private String fecha;

	private String descripcion;

	private double importe;

	private int tipoItem;

	private String tipoPago;

	private String usuario;

	private String observacion;

	public DebtDto() {
	}

	public DebtDto(final Debt debt) {
		id = debt.getId();
		asignacion = debt.getExternalId();
		shopperDni = debt.getShopperDni();
		if (debt.getClient() != null) {
			empresa = debt.getClient().getName();
		} else {
			empresa = debt.getClientDescription();
		}
		programa = debt.getSurvey();
		if (debt.getBranch() != null) {
			local = debt.getBranch().getAddress();
		} else {
			local = debt.getBranchDescription();
		}
		//nombre = unNombre;
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		fecha = format.format(debt.getFecha());
		descripcion = debt.getTipoPago().toString().toUpperCase();
		importe = debt.getImporte();
		usuario = debt.getUsuario();
		observacion = debt.getObservaciones();
		tipoPago = debt.getTipoPago().toString();
	}

	public long getId() {
		return id;
	}

	public String getShopperDni() {
		return shopperDni;
	}

	public String getEmpresa() {
		return empresa;
	}

	public String getPrograma() {
		return programa;
	}

	public String getLocal() {
		return local;
	}

	public String getNombre() {
		return nombre;
	}

	public Long getAsignacion() {
		return asignacion;
	}

	public String getFecha() {
		return fecha;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public Double getImporte() {
		return importe;
	}

	public int getTipoItem() {
		return tipoItem;
	}

	public void setImporte(final Double importe) {
		this.importe = importe;
	}

	public String getTipoPago() {
		return tipoPago;
	}

	public void setTipoPago(final String tipoPago) {
		this.tipoPago = tipoPago;
	}

	public String getUsuario() {
		return usuario;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(final String observacion) {
		this.observacion = observacion;
	}
}
