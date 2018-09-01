package com.ibiscus.shopnchek.domain.report;

public class RowValues {

    private final Key key;
    private final SummaryValue minuendo;
    private SummaryValue sustraendo;

    public RowValues(Key key, SummaryValue value) {
        this.key = key;
        this.minuendo = value;
    }

    public Key getKey() {
        return key;
    }

    public int getYear() {
        return key.getYear();
    }

    public int getMonth() {
        return key.getMonth();
    }

    public void updateSustraendo (SummaryValue sustraendo) {
        this.sustraendo = sustraendo;
    }

    public Double getHonorariosMinuendo() {
        return minuendo.getHonorarios();
    }

    public Double getReintegrosMinuendo() {
        return minuendo.getReintegros();
    }

    public Double getOtrosMinuendo() {
        return minuendo.getOtrosGastos();
    }

    public Double getHonorariosSustraendo() {
        if (sustraendo != null) {
            return sustraendo.getHonorarios();
        }
        return 0d;
    }

    public Double getReintegrosSustraendo() {
        if (sustraendo != null) {
            return sustraendo.getReintegros();
        }
        return 0d;
    }

    public Double getOtrosSustraendo() {
        if (sustraendo != null) {
            return sustraendo.getOtrosGastos();
        }
        return 0d;
    }

    public Double getHonorariosResto() {
        return getHonorariosMinuendo() - getHonorariosSustraendo();
    }

    public Double getReintegrosResto() {
        return getReintegrosMinuendo() - getReintegrosSustraendo();
    }

    public Double getOtrosResto() {
        return getOtrosMinuendo() - getOtrosSustraendo();
    }
}
