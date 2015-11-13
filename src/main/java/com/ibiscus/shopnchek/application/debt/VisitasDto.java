package com.ibiscus.shopnchek.application.debt;

import java.util.List;

public class VisitasDto {

  private long nroOrden;
  private List<VisitaDto> items;

  public long getNroOrden() {
    return nroOrden;
  }

  public void setNroOrden(final long theNroOrden) {
    nroOrden = theNroOrden;
  }

  public List<VisitaDto> getItems() {
    return items;
  }

  public void setItems(final List<VisitaDto> theItems) {
    items = theItems;
  }

}
