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

    private enum Position {
        ODD(1), EVEN(0);

        private final int modulus;

        Position(int modulus) {
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

    private OrderRepository orderRepository;

    private ShopperRepository shopperRepository;

    private ProveedorRepository proveedorRepository;

    // private ExecutorService executor = Executors.newFixedThreadPool(10);

    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void setShopperRepository(ShopperRepository shopperRepository) {
        this.shopperRepository = shopperRepository;
    }

    public void setProveedorRepository(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
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
                            writeRoute(workbook, styles, row, position, route);
                            createdRows++;
                        }
                    }
                    if (!createRow) {
                        createCell(workbook, styles, row, position, 13, route);
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
