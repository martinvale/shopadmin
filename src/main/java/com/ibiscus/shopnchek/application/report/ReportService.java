package com.ibiscus.shopnchek.application.report;

import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.google.common.collect.ImmutableList;

import com.ibiscus.shopnchek.domain.debt.Debt.State;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;
import com.ibiscus.shopnchek.domain.report.*;
import com.ibiscus.shopnchek.domain.util.Row;

import static com.ibiscus.shopnchek.domain.debt.Debt.State.asignada;
import static com.ibiscus.shopnchek.domain.debt.Debt.State.pagada;

public class ReportService {

    private final Logger logger = Logger.getLogger(ReportService.class.getName());

    private final DataSource dataSource;
    private final DebtRepository debtRepository;

    public ReportService(final DataSource theDataSource,
                         DebtRepository debtRepository) {
        this.dataSource = theDataSource;
        this.debtRepository = debtRepository;
    }

    public Report getGeneralDebtReport(final Date from, final Date to, boolean includeEmpresa, boolean includeShopper) {
        List<String> orderBy = getOrderConditions(includeEmpresa, includeShopper);
        List<Row> productionRows = debtRepository.getSummary(null, null, from, to, orderBy);
        Report report = getReport(productionRows);

        List<State> states = ImmutableList.of(pagada);
        List<Row> debtRows = debtRepository.getSummary(null, states, from, to, orderBy);
        populateReport(report, debtRows);

        return report;
    }

    public Report getPresentedDebtReport(final Date from, final Date to, boolean includeEmpresa, boolean includeShopper) {
        List<String> orderBy = getOrderConditions(includeEmpresa, includeShopper);
        List<State> states = ImmutableList.of(pagada, asignada);
        List<Row> productionRows = debtRepository.getSummary(null, states, from, to, orderBy);
        Report report = getReport(productionRows);

        states = ImmutableList.of(pagada);
        List<Row> debtRows = debtRepository.getSummary(null, states, from, to, orderBy);
        populateReport(report, debtRows);

        return report;
    }

    public Report getPendingDebtReport(final Date from, final Date to, boolean includeEmpresa, boolean includeShopper) {
        List<String> orderBy = getOrderConditions(includeEmpresa, includeShopper);
        List<Row> productionRows = debtRepository.getSummary(null, null, from, to, orderBy);
        Report report = getReport(productionRows);

        List<State> states = ImmutableList.of(pagada, asignada);
        List<Row> debtRows = debtRepository.getSummary(null, states, from, to, orderBy);
        populateReport(report, debtRows);

        return report;
    }

    private List<String> getOrderConditions(boolean includeEmpresa, boolean includeShopper) {
        List<String> orderBy = new ArrayList<String>();
        if (includeEmpresa) {
            orderBy.add("clients.name");
            orderBy.add("clients.id");
        }
        if (includeShopper) {
            orderBy.add("shoppers.apellido_y_nombre");
            orderBy.add("shoppers.nro_documento");
        }
        return orderBy;
    }

    private void populateReport(Report report, List<Row> debtRows) {
        for (Row row : debtRows) {
            Key key = new Key((Integer) row.getValue("year"),
                    (Integer) row.getValue("month"), (BigInteger) row.getValue("id"),
                    (String) row.getValue("name"),
                    (String) row.getValue("nro_documento"),
                    (String) row.getValue("apellido_y_nombre"));
            SummaryValue sustraendo = new SummaryValue((Double) row.getValue("honorarios"),
                    (Double) row.getValue("reintegros"), (Double) row.getValue("otros"));

            report.updateSustraendo(key, sustraendo);
        }
    }

    private Report getReport(List<Row> productionRows) {
        Report report = new Report();
        for (Row row : productionRows) {
            Key key = new Key((Integer) row.getValue("year"),
                    (Integer) row.getValue("month"), (BigInteger) row.getValue("id"),
                    (String) row.getValue("name"),
                    (String) row.getValue("nro_documento"),
                    (String) row.getValue("apellido_y_nombre"));
            SummaryValue minuendo = new SummaryValue((Double) row.getValue("honorarios"),
                    (Double) row.getValue("reintegros"), (Double) row.getValue("otros"));
            RowValues rowValues = new RowValues(key, minuendo);
            report.addValue(rowValues);
        }
        return report;
    }

    public List<Row> getProdSummaryReport(final Date desde, final Date hasta,
                                          final boolean includeGac, final boolean includeMCD,
                                          final boolean includeAdicionales, final boolean includeShopmetrics,
                                          final boolean includeEmpresa) {
        List<Row> rows = new LinkedList<Row>();

        CallableStatement cstmt = null;
        ResultSet resulset = null;
        try {
            cstmt = dataSource.getConnection().prepareCall(
                    "{call prodGeneral(?,?,?)}");

            cstmt.setDate(1, new java.sql.Date(desde.getTime()));
            cstmt.setDate(2, new java.sql.Date(hasta.getTime()));
            cstmt.setBoolean(3, includeEmpresa);

            resulset = cstmt.executeQuery();
            while (resulset.next()) {
                Row row = new Row();
                row.addValue("year", resulset.getInt("year"));
                row.addValue("month", resulset.getInt("month"));

                if (includeEmpresa) {
                    row.addValue("empresa", resulset.getString("empresa"));
                }

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
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    logger.log(Level.WARNING, null, ex);
                }
            }
        }

        return rows;
    }

    public List<Row> getPaySummaryReport(final Date desde, final Date hasta) {
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
                    + "where (ordenes.estado = 4 or ordenes.estado = 2) and ordenes.fecha_pago >= ? and ordenes.fecha_pago <= ? "
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
