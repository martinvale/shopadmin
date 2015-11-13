package com.ibiscus.shopnchek.application.order;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.ibiscus.shopnchek.domain.admin.Adicional;
import com.ibiscus.shopnchek.domain.admin.Visita;

public class ItemsOrdenService {

  private final Logger logger = Logger.getLogger(ItemsOrdenService.class.getName());

  private final DataSource dataSource;

  public ItemsOrdenService(final DataSource theDataSource) {
    dataSource = theDataSource;
  }

  public synchronized Long getItemOrdenId() {
    Long newId = null;

    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dataSource.getConnection().createStatement();

      rs = stmt.executeQuery("SELECT item_nro FROM parametros");
      while (rs.next()) {
        newId = rs.getLong("item_nro");
        newId++;
      }
      stmt.execute("UPDATE parametros SET item_nro = item_nro + 1");
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
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException ex) {
          logger.log(Level.WARNING, null, ex);
        }
      }
    }

    return newId;
  }

  public void linkAdicional(final int adicional, final long ordenNro) {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dataSource.getConnection().createStatement();
      stmt.execute("UPDATE items_adicionales_autorizados SET opnro = " + ordenNro
          + " WHERE id = " + adicional);
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
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException ex) {
          logger.log(Level.WARNING, null, ex);
        }
      }
    }
  }

  public List<Visita> getVisitasDisponiblesMCD(final String dniShopper) {
    List<Visita> items = new LinkedList<Visita>();

    CallableStatement cstmt = null;
    ResultSet rs = null;
    try {
      cstmt = dataSource.getConnection().prepareCall(
          "{call dbo.SProc_Items_MCD_Disponibles(?)}");

      cstmt.setString(1, dniShopper);
      rs = cstmt.executeQuery();
      while (rs.next()) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        //String importe = String.format("%.2f", rs.getFloat("IMPORTE"));
        Visita visita = new Visita(rs.getInt("ASIGNACION"), dniShopper, null,
            rs.getString("SUBCUESTIONARIO"),
            rs.getString("LOCAL"), rs.getString("Apellido_Y_Nombre"),
            rs.getInt("MES"), rs.getInt("AÑO"), format.format(rs.getDate("FECHA")),
            rs.getString("PAGO"), format.format(rs.getDate("Fecha_Cobro")),
            rs.getFloat("IMPORTE"), rs.getInt("TIPO_ITEM"),
            rs.getInt("ID_Tipo_Pago"));
        items.add(visita);
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

  public List<Adicional> getAdicionalesDisponibles(final String dniShopper) {
    List<Adicional> items = new LinkedList<Adicional>();

    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dataSource.getConnection().createStatement();

      rs = stmt.executeQuery("SELECT ITEMS_ADICIONALES_AUTORIZADOS.ID, TIPOS_PAGO.DESCRIPCION, "
          + "ITEMS_ADICIONALES_AUTORIZADOS.CLIENTE_NOMBRE, "
          + "ITEMS_ADICIONALES_AUTORIZADOS.SUCURSAL_NOMBRE, "
          + "ITEMS_ADICIONALES_AUTORIZADOS.MES_TRABAJO, "
          + "ITEMS_ADICIONALES_AUTORIZADOS.ANIO_TRABAJO, "
          + "ITEMS_ADICIONALES_AUTORIZADOS.IMPORTE, "
          + "ITEMS_ADICIONALES_AUTORIZADOS.FECHA_VISITA, "
          + "ITEMS_ADICIONALES_AUTORIZADOS.FECHA_COBRO, "
          + "ITEMS_ADICIONALES_AUTORIZADOS.OBSERVACIONES, "
          + "ITEMS_ADICIONALES_AUTORIZADOS.TIPO_PAGO, "
          + "USUARIOS.NOMBRE AS USUARIO_AUTORIZACION "
          + "FROM USUARIOS INNER JOIN "
          + "(ITEMS_ADICIONALES_AUTORIZADOS INNER JOIN TIPOS_PAGO ON ITEMS_ADICIONALES_AUTORIZADOS.TIPO_PAGO = TIPOS_PAGO.ID) "
          + "ON USUARIOS.DESCRIPCION = ITEMS_ADICIONALES_AUTORIZADOS.USUARIO_AUTORIZACION "
          + "WHERE (ITEMS_ADICIONALES_AUTORIZADOS.SHOPPER_DNI = '" + dniShopper + "') AND "
          + "((ITEMS_ADICIONALES_AUTORIZADOS.OPNRO=0)OR(ITEMS_ADICIONALES_AUTORIZADOS.OPNRO IS NULL)) "
          + "ORDER BY CLIENTE_NOMBRE, FECHA_VISITA;");

      while (rs.next()) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Adicional visita = new Adicional(dniShopper, rs.getInt("ID"),
            rs.getString("DESCRIPCION"), rs.getString("CLIENTE_NOMBRE"),
            rs.getString("SUCURSAL_NOMBRE"),
            rs.getInt("MES_TRABAJO"), rs.getInt("ANIO_TRABAJO"), format.format(rs.getDate("FECHA_VISITA")),
            rs.getString("OBSERVACIONES"), rs.getDouble("IMPORTE"), rs.getInt("TIPO_PAGO"));
        items.add(visita);
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
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException ex) {
          logger.log(Level.WARNING, null, ex);
        }
      }
    }
    return items;
  }

  public List<Visita> getDeudaShopper(final String dniShopper,
      final boolean includeIplan, final boolean includeMcd,
      final boolean includeGac, final boolean includeAdicionales,
      final boolean includeShopmetrics, final Date desde, final Date hasta) {
    List<Visita> items = new LinkedList<Visita>();

    CallableStatement cstmt = null;
    ResultSet rs = null;
    try {
      cstmt = dataSource.getConnection().prepareCall(
          "{call dbo.SProc_Items_Deuda_Shopper(?,?,?,?,?,?,?,?)}");

      cstmt.setString(1, dniShopper);
      cstmt.setBoolean(2, includeIplan);
      cstmt.setBoolean(3, includeMcd);
      cstmt.setBoolean(4, includeGac);
      cstmt.setBoolean(5, includeAdicionales);
      cstmt.setBoolean(6, includeShopmetrics);
      cstmt.setDate(7, new java.sql.Date(desde.getTime()));
      cstmt.setDate(8, new java.sql.Date(hasta.getTime()));
      rs = cstmt.executeQuery();
      while (rs.next()) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        //String importe = String.format("%.2f", rs.getFloat("IMPORTE"));
        Visita visita = new Visita(rs.getInt("ASIGNACION"), dniShopper,
            rs.getString("EMPRESA"),
            rs.getString("SUBCUESTIONARIO"),
            rs.getString("LOCAL"), rs.getString("Shopper"),
            rs.getInt("MES"), rs.getInt("AÑO"), format.format(rs.getDate("FECHA")),
            rs.getString("PAGO"), format.format(rs.getDate("Fecha_Cobro")),
            rs.getFloat("IMPORTE"), rs.getInt("TIPO_ITEM"),
            rs.getInt("ID_Tipo_Pago"));

        if (visita.getAsignacion() != null) {
          Statement stmt = null;
          ResultSet rsObservation = null;
          try {
            stmt = dataSource.getConnection().createStatement();

            rsObservation = stmt.executeQuery("SELECT USUARIO, OBSERVACION"
                + " FROM OBSERVACIONES_VISITAS"
                + " WHERE TIPO_ITEM = " + visita.getTipoItem()
                + " AND TIPO_PAGO = " + visita.getTipoPago()
                + " AND ASSIGN_ID = " + visita.getAsignacion());

            while (rsObservation.next()) {
              visita.updateObservacion(rsObservation.getString("USUARIO"), rsObservation.getString("OBSERVACION"));
            }
          } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
          } finally {
            if (rsObservation != null) {
              try {
                rsObservation.close();
              } catch (SQLException ex) {
                logger.log(Level.WARNING, null, ex);
              }
            }
            if (stmt != null) {
              try {
                stmt.close();
              } catch (SQLException ex) {
                logger.log(Level.WARNING, null, ex);
              }
            }
          }
        }

        items.add(visita);
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

  public String getObservacionAdicional(final long id) {
    String observacion = null;

    Statement stmt = null;
    ResultSet rsObservation = null;
    try {
      stmt = dataSource.getConnection().createStatement();

      rsObservation = stmt.executeQuery("SELECT OBSERVACIONES"
          + " FROM ITEMS_ADICIONALES_AUTORIZADOS"
          + " WHERE ID = " + id);

      while (rsObservation.next()) {
        observacion = rsObservation.getString("OBSERVACIONES");
      }
    } catch (Exception ex) {
      logger.log(Level.SEVERE, null, ex);
    } finally {
      if (rsObservation != null) {
        try {
          rsObservation.close();
        } catch (SQLException ex) {
          logger.log(Level.WARNING, null, ex);
        }
      }
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException ex) {
          logger.log(Level.WARNING, null, ex);
        }
      }
    }
    return observacion;
  }
}
