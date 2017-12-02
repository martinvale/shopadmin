package com.ibiscus.shopnchek.application.shopmetrics;

import java.awt.Color;
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
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.domain.admin.ItemOrden;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.OrderState;
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

    private static enum Position {
        ODD(1), EVEN(0);

        private final int modulus;

        private Position(int modulus) {
            this.modulus = modulus;
        }

        public int getModulus() {
            return modulus;
        }

        static Position byModulus(int modulus) {
            for (Position position : values()) {
                if (position.getModulus() == modulus) {
                    return position;
                }
            }
            throw new IllegalArgumentException("Cannot find a position for modulus " + modulus);
        }
    }

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

    // private ExecutorService executor = Executors.newFixedThreadPool(10);

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

    /**
     * Creates an import process to be executed asynchronously.
     * 
     * @param inputStream
     *            The input stream to parse, cannot be null.
     * 
     * @return The name identifier of the new process, never returns null.
     */
    public String process(String fileName, final InputStream inputStream,
            ServletContext servletContext) {
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
        // ApplicationContext context =
        // WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        // ApplicationContext context = new
        // FileSystemXmlApplicationContext("WEB-INF/applicationContext.xml");
        // ApplicationContext context = new
        // ClassPathXmlApplicationContext("applicationContext.xml");
        /*
         * FileImportTask task = (FileImportTask)
         * context.getBean("fileImportTask"); ((AbstractApplicationContext)
         * context).close();
         * 
         * task.setName(processName); task.setFile(tempFile);
         */
        /*
         * Runnable task = new FileImportTask(processName, tempFile,
         * batchTaskStatusRepository, debtRepository, clientRepository,
         * branchRepository, shopperRepository);
         */
        // executor.submit(task);
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
                    logger.debug("Row import total time: {} ms",
                            System.currentTimeMillis() - startTime);
                } catch (ShopperNotFoundException e) {
                    logger.warn("Cannot import Shopmetrics item for shopper with login "
                            + e.getIdentifier());
                    users.add(new ShopmetricsUserDto(e.getIdentifier(), null,
                            null));
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
    private void updateTaskErrorMessage(final String name,
            final String errorMessage) {
        BatchTaskStatus batchTaskStatus = batchTaskStatusRepository
                .getByName(name);
        batchTaskStatus.error(errorMessage);
        batchTaskStatusRepository.update(batchTaskStatus);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    private void updatePorcentage(final String name, final int porcentage) {
        BatchTaskStatus batchTaskStatus = batchTaskStatusRepository
                .getByName(name);
        batchTaskStatus.setProcentage(porcentage);
        batchTaskStatusRepository.update(batchTaskStatus);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void start(final String name) {
        BatchTaskStatus batchTaskStatus = batchTaskStatusRepository
                .getByName(name);
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
        BatchTaskStatus batchTaskStatus = batchTaskStatusRepository
                .getByName(name);
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
        if (surveyIdValue != null) {
            if (isOk) {
                cell = row.getCell(headers.get(Collogin));
                String login = cell.getStringCellValue();
                if (login != null && !login.isEmpty()) {
                    Shopper shopper = shopperRepository.findByLogin(login);
                    if (shopper == null) {
                        throw new ShopperNotFoundException(login);
                    }

                    String cliente = row.getCell(headers.get(ColCliente))
                            .getStringCellValue();
                    /*
                     * String apellido = row.getCell(headers.get(ColApellido))
                     * .getStringCellValue(); String nombre =
                     * row.getCell(headers.get(ColNombre))
                     * .getStringCellValue();
                     */
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
                            headers.get(ColSubcuestionario))
                            .getStringCellValue();
                    String idSucursal = row.getCell(headers.get(ColIDSucursal))
                            .getStringCellValue();
                    /*
                     * String nombreSucursal = row.getCell(
                     * headers.get(ColNombreSucursal)).getStringCellValue();
                     */
                    String direccionSucursal = row.getCell(
                            headers.get(ColDireccionSucursal))
                            .getStringCellValue();
                    String ciudadSucursal = row.getCell(
                            headers.get(ColCiudadSucursal))
                            .getStringCellValue();
                    String honorariosValue = row.getCell(
                            headers.get(ColHonorarios)).getStringCellValue();
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
                        if (reintegrosValue != null
                                && !reintegrosValue.isEmpty()) {
                            reintegrosValue = reintegrosValue.replaceAll(",",
                                    "");
                            reintegros = new Double(reintegrosValue);
                        }
                    }

                    Branch branch = null;
                    long startTime = System.currentTimeMillis();
                    Client client = clientRepository.getByName(cliente);
                    logger.debug("Get client by name in {} ms",
                            System.currentTimeMillis() - startTime);
                    if (client == null) {
                        client = new Client(cliente);
                        startTime = System.currentTimeMillis();
                        clientRepository.save(client);
                        logger.debug("Save client in {} ms",
                                System.currentTimeMillis() - startTime);
                    } else {
                        final String sucursalCode = idSucursal;
                        startTime = System.currentTimeMillis();
                        branch = branchRepository
                                .findByCode(client, idSucursal);
                        /*
                         * branch = (Branch)
                         * CollectionUtils.find(client.getBranchs(), new
                         * Predicate() {
                         * 
                         * @Override public boolean evaluate(Object item) {
                         * boolean result = false; Branch branch = (Branch)
                         * item; if (branch.getCode() != null) { result =
                         * branch.getCode().equals( sucursalCode); } return
                         * result; }
                         * 
                         * });
                         */
                        logger.debug(
                                "Get branch by code {} for client {} in {} ms",
                                sucursalCode, client.getId(),
                                System.currentTimeMillis() - startTime);
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
                                surveyIdValue.longValue(),
                                TipoItem.shopmetrics, TipoPago.honorarios);
                        logger.debug("Getting honorarios in {} ms",
                                System.currentTimeMillis() - startGetTime);
                        if (debt == null) {
                            debt = new Debt(TipoItem.shopmetrics,
                                    TipoPago.honorarios, shopper.getDni(),
                                    honorarios, fecha, null, subCuestionario,
                                    client, null, branch, null,
                                    surveyIdValue.longValue(), null);
                            debtRepository.save(debt);
                        } else {
                            debt.update(TipoItem.shopmetrics,
                                    TipoPago.honorarios, shopper.getDni(),
                                    honorarios, fecha, null, subCuestionario,
                                    client, null, branch, null, null,
                                    surveyIdValue.longValue(), null);
                            long startUpdateTime = System.currentTimeMillis();
                            debtRepository.update(debt);
                            logger.debug("Updating honorarios in {} ms",
                                    System.currentTimeMillis()
                                            - startUpdateTime);
                        }
                    }
                    logger.debug("Saving honorarios in {} ms",
                            System.currentTimeMillis() - startTime);

                    startTime = System.currentTimeMillis();
                    if (reintegros != null) {
                        long startGetTime = System.currentTimeMillis();
                        Debt debt = debtRepository.getByExternalId(
                                surveyIdValue.longValue(),
                                TipoItem.shopmetrics, TipoPago.reintegros);
                        logger.debug("Getting reintegros in {} ms",
                                System.currentTimeMillis() - startGetTime);
                        if (debt == null) {
                            debt = new Debt(TipoItem.shopmetrics,
                                    TipoPago.reintegros, shopper.getDni(),
                                    reintegros, fecha, null, subCuestionario,
                                    client, null, branch, null,
                                    surveyIdValue.longValue(), null);
                            debtRepository.save(debt);
                        } else {
                            debt.update(TipoItem.shopmetrics,
                                    TipoPago.reintegros, shopper.getDni(),
                                    reintegros, fecha, null, subCuestionario,
                                    client, null, branch, null, null,
                                    surveyIdValue.longValue(), null);
                            long startUpdateTime = System.currentTimeMillis();
                            debtRepository.update(debt);
                            logger.debug("Updating reintegros in {} ms",
                                    System.currentTimeMillis()
                                            - startUpdateTime);
                        }
                    }
                    logger.debug("Saving reintegros in {} ms",
                            System.currentTimeMillis() - startTime);
                }
            } else {
                TipoPago tipoPago = null;
                String honorariosValue = row.getCell(headers.get(ColHonorarios))
                        .getStringCellValue();
                if (honorariosValue != null && !honorariosValue.isEmpty()) {
                    tipoPago = TipoPago.honorarios;
                }
                String reintegrosValue = row.getCell(headers.get(ColReintegros))
                        .getStringCellValue();
                if (reintegrosValue != null && !reintegrosValue.isEmpty()) {
                    tipoPago = TipoPago.reintegros;
                }
                if (tipoPago != null) {
                    Debt debt = debtRepository.getByExternalId(surveyIdValue.longValue(),
                            TipoItem.shopmetrics, tipoPago);
                    if (debt != null) {
                        debt.anulada();
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
            final Integer tipoTitular, final Integer titularId,
            final String shopperDni, final String numeroCheque,
            final Long estadoId, final Date desde, final Date hasta) {
        Map<Class<?>, Map<Position, CellStyle>> styles = new HashMap<Class<?>, Map<Position, CellStyle>>();

        Workbook workbook = new SXSSFWorkbook();

        OrderState state = null;
        if (estadoId != null) {
            state = orderRepository.getOrderState(estadoId);
        }
        List<OrdenPago> orders = orderRepository.find(null, null, "numero",
                true, tipoTitular, titularId, shopperDni, numeroCheque, state,
                desde, hasta);
        try {
            Sheet sheet = workbook.createSheet("Ordenes");
            Row row = sheet.createRow(0);
            createCell(workbook, styles, row, Position.ODD, 0, "Fecha de impresion");
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            createCell(workbook, styles, row, Position.ODD, 1, dateFormat.format(new Date()));

            createHeaderOrderSummary(sheet, workbook, styles);

            int index = 2;
            int currentRow = 2;
            for (OrdenPago order : orders) {
                currentRow += writeOrderSummary(sheet, workbook, styles, order, currentRow, index++);
            }

            workbook.write(outputStream);
        } catch (IOException e) {
            logger.error("Cannot export the orders", e);
        }
    }

    private void createHeaderOrderSummary(Sheet sheet, Workbook workbook,
            Map<Class<?>, Map<Position, CellStyle>> styles) {
        Row row = sheet.createRow(1);
        createCell(workbook, styles, row, Position.ODD, 0, "NUMERO");
        createCell(workbook, styles, row, Position.ODD, 1, "TITULAR");
        createCell(workbook, styles, row, Position.ODD, 2, "CHEQUERA");
        createCell(workbook, styles, row, Position.ODD, 3, "CHEQUE");
        createCell(workbook, styles, row, Position.ODD, 4, "ID TRANSF.");
        createCell(workbook, styles, row, Position.ODD, 5, "ESTADO");
        createCell(workbook, styles, row, Position.ODD, 6, "FACTURA");
        createCell(workbook, styles, row, Position.ODD, 7, "FAC. NRO.");
        createCell(workbook, styles, row, Position.ODD, 8, "IVA HONORARIOS");
        createCell(workbook, styles, row, Position.ODD, 9, "CUIT");
        createCell(workbook, styles, row, Position.ODD, 10, "FECHA PAGO");
        createCell(workbook, styles, row, Position.ODD, 11, "REINTEGROS");
        createCell(workbook, styles, row, Position.ODD, 12, "OTROS GASTOS");
        createCell(workbook, styles, row, Position.ODD, 13, null);
        createCell(workbook, styles, row, Position.ODD, 14, "HONORARIOS");
        createCell(workbook, styles, row, Position.ODD, 15, "IMPORTE IVA");
        createCell(workbook, styles, row, Position.ODD, 16, "TOTAL");
    }

    private int writeOrderSummary(Sheet sheet, Workbook workbook,
            Map<Class<?>, Map<Position, CellStyle>> styles, OrdenPago order, int rowNumber,
            int index) {
        int createdRows = 1;
        int currentRow = rowNumber;
        Row row = sheet.createRow(currentRow++);
        Position position = Position.byModulus(index % 2);
        createCell(workbook, styles, row, position, 0, order.getNumero());
        String titular = null;
        String cuit = order.getCuit();
        if (order.getTipoProveedor().equals(OrdenPago.SHOPPER)) {
            Shopper shopper = shopperRepository.get(order
                    .getProveedor());
            titular = shopper.getName();
        } else {
            Proveedor proveedor = proveedorRepository.get(order
                    .getProveedor());
            titular = proveedor.getDescription();
        }
        createCell(workbook, styles, row, position, 1, titular);
        createCell(workbook, styles, row, position, 2, order.getNumeroChequera());
        createCell(workbook, styles, row, position, 3, order.getNumeroCheque());
        createCell(workbook, styles, row, position, 4, order.getIdTransferencia());
        createCell(workbook, styles, row, position, 5, order.getEstado()
                .getDescription());
        createCell(workbook, styles, row, position, 6, order.getTipoFactura());
        createCell(workbook, styles, row, position, 7, order.getNumeroFactura());
        createCell(workbook, styles, row, position, 8, order.getIva());
        createCell(workbook, styles, row, position, 9, cuit);
        createCell(workbook, styles, row, position, 10, order.getFechaPago());
        double honorarios = 0;
        double reintegros = 0;
        double otrosGastos = 0;
        for (ItemOrden item : order.getItems()) {
            if (item.getTipoPago()
                    .getDescription()
                    .equals(com.ibiscus.shopnchek.domain.admin.TipoPago.HONORARIOS)) {
                honorarios += item.getImporte();
            } else if (item
                    .getTipoPago()
                    .getDescription()
                    .equals(com.ibiscus.shopnchek.domain.admin.TipoPago.REINTEGROS)) {
                reintegros += item.getImporte();
            } else {
                otrosGastos += item.getImporte();
            }
        }
        createCell(workbook, styles, row, position, 11, reintegros);
        createCell(workbook, styles, row, position, 12, otrosGastos);
        createCell(workbook, styles, row, position, 14, honorarios);
        double ivaHonorarios = (order.getIva() * honorarios) / 100;
        createCell(workbook, styles, row, position, 15, ivaHonorarios);
        double total = honorarios + reintegros + otrosGastos
                + ivaHonorarios;
        createCell(workbook, styles, row, position, 16, total);
        boolean createRow = false;
        if (otrosGastos > 0) {
            for (ItemOrden item : order.getItems()) {
                if (item.getTipoPago()
                        .getDescription()
                        .equals(com.ibiscus.shopnchek.domain.admin.TipoPago.OTROS_GASTOS)) {
                    String route = null;
                    if (item.getDebt() != null) {
                        route = item.getDebt().getRoute();
                    }
                    if (route != null) {
                        if (createRow) {
                            row = sheet.createRow(currentRow++);
                            createdRows++;
                        }
                    }
                    if (!createRow) {
                        createCell(workbook, styles, row, position, 13, route);
                    } else {
                        writeRoute(workbook, styles, row, position, route);
                    }
                    createRow = true;
                }
            }
        } else {
            createCell(workbook, styles, row, position, 13, null);
        }
        return createdRows;
    }

    private void writeRoute(final Workbook workbook,
            final Map<Class<?>, Map<Position, CellStyle>> styles, final Row row,
            Position position, final String route) {
        for (int i = 0; i <= 16; i++) {
            String value = null;
            if (i == 13) {
                value = route;
            }
            createCell(workbook, styles, row, position, i, value);
        }
    }

    public void exportDeuda(final OutputStream outputStream,
            final String dniShopper, final boolean includeMcd,
            final boolean includeGac, final boolean includeAdicionales,
            final boolean includeShopmetrics, final Date desde, final Date hasta) {
        Map<Class<?>, CellStyle> styles = new HashMap<Class<?>, CellStyle>();

        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet("Items Adeudados");
    }

    public void reportDeuda(final OutputStream outputStream, final Date desde,
            final Date hasta) {
        Map<Class<?>, CellStyle> styles = new HashMap<Class<?>, CellStyle>();

        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet("Items Adeudados");

    }

    public void reportAdicionales(final OutputStream outputStream) {
        Map<Class<?>, CellStyle> styles = new HashMap<Class<?>, CellStyle>();

        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet("Items Adeudados");

    }

    private Cell createCell(final Workbook workbook,
            final Map<Class<?>, Map<Position, CellStyle>> styles, final Row theRow,
            Position position, final int theColumnIndex, final Object theValue) {

        Cell cell = theRow.createCell(theColumnIndex);
        Class<?> valueClass = String.class;
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
            } else if (theValue.getClass() == Boolean.class) {
                cell.setCellValue(((Boolean) theValue).booleanValue());
            } else {
                String cellValue = theValue.toString();

                cell.setCellValue(cellValue);
            }
            valueClass = theValue.getClass();
        }
        Map<Position, CellStyle> cellStyles = getCellStyle(workbook, styles,
                valueClass);
        if (cellStyles != null) {
            cell.setCellStyle(cellStyles.get(position));
        }
        return cell;
    }

    /**
     * Search for the cell style appropriate for this cell according the data
     * type, returns null if there is no cell style for this data type.
     * 
     * @param theDataType
     *            the data type whose cell style we want to retrieve, cannot be
     *            null.
     * 
     * @return the cell style or null.
     */
    private Map<Position, CellStyle> getCellStyle(final Workbook workbook,
            final Map<Class<?>, Map<Position, CellStyle>> styles, final Class<?> theDataType) {
        Validate.notNull(theDataType, "the data type cannot be null");

        Map<Position, CellStyle> cellStyles = styles.get(theDataType);

        if (cellStyles == null) {
            CellStyle oddCellStyle = null;
            CellStyle evenCellStyle = null;
            CreationHelper creationHelper = workbook.getCreationHelper();
            // If, in mysql query, the DATE() function is called, then
            // theDataType is
            // a java.sql.Date object. If the TIME() function is called, then
            // theDataType is a java.sql.Time object.
            if (theDataType == Integer.class || theDataType == Long.class) {
                oddCellStyle = workbook.createCellStyle();
                oddCellStyle.setDataFormat(creationHelper.createDataFormat()
                        .getFormat("#,##0"));
                evenCellStyle = workbook.createCellStyle();
                evenCellStyle.setDataFormat(creationHelper.createDataFormat()
                        .getFormat("#,##0"));
            } else if (theDataType == Float.class
                    || theDataType == Double.class) {
                oddCellStyle = workbook.createCellStyle();
                oddCellStyle.setDataFormat(creationHelper.createDataFormat()
                        .getFormat("$ #,##0.00"));
                evenCellStyle = workbook.createCellStyle();
                evenCellStyle.setDataFormat(creationHelper.createDataFormat()
                        .getFormat("$ #,##0.00"));
            } else if (theDataType == java.sql.Date.class
                    || theDataType == java.sql.Timestamp.class) {
                oddCellStyle = workbook.createCellStyle();
                oddCellStyle.setDataFormat(creationHelper.createDataFormat()
                        .getFormat("dd/mm/yyyy"));
                evenCellStyle = workbook.createCellStyle();
                evenCellStyle.setDataFormat(creationHelper.createDataFormat()
                        .getFormat("dd/mm/yyyy"));
            } else if (theDataType == java.sql.Time.class) {
                oddCellStyle = workbook.createCellStyle();
                oddCellStyle.setDataFormat(creationHelper.createDataFormat()
                        .getFormat("h:mm:ss AM/PM"));
                evenCellStyle = workbook.createCellStyle();
                evenCellStyle.setDataFormat(creationHelper.createDataFormat()
                        .getFormat("h:mm:ss AM/PM"));
            } else {
                oddCellStyle = workbook.createCellStyle();
                evenCellStyle = workbook.createCellStyle();
            }
            if (oddCellStyle != null && evenCellStyle != null) {
                ((XSSFCellStyle) evenCellStyle).setFillForegroundColor(
                        new XSSFColor(new Color(255, 243, 203)));
                ((XSSFCellStyle) evenCellStyle).setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cellStyles = new HashMap<Position, CellStyle>();
                cellStyles.put(Position.ODD, oddCellStyle);
                cellStyles.put(Position.EVEN, evenCellStyle);
                styles.put(theDataType, cellStyles);
            }
        }

        return cellStyles;
    }
}
