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

	private int tipoPago;

	private String usuario;

	private String observacion;

	public DebtDto(final Debt debt) {
		id = debt.getId();
		asignacion = debt.getExternalId();
		shopperDni = debt.getShopperDni();
		empresa = debt.getClient().getName();
		programa = debt.getSurvey();
		if (debt.getBranch() != null) {
			local = debt.getBranch().getAddress();
		}
		//nombre = unNombre;
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		fecha = format.format(debt.getFecha());
		descripcion = debt.getTipoPago().toString().toUpperCase();
		importe = debt.getImporte();
		usuario = debt.getUsuario();
		observacion = debt.getObservaciones();
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

	public int getTipoPago() {
		return tipoPago;
	}

	public String getUsuario() {
		return usuario;
	}

	public String getObservacion() {
		return observacion;
	}
}
