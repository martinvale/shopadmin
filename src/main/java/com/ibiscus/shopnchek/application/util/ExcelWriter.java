package com.ibiscus.shopnchek.application.util;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWriter {

	private final XSSFWorkbook workbook;

	private final XSSFSheet sheet;

	private int rowIndex = 1;

	private Map<Class<?>, CellStyle> styles = new HashMap<Class<?>, CellStyle>();

	public ExcelWriter(final List<String> header) {
		workbook = new XSSFWorkbook();
		sheet = workbook.createSheet();
		Row row = sheet.createRow(0);
		for (int i = 0; i < header.size(); i++) {
			createCell(row, i, header.get(i));
		}
	}

	public void write(final List<Object> columns) {
		Row row = sheet.createRow(rowIndex++);
		for (int i = 0; i < columns.size(); i++) {
			createCell(row, i, columns.get(i));
		}
	}

	/** Creates a cell and writes into it the value received. It applies the style
	 * needed, in order to write the data with the correct format.
	 *
	 * @param row the row where the data will be written. It cannot be null.
	 * @param columnIndex the column of the row where the data will be written.
	 * @param value the value to be written. It can be null.
	 *
	 * @return the cell containing the value in the correct format.
	 */
	private Cell createCell(final Row row, final int columnIndex, final Object value) {
		Cell cell = row.createCell(columnIndex);

		if (value != null) {
			if (value.getClass() == Integer.class || value.getClass() == Long.class) {
				cell.setCellValue(((Number) value).intValue());
			} else if (value.getClass() == Float.class || value.getClass() == Double.class) {
				cell.setCellValue(((Number) value).doubleValue());
			} else if (value.getClass() == Timestamp.class || value.getClass() == java.sql.Date.class) {
				cell.setCellValue((Timestamp) value);
			} else if(value.getClass() == Boolean.class) {
				cell.setCellValue(((Boolean) value).booleanValue());
			} else {
				String cellValue = value.toString();
				cell.setCellValue(cellValue);
			}
			CellStyle cellStyle = getCellStyle(value.getClass());
			if (cellStyle != null) {
				cell.setCellStyle(cellStyle);
			}
		}
		return cell;
	}

	/** Search for the cell style appropriate for this cell according the data
	 * type, returns null if there is no cell style for this data type.
	 *
	 * @param dataType the data type whose cell style we want to retrieve,
	 *  cannot be null.
	 *
	 * @return the cell style or null.
	 */
	private CellStyle getCellStyle(final Class<?> dataType) {
		Validate.notNull(dataType, "the data type cannot be null");

		CellStyle theCellStyle = styles.get(dataType);

		if (theCellStyle == null) {
			CreationHelper creationHelper = workbook.getCreationHelper();
			// If, in mysql query, the DATE() function is called, then theDataType is
			// a java.sql.Date object. If the TIME() function is called, then
			// theDataType is a java.sql.Time object.
			if (dataType == Integer.class || dataType == Long.class) {
				theCellStyle = workbook.createCellStyle();
				theCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("#,##0"));
			} else if (dataType == Float.class || dataType == Double.class) {
				theCellStyle = workbook.createCellStyle();
				theCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("$ #,##0.00"));
			} else if (dataType == java.sql.Date.class || dataType == java.sql.Timestamp.class) {
				theCellStyle = workbook.createCellStyle();
				theCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/mm/yyyy"));
			} else if (dataType == java.sql.Time.class) {
				theCellStyle = workbook.createCellStyle();
				theCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("h:mm:ss AM/PM"));
			}
			if (theCellStyle != null) {
				styles.put(dataType, theCellStyle);
			}
		}

		return theCellStyle;
	}

	public Workbook getWorkbook() {
		return workbook;
	}
}
