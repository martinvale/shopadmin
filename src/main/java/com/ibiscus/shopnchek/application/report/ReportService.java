package com.ibiscus.shopnchek.application.report;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.ibiscus.shopnchek.domain.util.Row;

public class ReportService {

  private final Logger logger = Logger.getLogger(ReportService.class.getName());

  private final DataSource dataSource;

  public ReportService(final DataSource theDataSource) {
    dataSource = theDataSource;
  }

  public List<Row> getSummaryReport(final Date desde, final Date hasta) {
    List<Row> rows = new LinkedList<Row>();

    PreparedStatement statement = null;
    ResultSet resulset = null;
    try {
      statement = dataSource.getConnection().prepareStatement("select year(ordenes.fecha_pago) as year, month(ordenes.fecha_pago) as month, "
          + "sum(case when items_orden.tipo_pago = 1 then items_orden.importe else 0 end) as honorarios, "
          + "sum(case when items_orden.tipo_pago = 2 then items_orden.importe else 0 end) as reintegros, "
          + "sum(case when items_orden.tipo_pago = 3 then items_orden.importe else 0 end) as otros "
          + "from ordenes "
          + "inner join items_orden on (ordenes.numero = items_orden.orden_nro) "
          + "where ordenes.estado = 4 and ordenes.fecha_pago >= ? and ordenes.fecha_pago <= ? "
          + "group by year(ordenes.fecha_pago), month(ordenes.fecha_pago) "
          + "order by year(ordenes.fecha_pago), month(ordenes.fecha_pago)");
      statement.setDate(1, new java.sql.Date(desde.getTime()));
      statement.setDate(2, new java.sql.Date(hasta.getTime()));

      resulset = statement.executeQuery();
      while (resulset.next()) {
        Row row = new Row();
        row.addValue("year", resulset.getInt("year"));
        row.addValue("month", resulset.getInt("month"));
        row.addValue("honorarios", resulset.getDouble("honorarios"));
        row.addValue("reintegros", resulset.getDouble("reintegros"));
        row.addValue("otros", resulset.getDouble("otros"));
        rows.add(row);
      }
    } catch (Exception ex) {
      logger.log(Level.SEVERE, null, ex);
    } finally {
      if (resulset != null) {
        try {
          resulset.close();
        } catch (SQLException ex) {
          logger.log(Level.WARNING, null, ex);
        }
      }
      if (statement != null) {
        try {
          statement.close();
        } catch (SQLException ex) {
          logger.log(Level.WARNING, null, ex);
        }
      }
    }

    return rows;
  }
}
