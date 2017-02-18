package com.ibiscus.shopnchek.application.shopmetrics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibiscus.shopnchek.domain.admin.ItemOrden;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.Proveedor;
import com.ibiscus.shopnchek.domain.admin.ProveedorRepository;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;
import com.ibiscus.shopnchek.domain.debt.BranchRepository;
import com.ibiscus.shopnchek.domain.debt.ClientRepository;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;
import com.ibiscus.shopnchek.domain.tasks.BatchTaskStatus;
import com.ibiscus.shopnchek.domain.tasks.BatchTaskStatusRepository;

public class ImportService {

  private final Logger logger = LoggerFactory.getLogger(ImportService.class);

  private final OrderRepository orderRepository;

  private final DebtRepository debtRepository;

  private final ClientRepository clientRepository;

  private final BranchRepository branchRepository;

  private final ShopperRepository shopperRepository;

  private final ProveedorRepository proveedorRepository;

  private final BatchTaskStatusRepository batchTaskStatusRepository;

  private ExecutorService executor = Executors.newFixedThreadPool(10);

  public ImportService(final OrderRepository orderRepository,
		  final DebtRepository debtRepository, final ClientRepository clientRepository,
		  final BranchRepository branchRepository, final ShopperRepository shopperRepository,
		  final ProveedorRepository proveedorRepository, final BatchTaskStatusRepository batchTaskStatusRepository) {
    this.orderRepository = orderRepository;
    this.debtRepository = debtRepository;
    this.clientRepository = clientRepository;
    this.branchRepository = branchRepository;
    this.shopperRepository = shopperRepository;
    this.proveedorRepository = proveedorRepository;
    this.batchTaskStatusRepository = batchTaskStatusRepository;
  }

  /** Creates an import process to be executed asynchronously.
   *
   * @param inputStream The input stream to parse, cannot be null.
   *
   * @return The name identifier of the new process, never returns null.
   */
  public String process(String fileName, final InputStream inputStream) {
	String processName = null;
	File tempFile = null;
    FileOutputStream fileOutputStream = null;
	try {
		tempFile = File.createTempFile(fileName, null);
		fileOutputStream = new FileOutputStream(tempFile);
		IOUtils.copy(inputStream, fileOutputStream);
	} catch (IOException e) {
		logger.error("Cannot copy " + fileName + " to temp file", e);
	} finally {
		IOUtils.closeQuietly(fileOutputStream);
		IOUtils.closeQuietly(inputStream);
	}

	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
	processName = fileName + dateFormat.format(new Date());
	BatchTaskStatus batchTaskStatus = new BatchTaskStatus(processName);
	batchTaskStatusRepository.save(batchTaskStatus);
	Runnable task = new FileImportTask(processName, tempFile, batchTaskStatusRepository,
			debtRepository, clientRepository, branchRepository, shopperRepository);
	executor.submit(task);
	return processName;
  }

