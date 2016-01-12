package com.ibiscus.shopnchek.application.shopmetrics;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.Validate;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.ibiscus.shopnchek.domain.admin.ItemOrden;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.Proveedor;
import com.ibiscus.shopnchek.domain.admin.ProveedorRepository;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;
import com.ibiscus.shopnchek.domain.debt.Branch;
import com.ibiscus.shopnchek.domain.debt.BranchRepository;
import com.ibiscus.shopnchek.domain.debt.Client;
import com.ibiscus.shopnchek.domain.debt.ClientRepository;
import com.ibiscus.shopnchek.domain.debt.Debt;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;
import com.ibiscus.shopnchek.domain.debt.TipoItem;
import com.ibiscus.shopnchek.domain.debt.TipoPago;

public class ImportService {

  private static final String OK_FOR_BILLING = "OK";

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

  private final OrderRepository orderRepository;

  private final DebtRepository debtRepository;

  private final ClientRepository clientRepository;

  private final BranchRepository branchRepository;

  private final ShopperRepository shopperRepository;

  private final ProveedorRepository proveedorRepository;

  public ImportService(final DataSource dataSource, final OrderRepository orderRepository,
		  final DebtRepository debtRepository, final ClientRepository clientRepository,
		  final BranchRepository branchRepository, final ShopperRepository shopperRepository,
		  final ProveedorRepository proveedorRepository) {
    this.dataSource = dataSource;
    this.orderRepository = orderRepository;
    this.debtRepository = debtRepository;
    this.clientRepository = clientRepository;
    this.branchRepository = branchRepository;
    this.shopperRepository = shopperRepository;
    this.proveedorRepository = proveedorRepository;
  }

  public List<ShopmetricsUserDto> process(final InputStream inputStream) {
    List<ShopmetricsUserDto> users = new LinkedList<ShopmetricsUserDto>();

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

    boolean salir = false;
    while (rowIterator.hasNext() && !salir) {
      row = (Row) rowIterator.next();
      try {
    	  salir = addRow(headers, row);
      } catch (ShopperNotFoundException e) {
    	  logger.log(Level.WARNING, "Cannot import Shopmetrics item for shopper with login "
    			  + e.getIdentifier());
    	  users.add(new ShopmetricsUserDto(e.getIdentifier(), null, null));
      }
    }

    /*ResultSet rs = null;
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
              + "I.Asignacion)and(I.Tipo_Item = 5)and(I.Tipo_Pago =1)) where ((a.honorarios <> S.Honorarios) or (S.Honorarios is null)) "
              + "and ((I.asignacion is null) or ((A.Honorarios = I.Importe)and(I.asignacion is not null)))");
      stmt.execute();

      //Actualizo los reintegros
      stmt = dataSource.getConnection().prepareStatement(
          "update ImportacionShopmetrics set Reintegros=A.Reintegros "
              + "from ImportacionShopmetrics S inner join ImportacionShopmetricsAuxiliar A on "
              + "S.InstanceID = A.InstanceID Left join Items_Orden I on ((S.InstanceId = "
              + "I.Asignacion)and(I.Tipo_Item = 5)and(I.Tipo_Pago =2)) where ((S.reintegros <> A.reintegros) or (S.reintegros is null)) "
              + "and ((I.asignacion is null) or ((A.Reintegros = I.Importe)and(I.asignacion is not null)))");
      stmt.execute();

      //Actualizo los otros gastos
      stmt = dataSource.getConnection().prepareStatement(
          "update ImportacionShopmetrics set OtrosGastos=A.OtrosGastos "
              + "from ImportacionShopmetrics S inner join ImportacionShopmetricsAuxiliar A on "
              + "S.InstanceID = A.InstanceID Left join Items_Orden I on ((S.InstanceId = "
              + "I.Asignacion)and(I.Tipo_Item = 5)and(I.Tipo_Pago =3)) where ((a.OtrosGastos <> S.OtrosGastos) or (S.OtrosGastos is null)) "
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

      stmt = dataSource.getConnection().prepareStatement("select aux.login, "
          + "aux.apellido, aux.nombre from ImportacionShopmetricsauxiliar aux "
          + "LEFT JOIN mcdonalds.dbo.shoppers s ON aux.login = s.login_shopmetrics "
          + "collate Modern_Spanish_CI_AS where s.id is null "
          + "group by aux.login, aux.apellido, aux.nombre;");

      rs = stmt.executeQuery();
      while (rs.next()) {
        ShopmetricsUserDto user = new ShopmetricsUserDto(rs.getString("login"),
            rs.getString("apellido"), rs.getString("nombre"));
        users.add(user);
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
    }*/

    return users;
  }

