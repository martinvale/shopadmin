package com.ibiscus.shopnchek.domain.util;

import java.util.HashMap;
import java.util.Map;

public class Row {

    private Map<String, Object> values = new HashMap<String, Object>();

    public Row() {
    }

    public void addValue(final String fieldName, final Object value) {
        values.put(fieldName, value);
    }

    public Object getValue(final String fieldName) {
        return values.get(fieldName);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Row row = (Row) o;

        if (values.containsKey("year") && !values.get("year").equals(row.getValue("year"))) {
            return false;
        }

        if (values.containsKey("month") && !values.get("month").equals(row.getValue("month"))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = values.containsKey("year") ? values.get("year").hashCode() : 0;
        result = 31 * result + (values.containsKey("month") ? values.get("month").hashCode() : 0);
        return result;
    }
}
