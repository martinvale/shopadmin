package com.ibiscus.shopnchek.application.shopmetrics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

public class ImportShopmetricsService {

    private static final Logger logger = LoggerFactory.getLogger(ImportShopmetricsService.class);

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

    private final VisitService visitService;

    private final TaskService taskService;

    public ImportShopmetricsService(VisitService visitService, TaskService taskService) {
        this.visitService = visitService;
        this.taskService = taskService;
    }

    public void importFile(String fileName, InputStream inputStream) {
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

        String processName = taskService.createTask(fileName);

        double startTime = System.currentTimeMillis();
        importFile(processName, tempFile);
        logger.debug("File processed in {} ms", System.currentTimeMillis() - startTime);
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
            taskService.updateTaskErrorMessage(name, "Invalid import file: " + name);
            throw new RuntimeException("Cannot read the XLS file", e);
        } catch (InvalidFormatException e) {
            taskService.updateTaskErrorMessage(name, "Invalid import file: " + name);
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

        Row row = rowIterator.next();
        Map<Integer, Integer> headers = getCellHeaders(row);

        theCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(
                "yyyy-mm-dd"));

        List<ShopmetricsUserDto> users = new LinkedList<ShopmetricsUserDto>();

        taskService.start(name);
        boolean salir = false;
        try {
            while (rowIterator.hasNext() && !salir) {
                row = rowIterator.next();
                currentRowIndex++;
                try {
                    long startTime = System.currentTimeMillis();
                    salir = visitService.importRow(headers, row);
                    logger.debug("Row import total time: {} ms",
                            System.currentTimeMillis() - startTime);
                } catch (ShopperNotFoundException e) {
                    logger.warn("Cannot import Shopmetrics item for shopper with login "
                            + e.getIdentifier());
                    users.add(new ShopmetricsUserDto(e.getIdentifier(), null,
                            null));
                }
                if ((currentRowIndex % intervalRowCount) == 0) {
                    taskService.updatePorcentage(name, (currentRowIndex * 100) / rowCount);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot import file " + name, e);
        } finally {
            logger.info("Finish file {} import", name);
        }
        taskService.finish(name, users);
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
