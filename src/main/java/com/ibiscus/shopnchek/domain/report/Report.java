package com.ibiscus.shopnchek.domain.report;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class Report {

    private List<RowValues> values = newArrayList();

    public void addValue(RowValues value) {
        values.add(value);
    }

    public void updateSustraendo(Key key, SummaryValue sustraendo) {
        RowValues rowValues = getRowValues(key);
        if (rowValues != null) {
            rowValues.updateSustraendo(sustraendo);
        }
    }

    private RowValues getRowValues(Key key) {
        for (RowValues rowValues : values) {
            if (rowValues.getKey().equals(key)) {
                return rowValues;
            }
        }
        return null;
    }

    public List<RowValues> getValues() {
        return values;
    }
}
