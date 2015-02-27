package com.ibiscus.shopnchek.application.report;

import java.sql.CallableStatement;
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

  /*private void initializeDataAnt(final int mesDesde, final int anioDesde,
      final int mesHasta, final int anioHasta,
      final boolean includeGac, final boolean includeMCD,
      final boolean includeAdicionales, final boolean includeShopmetrics) {
    //limpiamos todo antes de calcular los nuevos reportes
    CallableStatement cstmt = null;
    try {
      cstmt = dataSource.getConnection().prepareCall(
          "{call dbo.SProc_Limpiar_Tablas_Auxiliares_Deuda_General}");
      cstmt.execute();
      cstmt.close();
      if (includeMCD) {
        cstmt = dataSource.getConnection().prepareCall(
            "{call dbo.SProc_INSERTAR_EN_AUXILIAR_ITEMS_MCD(?,?)}");

        cstmt.setInt(1, (mesDesde + (anioDesde * 12)));
        cstmt.setInt(2, (mesHasta + (anioHasta * 12)));
        cstmt.execute();
        cstmt.close();
      }
      if (includeGac) {
        cstmt = dataSource.getConnection().prepareCall(
            "{call dbo.SProc_INSERTAR_EN_AUXILIAR_ITEMS_GAC(?,?)}");

        cstmt.setInt(1, (mesDesde + (anioDesde * 12)));
        cstmt.setInt(2, (mesHasta + (anioHasta * 12)));
        cstmt.execute();
        cstmt.close();

        cstmt = dataSource.getConnection().prepareCall(
            "{call dbo.SPROC_INSERTAR_EN_AUXILIAR_ITEMS_GAC_2(?,?)}");

        cstmt.setInt(1, (mesDesde + (anioDesde * 12)));
        cstmt.setInt(2, (mesHasta + (anioHasta * 12)));
        cstmt.execute();
        cstmt.close();
      }
    } catch (Exception ex) {
      logger.log(Level.SEVERE, null, ex);
    } finally {
      if (cstmt != null) {
        try {
          cstmt.close();
        } catch (SQLException ex) {
          logger.log(Level.WARNING, null, ex);
        }
      }
    }
  }*/

  private void initializeData(final int mesDesde, final int anioDesde,
      final int mesHasta, final int anioHasta,
      final boolean includeGac, final boolean includeMCD,
      final boolean includeAdicionales, final boolean includeShopmetrics) {

    //limpiamos todo antes de calcular los nuevos reportes
    PreparedStatement statement = null;
    CallableStatement cstmt = null;
    try {
      statement = dataSource.getConnection().prepareStatement("delete from Auxiliar_Visitas");
      statement.execute();

      if (includeMCD) {
        cstmt = dataSource.getConnection().prepareCall(
            "{call dbo.SProc_INSERTAR_EN_AUXILIAR_VISITAS_MCD(?,?)}");

        cstmt.setInt(1, (mesDesde + (anioDesde * 12)));
        cstmt.setInt(2, (mesHasta + (anioHasta * 12)));
        cstmt.execute();
        cstmt.close();
      }
      if (includeGac) {
        cstmt = dataSource.getConnection().prepareCall(
            "{call dbo.SProc_INSERTAR_EN_AUXILIAR_VISITAS_GAC(?,?)}");

        cstmt.setInt(1, (mesDesde + (anioDesde * 12)));
        cstmt.setInt(2, (mesHasta + (anioHasta * 12)));
        cstmt.execute();
        cstmt.close();

        cstmt = dataSource.getConnection().prepareCall(
            "{call dbo.SProc_INSERTAR_EN_AUXILIAR_VISITAS_GAC_2(?,?)}");

        cstmt.setInt(1, (mesDesde + (anioDesde * 12)));
        cstmt.setInt(2, (mesHasta + (anioHasta * 12)));
        cstmt.execute();
        cstmt.close();
      }
      if (includeAdicionales) {
        cstmt = dataSource.getConnection().prepareCall(
            "{call dbo.SProc_INSERTAR_EN_AUXILIAR_VISITAS_ADICIONALES(?,?)}");

        cstmt.setInt(1, (mesDesde + (anioDesde * 12)));
        cstmt.setInt(2, (mesHasta + (anioHasta * 12)));
        cstmt.execute();
        cstmt.close();
      }
      if (includeShopmetrics) {
        cstmt = dataSource.getConnection().prepareCall(
            "{call dbo.SProc_INSERTAR_EN_AUXILIAR_VISITAS_SHOPMETRICS(?,?)}");

        cstmt.setInt(1, (mesDesde + (anioDesde * 12)));
        cstmt.setInt(2, (mesHasta + (anioHasta * 12)));
        cstmt.execute();
        cstmt.close();
      }

      cstmt = dataSource.getConnection().prepareCall(
          "{call dbo.SProc_Modificar_Honorarios_Auxiliar_Visitas}");
      cstmt.execute();
      cstmt.close();

      cstmt = dataSource.getConnection().prepareCall(
          "{call dbo.SProc_Modificar_Reintegros_Auxiliar_Visitas}");
      cstmt.execute();
      cstmt.close();

      cstmt = dataSource.getConnection().prepareCall(
          "{call dbo.SProc_Modificar_Otros_Gastos_Auxiliar_Visitas}");
      cstmt.execute();
      cstmt.close();
    } catch (Exception ex) {
      logger.log(Level.SEVERE, null, ex);
    } finally {
      if (statement != null) {
        try {
          statement.close();
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
  }

  public List<Row> getDebtSummaryReport(final int mesDesde, final int anioDesde,
      final int mesHasta, final int anioHasta,
      final boolean includeGac, final boolean includeMCD,
      final boolean includeAdicionales, final boolean includeShopmetrics) {
    List<Row> rows = new LinkedList<Row>();

    initializeData(mesDesde, anioDesde, mesHasta, anioHasta, includeGac,
        includeMCD, includeAdicionales, includeShopmetrics);

    //Calculamos las deudas
    /*CallableStatement cstmt = null;
    try {
      if (includeMCD) {
        cstmt = dataSource.getConnection().prepareCall(
            "{call dbo.SProc_CALCULAR_DEUDA_GENERAL_MCD}");
        cstmt.execute();
        cstmt.close();
      }
      if (includeGac) {
        cstmt = dataSource.getConnection().prepareCall(
            "{call dbo.SProc_CALCULAR_DEUDA_GENERAL_GAC}");
        cstmt.execute();
        cstmt.close();
      }
      if (includeAdicionales) {
        cstmt = dataSource.getConnection().prepareCall(
            "{call dbo.SProc_CALCULAR_DEUDA_GENERAL_ADICIONALES(?,?)}");
        cstmt.setInt(1, (mesDesde + (anioDesde * 12)));
        cstmt.setInt(2, (mesHasta + (anioHasta * 12)));
        cstmt.execute();
        cstmt.close();
      }
    } catch (Exception ex) {
      logger.log(Level.SEVERE, null, ex);
    } finally {
      if (cstmt != null) {
        try {
          cstmt.close();
        } catch (SQLException ex) {
          logger.log(Level.WARNING, null, ex);
        }
      }
    }*/

    PreparedStatement statement = null;
    ResultSet resulset = null;
    try {
      statement = dataSource.getConnection().prepareStatement("select año as year, mes as month, "
          //+ "DATEPART(dd, fecha) as day, empresa, tipo_item, "
          + "SUM(CASE WHEN PAGO_HONORARIOS is null THEN IMPORTE_HONORARIOS ELSE 0 END) AS honorarios, "
          + "SUM(CASE WHEN PAGO_REINTEGROS is null THEN IMPORTE_REINTEGROS ELSE 0 END) AS reintegros, "
          + "SUM(CASE WHEN PAGO_OTROS_GASTOS is null THEN IMPORTE_OTROS_GASTOS ELSE 0 END) AS otros "
          + "from AUXILIAR_VISITAS "
          + "group by año, mes "
          + "having (SUM(CASE WHEN PAGO_HONORARIOS is null THEN IMPORTE_HONORARIOS ELSE 0 END) <> 0) or "
          + "(SUM(CASE WHEN PAGO_REINTEGROS is null THEN IMPORTE_REINTEGROS ELSE 0 END) <> 0) or "
          + "(SUM(CASE WHEN PAGO_OTROS_GASTOS is null THEN IMPORTE_OTROS_GASTOS ELSE 0 END) <> 0) "
          + "order by año, mes");

      resulset = statement.executeQuery();
      while (resulset.next()) {
        Row row = new Row();
        row.addValue("year", resulset.getInt("year"));
        row.addValue("month", resulset.getInt("month"));
        /*row.addValue("empresa", resulset.getString("empresa"));
        row.addValue("day", resulset.getInt("day"));
        row.addValue("tipo_item", resulset.getInt("tipo_item"));*/
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

  public List<Row> getProdSummaryReport(final int mesDesde, final int anioDesde,
      final int mesHasta, final int anioHasta,
      final boolean includeGac, final boolean includeMCD,
      final boolean includeAdicionales, final boolean includeShopmetrics) {
    List<Row> rows = new LinkedList<Row>();

    initializeData(mesDesde, anioDesde, mesHasta, anioHasta, includeGac,
        includeMCD, includeAdicionales, includeShopmetrics);

    //Calculamos las gastos de produccion
    /*CallableStatement cstmt = null;
    try {
      if (includeMCD) {
        cstmt = dataSource.getConnection().prepareCall(
            "{call dbo.SProc_CALCULAR_PRODUCCION_GENERAL_MCD}");
        cstmt.execute();
        cstmt.close();
      }
      if (includeGac) {
        cstmt = dataSource.getConnection().prepareCall(
            "{call dbo.SProc_CALCULAR_PRODUCCION_GENERAL_GAC}");
        cstmt.execute();
        cstmt.close();
      }
      if (includeAdicionales) {
        cstmt = dataSource.getConnection().prepareCall(
            "{call dbo.SProc_CALCULAR_PRODUCCION_GENERAL_ADICIONALES(?,?)}");
        cstmt.setInt(1, (mesDesde + (anioDesde * 12)));
        cstmt.setInt(2, (mesHasta + (anioHasta * 12)));
        cstmt.execute();
        cstmt.close();
      }
    } catch (Exception ex) {
      logger.log(Level.SEVERE, null, ex);
    } finally {
      if (cstmt != null) {
        try {
          cstmt.close();
        } catch (SQLException ex) {
          logger.log(Level.WARNING, null, ex);
        }
      }
    }*/

    PreparedStatement statement = null;
    ResultSet resulset = null;
    try {
      statement = dataSource.getConnection().prepareStatement("select año as year, mes as month, "
          + "SUM(CASE WHEN isnull(PAGO_HONORARIOS, 0) = 0 THEN IMPORTE_HONORARIOS ELSE PAGO_HONORARIOS END) AS honorarios, "
          + "SUM(CASE WHEN isnull(PAGO_REINTEGROS, 0) = 0 THEN IMPORTE_REINTEGROS ELSE PAGO_REINTEGROS END) AS reintegros, "
          + "SUM(CASE WHEN isnull(PAGO_OTROS_GASTOS, 0) = 0 THEN IMPORTE_OTROS_GASTOS ELSE PAGO_OTROS_GASTOS END) AS otros "
          + "from AUXILIAR_VISITAS "
          + "group by año, mes "
          + "having (SUM(CASE WHEN isnull(PAGO_HONORARIOS, 0) = 0 THEN IMPORTE_HONORARIOS ELSE PAGO_HONORARIOS END) <> 0) or "
          + "(SUM(CASE WHEN isnull(PAGO_REINTEGROS, 0) = 0 THEN IMPORTE_REINTEGROS ELSE PAGO_REINTEGROS END) <> 0) or "
          + "(SUM(CASE WHEN isnull(PAGO_OTROS_GASTOS, 0) = 0 THEN IMPORTE_OTROS_GASTOS ELSE PAGO_OTROS_GASTOS END) <> 0) "
          + "order by año, mes");
      /*statement = dataSource.getConnection().prepareStatement("select año as year, mes as month, "
          + "DATEPART(dd, fecha) as day, empresa, tipo_item, "
          + "SUM(CASE WHEN PAGO_HONORARIOS = 0 THEN IMPORTE_HONORARIOS ELSE PAGO_HONORARIOS END) AS honorarios, "
          + "SUM(CASE WHEN PAGO_REINTEGROS = 0 THEN IMPORTE_REINTEGROS ELSE PAGO_REINTEGROS END) AS reintegros, "
          + "SUM(CASE WHEN PAGO_OTROS_GASTOS = 0 THEN IMPORTE_OTROS_GASTOS ELSE PAGO_OTROS_GASTOS END) AS otros "
          + "from AUXILIAR_VISITAS "
          + "group by año, mes, DATEPART(dd, fecha), empresa, tipo_item "
          + "having (SUM(CASE WHEN PAGO_HONORARIOS = 0 THEN IMPORTE_HONORARIOS ELSE PAGO_HONORARIOS END) <> 0) or "
          + "(SUM(CASE WHEN PAGO_REINTEGROS = 0 THEN IMPORTE_REINTEGROS ELSE PAGO_REINTEGROS END) <> 0) or "
          + "(SUM(CASE WHEN PAGO_OTROS_GASTOS = 0 THEN IMPORTE_OTROS_GASTOS ELSE PAGO_OTROS_GASTOS END) <> 0) "
          + "order by año, mes, DATEPART(dd, fecha), empresa");*/

      resulset = statement.executeQuery();
      while (resulset.next()) {
        Row row = new Row();
        row.addValue("year", resulset.getInt("year"));
        row.addValue("month", resulset.getInt("month"));

        /*row.addValue("empresa", resulset.getString("empresa"));
        row.addValue("day", resulset.getInt("day"));
        row.addValue("tipo_item", resulset.getInt("tipo_item"));*/

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

  public List<Row> getPaySummaryReport(final Date desde, final Date hasta) {
    List<Row> rows = new LinkedList<Row>();

    //initializeData(mesDesde, anioDesde, mesHasta, anioHasta, includeGac,
        //includeMCD, includeAdicionales, includeShopmetrics);

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
      /*statement = dataSource.getConnection().prepareStatement("select año as year, mes as month, "
          + "SUM(PAGO_HONORARIOS) AS honorarios, "
          + "SUM(PAGO_REINTEGROS) AS reintegros, "
          + "SUM(PAGO_OTROS_GASTOS) AS otros "
          + "from AUXILIAR_VISITAS "
          + "group by año, mes "
          + "having (SUM(PAGO_HONORARIOS) <> 0) or "
          + "(SUM(PAGO_REINTEGROS) <> 0) or "
          + "(SUM(PAGO_OTROS_GASTOS) <> 0) "
          + "order by año, mes");*/
      /*statement = dataSource.getConnection().prepareStatement("select año as year, mes as month, "
          + "DATEPART(dd, fecha) as day, empresa, tipo_item, "
          + "SUM(PAGO_HONORARIOS) AS honorarios, "
          + "SUM(PAGO_REINTEGROS) AS reintegros, "
          + "SUM(PAGO_OTROS_GASTOS) AS otros "
          + "from AUXILIAR_VISITAS "
          + "group by año, mes, DATEPART(dd, fecha), empresa, tipo_item "
          + "having (SUM(PAGO_HONORARIOS) <> 0) or "
          + "(SUM(PAGO_REINTEGROS) <> 0) or "
          + "(SUM(PAGO_OTROS_GASTOS) <> 0) "
          + "order by año, mes, DATEPART(dd, fecha), empresa");*/

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