  public void exportOrdenes(final OutputStream outputStream,
      final Date desde, final Date hasta) {
    Map<Class<?>, CellStyle> styles = new HashMap<Class<?>, CellStyle>();

    Workbook workbook = new SXSSFWorkbook();
    Sheet sheet = workbook.createSheet("Ordenes");

    List<OrdenPago> orders = orderRepository.find(null, null, "numero", true, null, null, null, null,
        null, desde, hasta);
    try {
      int currentRow = 2;

      Row row = sheet.createRow(0);
      createCell(workbook, styles, row, 0, "Fecha de impresion");
      DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
      createCell(workbook, styles, row, 1, dateFormat.format(new Date()));

      row = sheet.createRow(1);
      createCell(workbook, styles, row, 0, "NUMERO");
      createCell(workbook, styles, row, 1, "TITULAR");
      createCell(workbook, styles, row, 2, "CHEQUERA");
      createCell(workbook, styles, row, 3, "CHEQUE");
      createCell(workbook, styles, row, 4, "ID TRANSF.");
      createCell(workbook, styles, row, 5, "ESTADO");
      createCell(workbook, styles, row, 6, "FACTURA");
      createCell(workbook, styles, row, 7, "FAC. NRO.");
      createCell(workbook, styles, row, 8, "IVA HONORARIOS");
      createCell(workbook, styles, row, 9, "CUIT");
      createCell(workbook, styles, row, 10, "FECHA PAGO");
      createCell(workbook, styles, row, 11, "REINTEGROS");
      createCell(workbook, styles, row, 12, "OTROS GASTOS");
      createCell(workbook, styles, row, 13, "HONORARIOS");
      createCell(workbook, styles, row, 14, "IMPORTE IVA");
      createCell(workbook, styles, row, 15, "TOTAL");

      for (OrdenPago order : orders) {
        row = sheet.createRow(currentRow++);
        createCell(workbook, styles, row, 0, order.getNumero());
        String titular = null;
        String cuit = null;
        if (order.getTipoProveedor().equals(OrdenPago.SHOPPER)) {
          Shopper shopper = shopperRepository.get(order.getProveedor());
          titular = shopper.getName();
          //cuit = shopper.getCuit();
        } else {
          Proveedor proveedor = proveedorRepository.get(order.getProveedor());
          titular = proveedor.getDescription();
          cuit = proveedor.getCuit();
        }
        createCell(workbook, styles, row, 1, titular);
        createCell(workbook, styles, row, 2, order.getNumeroChequera());
        createCell(workbook, styles, row, 3, order.getNumeroCheque());
        createCell(workbook, styles, row, 4, order.getIdTransferencia());
        createCell(workbook, styles, row, 5, order.getEstado().getDescription());
        createCell(workbook, styles, row, 6, order.getTipoFactura());
        createCell(workbook, styles, row, 7, order.getNumeroFactura());
        createCell(workbook, styles, row, 8, order.getIva());
        createCell(workbook, styles, row, 9, cuit);
        createCell(workbook, styles, row, 10, order.getFechaPago());
        double honorarios = 0;
        double reintegros = 0;
        double otrosGastos = 0;
        for (ItemOrden item : order.getItems()) {
          if (item.getTipoPago().getDescription().equals(com.ibiscus.shopnchek.domain.admin.TipoPago.HONORARIOS)) {
            honorarios += item.getImporte();
          } else if (item.getTipoPago().getDescription().equals(com.ibiscus.shopnchek.domain.admin.TipoPago.REINTEGROS)) {
            reintegros += item.getImporte();
          } else {
            otrosGastos += item.getImporte();
          }
        }
        createCell(workbook, styles, row, 11, reintegros);
        createCell(workbook, styles, row, 12, otrosGastos);
        createCell(workbook, styles, row, 13, honorarios);
        double ivaHonorarios = (order.getIva() * honorarios) / 100;
        createCell(workbook, styles, row, 14, ivaHonorarios);
        double total = honorarios + reintegros + otrosGastos + ivaHonorarios;
        createCell(workbook, styles, row, 15, total);
      }

      workbook.write(outputStream);
    } catch (IOException e) {
      logger.error("Cannot export the orders", e);
    }
  }

  public void exportDeuda(final OutputStream outputStream,
      final String dniShopper,
      final boolean includeMcd,
      final boolean includeGac, final boolean includeAdicionales,
      final boolean includeShopmetrics, final Date desde, final Date hasta) {
    Map<Class<?>, CellStyle> styles = new HashMap<Class<?>, CellStyle>();

    Workbook workbook = new SXSSFWorkbook();
    Sheet sheet = workbook.createSheet("Items Adeudados");

    /*CallableStatement cstmt = null;
    ResultSet rs = null;
    try {
      int currentRow = 1;
      cstmt = dataSource.getConnection().prepareCall(
          "{call dbo.SProc_Items_Deuda_Shopper(?,?,?,?,?,?,?,?)}");

      cstmt.setString(1, dniShopper);
      cstmt.setBoolean(2, false);
      cstmt.setBoolean(3, includeMcd);
      cstmt.setBoolean(4, includeGac);
      cstmt.setBoolean(5, includeAdicionales);
      cstmt.setBoolean(6, includeShopmetrics);
      cstmt.setDate(7, new java.sql.Date(desde.getTime()));
      cstmt.setDate(8, new java.sql.Date(hasta.getTime()));
      rs = cstmt.executeQuery();

      Row row = sheet.createRow(0);
      createCell(workbook, styles, row, 0, "EMPRESA");
      createCell(workbook, styles, row, 1, "SUBCUESTIONARIO");
      createCell(workbook, styles, row, 2, "LOCAL");
      createCell(workbook, styles, row, 3, "MES");
      createCell(workbook, styles, row, 4, "AÑO");
      createCell(workbook, styles, row, 5, "IMPORTE");
      createCell(workbook, styles, row, 6, "FECHA");
      createCell(workbook, styles, row, 7, "PAGO");

      while (rs.next()) {
        row = sheet.createRow(currentRow++);
        Object value = rs.getString("EMPRESA");
        createCell(workbook, styles, row, 0, value);
        value = rs.getString("SUBCUESTIONARIO");
        createCell(workbook, styles, row, 1, value);
        value = rs.getString("LOCAL");
        createCell(workbook, styles, row, 2, value);
        value = rs.getInt("MES");
        createCell(workbook, styles, row, 3, value);
        value = rs.getInt("AÑO");
        createCell(workbook, styles, row, 4, value);
        value = rs.getFloat("IMPORTE");
        createCell(workbook, styles, row, 5, value);
        value = rs.getTimestamp("FECHA");
        createCell(workbook, styles, row, 6, value);
        value = rs.getString("PAGO");
        createCell(workbook, styles, row, 7, value);
      }

      workbook.write(outputStream);
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
    }*/
  }

