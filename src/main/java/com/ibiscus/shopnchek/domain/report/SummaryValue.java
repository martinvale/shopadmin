package com.ibiscus.shopnchek.domain.report;

public class SummaryValue implements Value {

    private final Double honorarios;
    private final Double reintegros;
    private final Double otrosGastos;

    public SummaryValue(Double honorarios, Double reintegros, Double otrosGastos) {
        this.honorarios = honorarios;
        this.reintegros = reintegros;
        this.otrosGastos = otrosGastos;
    }

    public Double getHonorarios() {
        return honorarios;
    }

    public Double getReintegros() {
        return reintegros;
    }

    public Double getOtrosGastos() {
        return otrosGastos;
    }

}
