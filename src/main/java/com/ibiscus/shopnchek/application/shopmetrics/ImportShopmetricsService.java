package com.ibiscus.shopnchek.application.shopmetrics;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
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
    private static final int ColOtrosGastos = 16;

    private final VisitService visitService;

    private final TaskService taskService;

    public ImportShopmetricsService(VisitService visitService, TaskService taskService) {
        this.visitService = visitService;
        this.taskService = taskService;
    }

    public void importFile(String fileName, InputStream inputStream) {
        logger.info("Staring to import task: {}", fileName);

        String processName = taskService.createTask(fileName);
        double startTime = System.currentTimeMillis();
        try {
            processFile(processName, inputStream);
        } catch (Exception e) {
            taskService.updateTaskErrorMessage(processName, "Invalid import file: " + fileName);
            throw new RuntimeException("Cannot read the XLS file", e);
        }
        logger.debug("File processed in {} ms", System.currentTimeMillis() - startTime);
    }

    private void processFile(String name, InputStream inputStream) throws IOException, InvalidFormatException {
        List<ShopmetricsUserDto> users = new LinkedList<ShopmetricsUserDto>();
        OPCPackage opcPackage = OPCPackage.open(inputStream);
        try {
            Workbook workbook = WorkbookFactory.create(opcPackage);
            Sheet sheet = workbook.getSheetAt(2);

            Iterator<Row> rowIterator = sheet.rowIterator();
            int rowCount = sheet.getLastRowNum();
            int currentRowIndex = 0;
            int intervalRowCount = (5 * rowCount) / 100;
            if (intervalRowCount == 0) {
                intervalRowCount = 1;
            }

            Row row = rowIterator.next();
            Map<Integer, Integer> headers = getCellHeaders(row);

            taskService.start(name);
            boolean salir = false;
            while (rowIterator.hasNext() && !salir) {
                row = rowIterator.next();
                currentRowIndex++;
                try {
                    long startTime = System.currentTimeMillis();
                    salir = visitService.importRow(headers, row);
                    logger.debug("Row import total time: {} ms", System.currentTimeMillis() - startTime);
                } catch (ShopperNotFoundException e) {
                    logger.warn("Cannot import Shopmetrics item for shopper with login ", e.getIdentifier());
                    users.add(new ShopmetricsUserDto(e.getIdentifier(),null,null));
                }
                if ((currentRowIndex % intervalRowCount) == 0) {
                    taskService.updatePorcentage(name, (currentRowIndex * 100) / rowCount);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot import file " + name, e);
        } finally {
            opcPackage.close();
            logger.info("Finish file {} import", name);
        }
        taskService.finish(name, users);
    }

    private Map<Integer, Integer> getCellHeaders(final Row row) {
        Map<Integer, Integer> headers = new HashMap<Integer, Integer>();

        Iterator<Cell> cellIterator = row.cellIterator();

        int currentPos = 0;
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
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
            if (cell.getStringCellValue().equalsIgnoreCase("Reintegro")) {
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
            if (cell.getStringCellValue().equalsIgnoreCase("Otros Gastos")) {
                headers.put(ColOtrosGastos, currentPos);
            }

            currentPos++;
        }
        return headers;
    }

}