  public void reportDeuda(final OutputStream outputStream,
		  final Date desde, final Date hasta) {
	    Map<Class<?>, CellStyle> styles = new HashMap<Class<?>, CellStyle>();

	    Workbook workbook = new SXSSFWorkbook();
	    Sheet sheet = workbook.createSheet("Items Adeudados");

	    /*CallableStatement cstmt = null;
	    ResultSet rs = null;
	    try {
	      int currentRow = 1;
	      cstmt = dataSource.getConnection().prepareCall(
	          "{call prodGeneral2(?,?)}");

	      cstmt.setDate(1, new java.sql.Date(desde.getTime()));
	      cstmt.setDate(2, new java.sql.Date(hasta.getTime()));
	      rs = cstmt.executeQuery();

	      Row row = sheet.createRow(0);
	      createCell(workbook, styles, row, 0, "SHOPPER");
	      createCell(workbook, styles, row, 1, "EMPRESA");
	      createCell(workbook, styles, row, 2, "LOCAL");
	      createCell(workbook, styles, row, 3, "FECHA");
	      createCell(workbook, styles, row, 4, "PAGO");
	      createCell(workbook, styles, row, 5, "IMPORTE");

	      while (rs.next()) {
	        row = sheet.createRow(currentRow++);
	        Object value = rs.getString("SHOPPER");
	        createCell(workbook, styles, row, 0, value);
	        value = rs.getString("EMPRESA");
	        createCell(workbook, styles, row, 1, value);
	        value = rs.getString("LOCAL");
	        createCell(workbook, styles, row, 2, value);
	        value = rs.getTimestamp("FECHA");
	        createCell(workbook, styles, row, 3, value);
	        value = rs.getString("PAGO");
	        createCell(workbook, styles, row, 4, value);
	        value = rs.getFloat("IMPORTE");
	        createCell(workbook, styles, row, 5, value);
	      }

	      workbook.write(outputStream);
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
	    }*/
	  }

  public void reportAdicionales(final OutputStream outputStream) {
	    Map<Class<?>, CellStyle> styles = new HashMap<Class<?>, CellStyle>();

	    Workbook workbook = new SXSSFWorkbook();
	    Sheet sheet = workbook.createSheet("Items Adeudados");

	    /*PreparedStatement stmt = null;
	    ResultSet rs = null;
	    try {
	      int currentRow = 1;
	      stmt = dataSource.getConnection().prepareStatement(
		          "select mcdonalds.dbo.shoppers.apellido_y_nombre as shopper, "
		    		  + "items_adicionales_autorizados.opnro as numero, items_adicionales_autorizados.cliente_nombre as cliente, items_adicionales_autorizados.sucursal_nombre as sucursal, "
		        	  + "items_adicionales_autorizados.fecha_visita as fecha, '-' as pago, items_adicionales_autorizados.importe, items_adicionales_autorizados.observaciones "
		    		  + "from items_adicionales_autorizados "
		    		  //+ "inner join ordenes on (ordenes.numero = items_adicionales_autorizados.opnro) "
		        	  + "left join mcdonalds.dbo.shoppers on (mcdonalds.dbo.shoppers.nro_documento collate Modern_Spanish_CI_AS = items_adicionales_autorizados.shopper_dni) "
		        	  + "where items_adicionales_autorizados.fecha_visita >= ? and items_adicionales_autorizados.shopper_dni <> '' "
		        	  + "and items_adicionales_autorizados.opnro is not null and items_adicionales_autorizados.opnro > 0 "
	    	  		  + "order by items_adicionales_autorizados.fecha_visita asc");

	      Calendar calendar = Calendar.getInstance();
	      calendar.set(Calendar.YEAR, 2014);
	      calendar.set(Calendar.MONTH, Calendar.DECEMBER);
	      calendar.set(Calendar.DAY_OF_MONTH, 1);
	      
	      stmt.setDate(1, new java.sql.Date(calendar.getTimeInMillis()));
	      rs = stmt.executeQuery();

	      Row row = sheet.createRow(0);
	      createCell(workbook, styles, row, 0, "SHOPPER");
	      createCell(workbook, styles, row, 1, "NUMERO ORDEN");
	      createCell(workbook, styles, row, 2, "EMPRESA");
	      createCell(workbook, styles, row, 3, "LOCAL");
	      createCell(workbook, styles, row, 4, "FECHA");
	      createCell(workbook, styles, row, 5, "PAGO");
	      createCell(workbook, styles, row, 6, "IMPORTE");
	      createCell(workbook, styles, row, 7, "OBSERVACIONES");

	      while (rs.next()) {
	        row = sheet.createRow(currentRow++);
	        Object value = rs.getString("shopper");
	        createCell(workbook, styles, row, 0, value);
	        value = rs.getString("numero");
	        createCell(workbook, styles, row, 1, value);
	        value = rs.getString("cliente");
	        createCell(workbook, styles, row, 2, value);
	        value = rs.getString("sucursal");
	        createCell(workbook, styles, row, 3, value);
	        value = rs.getTimestamp("fecha");
	        createCell(workbook, styles, row, 4, value);
	        value = rs.getString("pago");
	        createCell(workbook, styles, row, 5, value);
	        value = rs.getFloat("importe");
	        createCell(workbook, styles, row, 6, value);
	        value = rs.getString("observaciones");
	        createCell(workbook, styles, row, 7, value);
	      }

	      workbook.write(outputStream);
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
	    }*/
	  }

