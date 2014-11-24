package com.ibiscus.shopnchek.application.shopmetrics;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ImportService {

  private static final int ColSurveyID = 0;
  private static final int ColCliente = 1;
  private static final int ColApellido = 2;
  private static final int ColNombre = 3;
  private static final int Collogin = 4;
  private static final int ColFecha = 5;
  private static final int ColSubcuestionario = 6;
  private static final int ColIDSucursal = 7;
  private static final int ColNombreSucursal = 8;
  private static final int ColDireccionSucursal = 9;
  private static final int ColCiudadSucursal = 10;
  private static final int ColHonorarios = 11;
  private static final int ColReintegros = 12;
  private static final int Colmoneda = 14;
  private static final int ColOK_Pay = 15;

  private final Logger logger = Logger.getLogger(ImportService.class.getName());

  private final DataSource dataSource;

  public ImportService(final DataSource theDataSource) {
    dataSource = theDataSource;
  }

  public void process(final InputStream inputStream) {
    PreparedStatement stmt = null;
    try {
      //Actualizar ID shoppers
      stmt = dataSource.getConnection().prepareStatement(
          "DELETE FROM ImportacionShopmetricsAuxiliar");
      stmt.execute();
    } catch (Exception ex) {
      logger.log(Level.SEVERE, null, ex);
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException ex) {
          logger.log(Level.WARNING, null, ex);
        }
      }
    }

    Workbook workbook;
    Sheet sheet;
    try {
      workbook = WorkbookFactory.create(inputStream);
      sheet = workbook.getSheetAt(2);
    } catch (IOException e) {
      throw new RuntimeException("Cannot read the XLS file", e);
    } catch (InvalidFormatException e) {
      throw new RuntimeException("Cannot open the XLS file", e);
    }

    CreationHelper creationHelper = workbook.getCreationHelper();
    CellStyle theCellStyle = workbook.createCellStyle();
    Iterator<Row> rowIterator = sheet.rowIterator();

    Row row = (Row) rowIterator.next();
    Map<Integer, Integer> headers = getCellHeaders(row);

    theCellStyle.setDataFormat(creationHelper.createDataFormat()
        .getFormat("yyyy-mm-dd"));

    while (rowIterator.hasNext()) {
      row = (Row) rowIterator.next();
      addRow(headers, row);
    }

    try {
      //Actualizar ID shoppers
      stmt = dataSource.getConnection().prepareStatement(
          "UPDATE importacionshopmetricsauxiliar SET tax_id = S.NRO_Documento, tax_id_type = s.TipoDocumento "
              + "FROM ImportacionShopmetricsauxiliar I LEFT JOIN mcdonalds.dbo.shoppers s ON I.login = S.login_shopmetrics "
              + "collate Modern_Spanish_CI_AS WHERE isnull(I.tax_id,'''')='''' ");

      stmt.execute();

      //Insertar nuevos items
      stmt = dataSource.getConnection().prepareStatement(
          "INSERT ImportacionShopmetrics SELECT * FROM ImportacionShopmetricsAuxiliar A "
              + "where A.Tax_id IS NOT NULL and A.InstanceId not in (select InstanceId from ImportacionShopmetrics)");

      stmt.execute();

      //Actualizo los honorarios
      stmt = dataSource.getConnection().prepareStatement(
          "update ImportacionShopmetrics set Honorarios=A.Honorarios "
              + "from ImportacionShopmetrics S inner join ImportacionShopmetricsAuxiliar A on "
              + "S.InstanceID = A.InstanceID Left join Items_Orden I on ((S.InstanceId = "
              + "I.Asignacion)and(I.Tipo_Item = 5)and(I.Tipo_Pago =1)) where (a.honorarios <> S.Honorarios) "
              + "and ((I.asignacion is null) or ((A.Honorarios = I.Importe)and(I.asignacion is not null)))");
      stmt.execute();

      //Actualizo los reintegros
      stmt = dataSource.getConnection().prepareStatement(
          "update ImportacionShopmetrics set Reintegros=A.Reintegros "
              + "from ImportacionShopmetrics S inner join ImportacionShopmetricsAuxiliar A on "
              + "S.InstanceID = A.InstanceID Left join Items_Orden I on ((S.InstanceId = "
              + "I.Asignacion)and(I.Tipo_Item = 5)and(I.Tipo_Pago =2)) where (a.Reintegros <> S.Reintegros) "
              + "and ((I.asignacion is null) or ((A.Reintegros = I.Importe)and(I.asignacion is not null)))");
      stmt.execute();

      //Actualizo los otros gastos
      stmt = dataSource.getConnection().prepareStatement(
          "update ImportacionShopmetrics set OtrosGastos=A.OtrosGastos "
              + "from ImportacionShopmetrics S inner join ImportacionShopmetricsAuxiliar A on "
              + "S.InstanceID = A.InstanceID Left join Items_Orden I on ((S.InstanceId = "
              + "I.Asignacion)and(I.Tipo_Item = 5)and(I.Tipo_Pago =3)) where (a.OtrosGastos <> S.OtrosGastos) "
              + "and ((I.asignacion is null) or ((A.OtrosGastos = I.Importe)and(I.asignacion is not null)))");
      stmt.execute();

      //Actualizo shopper asignado
      stmt = dataSource.getConnection().prepareStatement(
          "update ImportacionShopmetrics set TAX_ID = A.TAX_ID, "
              + "[LOGIN] = A.[LOGIN], APELLIDO = A.APELLIDO, NOMBRE = A.NOMBRE "
              + "from ImportacionShopmetrics S inner join ImportacionShopmetricsAuxiliar A on "
              + "S.InstanceID = A.InstanceID Left join Items_Orden I on ((S.InstanceId = "
              + "I.Asignacion)and(I.Tipo_Item = 5)) where (I.asignacion is null)and "
              + "((isnull(A.tax_id,'''') <> isnull(S.tax_id,''''))or(isnull(A.login,'''') <> isnull(S.login,'''')))");
      stmt.execute();

      //Actualizo fechas de visitas
      stmt = dataSource.getConnection().prepareStatement(
          "update ImportacionShopmetrics set Date_Time = A.Date_Time "
              + "from ImportacionShopmetrics S inner join ImportacionShopmetricsAuxiliar A on "
              + "S.InstanceID = A.InstanceID where (a.Date_Time <> S.Date_Time)");
      stmt.execute();

      //Actualizo OK pay
      stmt = dataSource.getConnection().prepareStatement(
          "update ImportacionShopmetrics set OK_Pay=A.OK_Pay "
              + "from ImportacionShopmetrics S inner join ImportacionShopmetricsAuxiliar A on "
              + "S.InstanceID = A.InstanceID where (a.OK_Pay <> S.OK_Pay)");
      stmt.execute();
    } catch (Exception ex) {
      logger.log(Level.SEVERE, null, ex);
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException ex) {
          logger.log(Level.WARNING, null, ex);
        }
      }
    }
  }

  private void addRow(final Map<Integer, Integer> headers, final Row row) {
    Cell cell = row.getCell(headers.get(ColSurveyID));
    Double surveyIdValue = null;
    try {
      surveyIdValue = cell.getNumericCellValue();
    } catch (IllegalStateException e) {
      surveyIdValue = null;
    }
    if (surveyIdValue != null) {
      Long surveyId = surveyIdValue.longValue();

      cell = row.getCell(headers.get(Collogin));
      String login = cell.getStringCellValue();
      if (login != null && !login.isEmpty()) {
        String cliente = row.getCell(headers.get(ColCliente))
            .getStringCellValue();
        String apellido = row.getCell(headers.get(ColApellido))
            .getStringCellValue();
        String nombre = row.getCell(headers.get(ColNombre))
            .getStringCellValue();
        String fechaValue = row.getCell(headers.get(ColFecha))
            .getStringCellValue();
        Date fecha = null;
        if (fechaValue != null) {
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
          try {
            fecha = dateFormat.parse(fechaValue);
          } catch (ParseException e) {
            throw new RuntimeException("Cannot parse the cell date", e);
          }
        }
        String subCuestionario = row.getCell(headers.get(ColSubcuestionario))
            .getStringCellValue();
        String idSucursal = row.getCell(headers.get(ColIDSucursal))
            .getStringCellValue();
        String nombreSucursal = row.getCell(headers.get(ColNombreSucursal))
            .getStringCellValue();
        String direccionSucursal = row.getCell(headers.get(ColDireccionSucursal))
            .getStringCellValue();
        String ciudadSucursal = row.getCell(headers.get(ColCiudadSucursal))
            .getStringCellValue();
        String honorariosValue = row.getCell(headers.get(ColHonorarios))
            .getStringCellValue();
        Double honorarios = null;
        if (honorariosValue != null && !honorariosValue.isEmpty()) {
          honorarios = new Double(honorariosValue);
        }
        String reintegrosValue = row.getCell(headers.get(ColReintegros))
            .getStringCellValue();
        Double reintegros = null;
        if (reintegrosValue != null && !reintegrosValue.isEmpty()) {
          reintegros = new Double(reintegrosValue);
        }
        String okPayValue = row.getCell(headers.get(ColOK_Pay))
            .getStringCellValue();

        if (cliente.length() > 50) {
          cliente = cliente.substring(0, 50);
        }
        if (apellido.length() > 60) {
          apellido = apellido.substring(0, 60);
        }
        if (nombre.length() > 100) {
          nombre = nombre.substring(0, 100);
        }
        if (login.length() > 50) {
          login = login.substring(0, 50);
        }
        if (subCuestionario.length() > 50) {
          subCuestionario = subCuestionario.substring(0, 50);
        }
        if (idSucursal.length() > 20) {
          idSucursal = idSucursal.substring(0, 20);
        }
        if (nombreSucursal.length() > 50) {
          nombreSucursal = nombreSucursal.substring(0, 50);
        }
        if (direccionSucursal.length() > 100) {
          direccionSucursal = direccionSucursal.substring(0, 100);
        }
        if (ciudadSucursal.length() > 50) {
          ciudadSucursal = ciudadSucursal.substring(0, 50);
        }

        PreparedStatement stmt = null;
        try {
          stmt = dataSource.getConnection().prepareStatement(
              "INSERT INTO ImportacionShopmetricsAuxiliar(Cliente, Apellido, Nombre, Login, Tax_ID, "
              + "Tax_ID_Type, Date_Time, Survey, InstanceID, Location, Location_Name, Loc_Address, "
              + "Loc_City, Honorarios, Reintegros, OK_Pay) Values(?, ?, ?, "
              + "?, NULL, NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

          stmt.setString(1, cliente);
          stmt.setString(2, apellido);
          stmt.setString(3, nombre);
          stmt.setString(4, login);
          stmt.setDate(5, new java.sql.Date(fecha.getTime()));
          stmt.setString(6, subCuestionario);
          stmt.setLong(7, surveyId);
          stmt.setString(8, idSucursal);
          stmt.setString(9, nombreSucursal);
          stmt.setString(10, direccionSucursal);
          stmt.setString(11, ciudadSucursal);
          stmt.setDouble(12, honorarios);
          stmt.setDouble(13, reintegros);
          stmt.setBoolean(14, okPayValue.equalsIgnoreCase("OK"));

          stmt.execute();
        } catch (Exception ex) {
          logger.log(Level.SEVERE, null, ex);
        } finally {
          if (stmt != null) {
            try {
              stmt.close();
            } catch (SQLException ex) {
              logger.log(Level.WARNING, null, ex);
            }
          }
        }
      }
    }
  }

  private Map<Integer, Integer> getCellHeaders(final Row row) {
    Map<Integer, Integer> headers = new HashMap<Integer, Integer>();

    Iterator<Cell> cellIterator = row.cellIterator();

    int currentPos = 0;
    while (cellIterator.hasNext()) {
      Cell cell = (Cell) cellIterator.next();
      if (cell.getStringCellValue().equalsIgnoreCase("Survey ID")) {
        headers.put(ColSurveyID, currentPos);
      }
      if (cell.getStringCellValue().equalsIgnoreCase("Client")) {
        headers.put(ColCliente, currentPos);
      }
      if (cell.getStringCellValue().equalsIgnoreCase("Login")) {
        headers.put(Collogin, currentPos);
      }
      if (cell.getStringCellValue().equalsIgnoreCase("First Name")) {
        headers.put(ColNombre, currentPos);
      }
      if (cell.getStringCellValue().equalsIgnoreCase("Last Name")) {
        headers.put(ColApellido, currentPos);
      }
      if (cell.getStringCellValue().equalsIgnoreCase("PayRate")) {
        headers.put(ColHonorarios, currentPos);
      }
      if (cell.getStringCellValue().equalsIgnoreCase("Purchase Reimbursement")) {
        headers.put(ColReintegros, currentPos);
      }
      if (cell.getStringCellValue().equalsIgnoreCase("Currency")) {
        headers.put(Colmoneda, currentPos);
      }
      if (cell.getStringCellValue().equalsIgnoreCase("Ok Pay")) {
        headers.put(ColOK_Pay, currentPos);
      }
      if (cell.getStringCellValue().equalsIgnoreCase("Survey Date")) {
        headers.put(ColFecha, currentPos);
      }
      if (cell.getStringCellValue().equalsIgnoreCase("Survey")) {
        headers.put(ColSubcuestionario, currentPos);
      }
      if (cell.getStringCellValue().equalsIgnoreCase("Store ID")) {
        headers.put(ColIDSucursal, currentPos);
      }
      if (cell.getStringCellValue().equalsIgnoreCase("Location Name")) {
        headers.put(ColNombreSucursal, currentPos);
      }
      if (cell.getStringCellValue().equalsIgnoreCase("Address")) {
        headers.put(ColDireccionSucursal, currentPos);
      }
      if (cell.getStringCellValue().equalsIgnoreCase("City")) {
        headers.put(ColCiudadSucursal, currentPos);
      }

      currentPos++;
    }
    return headers;
  }
}
