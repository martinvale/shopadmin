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
}