  /** Creates a cell and writes into it the value received. It applies the style
  * needed, in order to write the data with the correct format.
  *
  * @param theRow the row where the data will be written. It cannot be null.
  * @param theColumnIndex the column of the row where the data will be written.
  * @param theValue the value to be written. It can be null.
  * @return the cell containing the value in the correct format.
  */
  private Cell createCell(final Workbook workbook,
      final Map<Class<?>, CellStyle> styles, final Row theRow,
      final int theColumnIndex, final Object theValue) {

    Cell cell = theRow.createCell(theColumnIndex);

    if (theValue != null) {
      if (theValue.getClass() == Integer.class
          || theValue.getClass() == Long.class) {
        cell.setCellValue(((Number) theValue).intValue());
      } else if (theValue.getClass() == Float.class
          || theValue.getClass() == Double.class) {
        cell.setCellValue(((Number) theValue).doubleValue());
      } else if (theValue.getClass() == Timestamp.class
          || theValue.getClass() == java.sql.Date.class) {
        cell.setCellValue((Timestamp) theValue);
      } else if(theValue.getClass() == Boolean.class) {
        cell.setCellValue(((Boolean) theValue).booleanValue());
      } else {
        String cellValue = theValue.toString();

        cell.setCellValue(cellValue);
      }
      CellStyle cellStyle = getCellStyle(workbook, styles, theValue.getClass());
      if (cellStyle != null) {
        cell.setCellStyle(cellStyle);
      }
    }
    return cell;
  }

  /** Search for the cell style appropriate for this cell according the data
   * type, returns null if there is no cell style for this data type.
   *
   * @param theDataType the data type whose cell style we want to retrieve,
   *  cannot be null.
   *
   * @return the cell style or null.
   */
  private CellStyle getCellStyle(final Workbook workbook,
      final Map<Class<?>, CellStyle> styles, final Class<?> theDataType) {
    Validate.notNull(theDataType, "the data type cannot be null");

    CellStyle theCellStyle = styles.get(theDataType);

    if (theCellStyle == null) {
      CreationHelper creationHelper = workbook.getCreationHelper();
      // If, in mysql query, the DATE() function is called, then theDataType is
      // a java.sql.Date object. If the TIME() function is called, then
      // theDataType is a java.sql.Time object.
      if (theDataType == Integer.class
          || theDataType == Long.class) {
        theCellStyle = workbook.createCellStyle();
        theCellStyle.setDataFormat(creationHelper.createDataFormat().
            getFormat("#,##0"));
      } else if (theDataType == Float.class
          || theDataType == Double.class) {
        theCellStyle = workbook.createCellStyle();
        theCellStyle.setDataFormat(creationHelper.createDataFormat().
            getFormat("$ #,##0.00"));
      } else if (theDataType == java.sql.Date.class
          || theDataType == java.sql.Timestamp.class) {
        theCellStyle = workbook.createCellStyle();
        theCellStyle.setDataFormat(creationHelper.createDataFormat().
            getFormat("dd/mm/yyyy"));
      } else if (theDataType == java.sql.Time.class) {
        theCellStyle = workbook.createCellStyle();
        theCellStyle.setDataFormat(creationHelper.createDataFormat().
            getFormat("h:mm:ss AM/PM"));
      }
      if (theCellStyle != null) {
        styles.put(theDataType, theCellStyle);
      }
    }

    return theCellStyle;
  }
}
