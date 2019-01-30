package com.ibiscus.shopnchek.application.shopmetrics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

public class FileImportTask implements Runnable {

	private static final Logger logger = LoggerFactory
			.getLogger(FileImportTask.class);

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

	private String name;

	private File file;

	private final BatchTaskStatusRepository batchTaskStatusRepository;

	private final DebtRepository debtRepository;

	private final ClientRepository clientRepository;

	private final BranchRepository branchRepository;

	private final ShopperRepository shopperRepository;

	public FileImportTask(final BatchTaskStatusRepository batchTaskStatusRepository,
			final DebtRepository debtRepository,
			final ClientRepository clientRepository, final BranchRepository branchRepository,
			final ShopperRepository shopperRepository) {
		this.batchTaskStatusRepository = batchTaskStatusRepository;
		this.debtRepository = debtRepository;
		this.clientRepository = clientRepository;
		this.branchRepository = branchRepository;
		this.shopperRepository = shopperRepository;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public void run() {
		logger.info("Staring to import task: {}", name);

		Workbook workbook;
		Sheet sheet;
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			workbook = WorkbookFactory.create(fileInputStream);
			sheet = workbook.getSheetAt(2);
		} catch (IOException e) {
			updateTaskErrorMessage("Invalid import file: " + name);
			throw new RuntimeException("Cannot read the XLS file", e);
		} catch (InvalidFormatException e) {
			updateTaskErrorMessage("Invalid import file: " + name);
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

		start();
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
					updatePorcentage((currentRowIndex * 100) / rowCount);
				}
			}
		} catch (Exception e) {
			logger.error("Cannot import file " + name, e);
		} finally {
			logger.info("Finish file {} import", name);
		}
		finish(users);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	private void updateTaskErrorMessage(final String errorMessage) {
		BatchTaskStatus batchTaskStatus = batchTaskStatusRepository.getByName(name);
		batchTaskStatus.error(errorMessage);
		batchTaskStatusRepository.update(batchTaskStatus);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	private void updatePorcentage(final int porcentage) {
		BatchTaskStatus batchTaskStatus = batchTaskStatusRepository.getByName(name);
		batchTaskStatus.setProcentage(porcentage);
		batchTaskStatusRepository.update(batchTaskStatus);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	private void start() {
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
	private void finish(final List<ShopmetricsUserDto> users) {
		BatchTaskStatus batchTaskStatus = batchTaskStatusRepository.getByName(name);
		batchTaskStatus.finish();
		batchTaskStatus.setAdditionalInfo(getImportErrorInfo(users));
		batchTaskStatusRepository.update(batchTaskStatus);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
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
								TipoPago.honorarios, shopper.getIdentityId(),
								honorarios, fecha, null, subCuestionario,
								client, null, branch, null,
								surveyIdValue.longValue(), null);
						debtRepository.save(debt);
					} else {
						debt.update(TipoItem.shopmetrics, TipoPago.honorarios,
								shopper.getIdentityId(), honorarios, fecha, null,
								subCuestionario, client, null, branch, null,
								null, surveyIdValue.longValue(), null);
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
								TipoPago.reintegros, shopper.getIdentityId(),
								reintegros, fecha, null, subCuestionario,
								client, null, branch, null,
								surveyIdValue.longValue(), null);
						debtRepository.save(debt);
					} else {
						debt.update(TipoItem.shopmetrics, TipoPago.reintegros,
								shopper.getIdentityId(), reintegros, fecha, null,
								subCuestionario, client, null, branch, null,
								null, surveyIdValue.longValue(), null);
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

}
