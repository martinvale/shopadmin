package com.ibiscus.shopnchek.application.orden;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.ibiscus.shopnchek.domain.admin.ItemOrden;

public class ItemsOrdenService {

  private final Logger logger = Logger.getLogger(ItemsOrdenService.class.getName());

  private final DataSource dataSource;

  public ItemsOrdenService(final DataSource theDataSource) {
    dataSource = theDataSource;
  }

  public List<ItemOrden> getItemsOrdenDisponiblesMCD(final String dniShopper) {
    List<ItemOrden> items = new LinkedList<ItemOrden>();

    CallableStatement cstmt = null;
    ResultSet rs = null;
    try {
      cstmt = dataSource.getConnection().prepareCall(
          "{call dbo.SProc_Items_MCD_Disponibles(?)}");

      cstmt.setString(1, dniShopper);
      rs = cstmt.executeQuery();
      while (rs.next()) {
        ItemOrden product = new ItemOrden(dniShopper,
            rs.getInt("ASIGNACION"), rs.getInt("TIPO_ITEM"),
            rs.getInt("ID_Tipo_Pago"), rs.getString("SUBCUESTIONARIO"),
            rs.getString("LOCAL"), rs.getInt("MES"), rs.getInt("AÑO"),
            rs.getDate("FECHA"), rs.getDouble("IMPORTE"), 1);
        items.add(product);
      }
    } catch (Exception ex) {
      logger.log(Level.SEVERE, null, ex);
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException ex) {
          logger.log(Level.WARNING, null, ex);
        }
      }
      if (cstmt != null) {
        try {
          cstmt.close();
        } catch (SQLException ex) {
          logger.log(Level.WARNING, null, ex);
        }
      }
    }
    return items;
  }
}