  private boolean addRow(final Map<Integer, Integer> headers, final Row row) {
    boolean end = false;
    Cell cell = row.getCell(headers.get(ColSurveyID));
    Double surveyIdValue = null;
    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
      surveyIdValue = cell.getNumericCellValue();
    } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
      String cellTotal = cell.getStringCellValue();
      end = "Total".equals(cellTotal);
    }

    cell = row.getCell(headers.get(ColOK_Pay));
    boolean isOk = false;
    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
    	isOk = OK_FOR_BILLING.equals(cell.getStringCellValue());
    }
    if (surveyIdValue != null && isOk) {
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
          honorariosValue = honorariosValue.replaceAll(",", "");
          honorarios = new Double(honorariosValue);
        }
        Double reintegros = null;
        Integer reintegroPos = headers.get(ColReintegros);
        if (reintegroPos != null) {
          String reintegrosValue = row.getCell(reintegroPos)
              .getStringCellValue();
          if (reintegrosValue != null && !reintegrosValue.isEmpty()) {
            reintegrosValue = reintegrosValue.replaceAll(",", "");
            reintegros = new Double(reintegrosValue);
          }
        }

        Branch branch = null;
        Client client = clientRepository.getByName(cliente);
        if (client == null) {
        	client = new Client(cliente);
        	clientRepository.save(client);
        } else {
        	final String sucursalCode = idSucursal;
        	branch = (Branch) CollectionUtils.find(client.getBranchs(), new Predicate() {

				@Override
				public boolean evaluate(Object item) {
					boolean result = false;
					Branch branch = (Branch) item;
					if (branch.getCode() != null) {
						result = branch.getCode().equals(sucursalCode);
					}
					return result;
				}

        	});
        }
    	if (branch == null) {
    		branch = new Branch(client, idSucursal, ciudadSucursal, direccionSucursal);
    		branchRepository.save(branch);
    		//client.addBranch(branch);
    		//clientRepository.update(client);
    	}

    	Shopper shopper = shopperRepository.findByLogin(login);
    	if (shopper == null) {
    		throw new ShopperNotFoundException(login);
    	}
    	if (honorarios != null) {
	        Debt debt = debtRepository.getByExternalId(surveyIdValue.longValue(), TipoItem.shopmetrics,
	        		TipoPago.honorarios);
	        if (debt == null) {
	        	debt = new Debt(TipoItem.shopmetrics, TipoPago.honorarios, shopper.getDni(),
	        			honorarios, fecha, null, subCuestionario, client, null, branch, null,
	        			surveyIdValue.longValue(), null);
	        	debtRepository.save(debt);
	        } else {
	        	debt.update(TipoItem.shopmetrics, TipoPago.honorarios, shopper.getDni(),
	        			honorarios, fecha, null, subCuestionario, client, null, branch, null,
	        			surveyIdValue.longValue(), null);
	        	debtRepository.update(debt);
	        }
    	}
    	if (reintegros != null) {
	        Debt debt = debtRepository.getByExternalId(surveyIdValue.longValue(), TipoItem.shopmetrics,
	        		TipoPago.reintegros);
	        if (debt == null) {
	        	debt = new Debt(TipoItem.shopmetrics, TipoPago.reintegros, shopper.getDni(),
	        			reintegros, fecha, null, subCuestionario, client, null, branch, null,
	        			surveyIdValue.longValue(), null);
	        	debtRepository.save(debt);
	        } else {
	        	debt.update(TipoItem.shopmetrics, TipoPago.reintegros, shopper.getDni(),
	        			reintegros, fecha, null, subCuestionario, client, null, branch, null,
	        			surveyIdValue.longValue(), null);
	        	debtRepository.update(debt);
	        }
    	}
      }
    }
    return end;
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
      logger.log(Level.SEVERE, "Cannot export the orders", e);
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
