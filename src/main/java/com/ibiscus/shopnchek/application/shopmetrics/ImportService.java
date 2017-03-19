package com.ibiscus.shopnchek.application.shopmetrics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
import com.ibiscus.shopnchek.domain.tasks.BatchTaskStatus;
import com.ibiscus.shopnchek.domain.tasks.BatchTaskStatusRepository;

public class ImportService {

  private final Logger logger = LoggerFactory.getLogger(ImportService.class);

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

	private static final String OK_FOR_BILLING = "OK";

  private OrderRepository orderRepository;

  private DebtRepository debtRepository;

  private ClientRepository clientRepository;

  private BranchRepository branchRepository;

  private ShopperRepository shopperRepository;

  private ProveedorRepository proveedorRepository;

  private BatchTaskStatusRepository batchTaskStatusRepository;

//  private ExecutorService executor = Executors.newFixedThreadPool(10);

  public ImportService() {
	  
  }

	public void setOrderRepository(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}
	public void setDebtRepository(DebtRepository debtRepository) {
		this.debtRepository = debtRepository;
	}
	public void setClientRepository(ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}
	public void setBranchRepository(BranchRepository branchRepository) {
		this.branchRepository = branchRepository;
	}
	public void setShopperRepository(ShopperRepository shopperRepository) {
		this.shopperRepository = shopperRepository;
	}
	public void setProveedorRepository(ProveedorRepository proveedorRepository) {
		this.proveedorRepository = proveedorRepository;
	}
	public void setBatchTaskStatusRepository(
			BatchTaskStatusRepository batchTaskStatusRepository) {
		this.batchTaskStatusRepository = batchTaskStatusRepository;
	}

/** Creates an import process to be executed asynchronously.
   *
   * @param inputStream The input stream to parse, cannot be null.
   *
   * @return The name identifier of the new process, never returns null.
   */
  public String process(String fileName, final InputStream inputStream, ServletContext servletContext) {
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

	importFile(processName, tempFile);
	//ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
	//ApplicationContext context = new FileSystemXmlApplicationContext("WEB-INF/applicationContext.xml");
	//ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
	/*FileImportTask task = (FileImportTask) context.getBean("fileImportTask");
	((AbstractApplicationContext) context).close();

	task.setName(processName);
	task.setFile(tempFile);*/
	/*Runnable task = new FileImportTask(processName, tempFile, batchTaskStatusRepository,
	debtRepository, clientRepository, branchRepository, shopperRepository);*/
	//executor.submit(task);
	return processName;
  }

	private void importFile(final String name, final File file) {
		logger.info("Staring to import task: {}", name);

		Workbook workbook;
		Sheet sheet;
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			workbook = WorkbookFactory.create(fileInputStream);
			sheet = workbook.getSheetAt(2);
		} catch (IOException e) {
			updateTaskErrorMessage(name, "Invalid import file: " + name);
			throw new RuntimeException("Cannot read the XLS file", e);
		} catch (InvalidFormatException e) {
			updateTaskErrorMessage(name, "Invalid import file: " + name);
			throw new RuntimeException("Cannot open the XLS file", e);
		} finally {
			IOUtils.closeQuietly(fileInputStream);
		}

		CreationHelper creationHelper = workbook.getCreationHelper();
		CellStyle theCellStyle = workbook.createCellStyle();
		Iterator<Row> rowIterator = sheet.rowIterator();
		int rowCount = sheet.getLastRowNum();
		int currentRowIndex = 0;
		int intervalRowCount = (5 * rowCount) / 100;
		if (intervalRowCount == 0) {
			intervalRowCount = 1;
		}

		Row row = (Row) rowIterator.next();
		Map<Integer, Integer> headers = getCellHeaders(row);

		theCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(
				"yyyy-mm-dd"));

		List<ShopmetricsUserDto> users = new LinkedList<ShopmetricsUserDto>();

		start(name);
		boolean salir = false;
		try {
			while (rowIterator.hasNext() && !salir) {
				row = (Row) rowIterator.next();
				currentRowIndex++;
				try {
					long startTime = System.currentTimeMillis();
					salir = addRow(headers, row);
					logger.debug("Row import total time: {} ms", System.currentTimeMillis() - startTime);
				} catch (ShopperNotFoundException e) {
					logger.warn("Cannot import Shopmetrics item for shopper with login "
							+ e.getIdentifier());
					users.add(new ShopmetricsUserDto(e.getIdentifier(), null, null));
				}
				if ((currentRowIndex % intervalRowCount) == 0) {
					updatePorcentage(name, (currentRowIndex * 100) / rowCount);
				}
			}
		} catch (Exception e) {
			logger.error("Cannot import file " + name, e);
		} finally {
			logger.info("Finish file {} import", name);
		}
		finish(name, users);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	private void updateTaskErrorMessage(final String name, final String errorMessage) {
		BatchTaskStatus batchTaskStatus = batchTaskStatusRepository.getByName(name);
		batchTaskStatus.error(errorMessage);
		batchTaskStatusRepository.update(batchTaskStatus);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	private void updatePorcentage(final String name, final int porcentage) {
		BatchTaskStatus batchTaskStatus = batchTaskStatusRepository.getByName(name);
		batchTaskStatus.setProcentage(porcentage);
		batchTaskStatusRepository.update(batchTaskStatus);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void start(final String name) {
		BatchTaskStatus batchTaskStatus = batchTaskStatusRepository.getByName(name);
		batchTaskStatus.start();
		batchTaskStatusRepository.update(batchTaskStatus);
	}

	private String getImportErrorInfo(final List<ShopmetricsUserDto> users) {
		String additionalInfo = null;
		if (!users.isEmpty()) {
			StringBuilder builder = new StringBuilder("[");
			boolean first = true;
			for (ShopmetricsUserDto userDto : users) {
				if (!first) {
					builder.append(",");
				}
				first = false;
				builder.append("\"");
				builder.append(userDto.getLogin());
				builder.append("\"");
			}
			builder.append("]");
			additionalInfo = builder.toString();
		}
		return additionalInfo;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	private void finish(final String name, final List<ShopmetricsUserDto> users) {
		BatchTaskStatus batchTaskStatus = batchTaskStatusRepository.getByName(name);
		batchTaskStatus.finish();
		batchTaskStatus.setAdditionalInfo(getImportErrorInfo(users));
		batchTaskStatusRepository.update(batchTaskStatus);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean addRow(final Map<Integer, Integer> headers, final Row row) {
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
				Shopper shopper = shopperRepository.findByLogin(login);
				if (shopper == null) {
					throw new ShopperNotFoundException(login);
				}

				String cliente = row.getCell(headers.get(ColCliente))
						.getStringCellValue();
				/*String apellido = row.getCell(headers.get(ColApellido))
						.getStringCellValue();
				String nombre = row.getCell(headers.get(ColNombre))
						.getStringCellValue();*/
				String fechaValue = row.getCell(headers.get(ColFecha))
						.getStringCellValue();
				Date fecha = null;
				if (fechaValue != null) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd");
					try {
						fecha = dateFormat.parse(fechaValue);
					} catch (ParseException e) {
						throw new RuntimeException(
								"Cannot parse the cell date", e);
					}
				}
				String subCuestionario = row.getCell(
						headers.get(ColSubcuestionario)).getStringCellValue();
				String idSucursal = row.getCell(headers.get(ColIDSucursal))
						.getStringCellValue();
				/*String nombreSucursal = row.getCell(
						headers.get(ColNombreSucursal)).getStringCellValue();*/
				String direccionSucursal = row.getCell(
						headers.get(ColDireccionSucursal)).getStringCellValue();
				String ciudadSucursal = row.getCell(
						headers.get(ColCiudadSucursal)).getStringCellValue();
				String honorariosValue = row
						.getCell(headers.get(ColHonorarios))
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
				long startTime = System.currentTimeMillis();
				Client client = clientRepository.getByName(cliente);
				logger.debug("Get client by name in {} ms", System.currentTimeMillis() - startTime);
				if (client == null) {
					client = new Client(cliente);
					startTime = System.currentTimeMillis();
					clientRepository.save(client);
					logger.debug("Save client in {} ms", System.currentTimeMillis() - startTime);
				} else {
					final String sucursalCode = idSucursal;
					startTime = System.currentTimeMillis();
					branch = branchRepository.findByCode(client, idSucursal);
					/*branch = (Branch) CollectionUtils.find(client.getBranchs(),
							new Predicate() {

								@Override
								public boolean evaluate(Object item) {
									boolean result = false;
									Branch branch = (Branch) item;
									if (branch.getCode() != null) {
										result = branch.getCode().equals(
												sucursalCode);
									}
									return result;
								}

							});*/
					logger.debug("Get branch by code {} for client {} in {} ms", sucursalCode,
							client.getId(), System.currentTimeMillis() - startTime);
				}
				if (branch == null) {
					branch = new Branch(client, idSucursal, ciudadSucursal,
							direccionSucursal);
					branchRepository.save(branch);
				}

				startTime = System.currentTimeMillis();
				if (honorarios != null) {
					long startGetTime = System.currentTimeMillis();
					Debt debt = debtRepository.getByExternalId(
							surveyIdValue.longValue(), TipoItem.shopmetrics,
							TipoPago.honorarios);
					logger.debug("Getting honorarios in {} ms", System.currentTimeMillis() - startGetTime);
					if (debt == null) {
						debt = new Debt(TipoItem.shopmetrics,
								TipoPago.honorarios, shopper.getDni(),
								honorarios, fecha, null, subCuestionario,
								client, null, branch, null,
								surveyIdValue.longValue(), null);
						debtRepository.save(debt);
					} else {
						debt.update(TipoItem.shopmetrics, TipoPago.honorarios,
								shopper.getDni(), honorarios, fecha, null,
								subCuestionario, client, null, branch, null,
								surveyIdValue.longValue(), null);
						long startUpdateTime = System.currentTimeMillis();
						debtRepository.update(debt);
						logger.debug("Updating honorarios in {} ms", System.currentTimeMillis() - startUpdateTime);
					}
				}
				logger.debug("Saving honorarios in {} ms", System.currentTimeMillis() - startTime);

				startTime = System.currentTimeMillis();
				if (reintegros != null) {
					long startGetTime = System.currentTimeMillis();
					Debt debt = debtRepository.getByExternalId(
							surveyIdValue.longValue(), TipoItem.shopmetrics,
							TipoPago.reintegros);
					logger.debug("Getting reintegros in {} ms", System.currentTimeMillis() - startGetTime);
					if (debt == null) {
						debt = new Debt(TipoItem.shopmetrics,
								TipoPago.reintegros, shopper.getDni(),
								reintegros, fecha, null, subCuestionario,
								client, null, branch, null,
								surveyIdValue.longValue(), null);
						debtRepository.save(debt);
					} else {
						debt.update(TipoItem.shopmetrics, TipoPago.reintegros,
								shopper.getDni(), reintegros, fecha, null,
								subCuestionario, client, null, branch, null,
								surveyIdValue.longValue(), null);
						long startUpdateTime = System.currentTimeMillis();
						debtRepository.update(debt);
						logger.debug("Updating reintegros in {} ms", System.currentTimeMillis() - startUpdateTime);
					}
				}
				logger.debug("Saving reintegros in {} ms", System.currentTimeMillis() - startTime);
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
			if (cell.getStringCellValue().equalsIgnoreCase(
					"Purchase Reimbursement")) {
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
        String cuit = order.getCuit();
        if (order.getTipoProveedor().equals(OrdenPago.SHOPPER)) {
          Shopper shopper = shopperRepository.get(order.getProveedor());
          titular = shopper.getName();
          //cuit = shopper.getCuit();
        } else {
          Proveedor proveedor = proveedorRepository.get(order.getProveedor());
          titular = proveedor.getDescription();
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
