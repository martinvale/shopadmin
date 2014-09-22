package com.ibiscus.shopnchek.domain.util;

import java.util.List;

public class ResultSet<T> {

  private final List<T> data;

  private final int size;

  public ResultSet(final List<T> theData, final int theSize) {
    data = theData;
    size = theSize;
  }

  public List<T> getData() {
    return data;
  }

  public int getSize() {
    return size;
  }
}
