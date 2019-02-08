package com.ibiscus.shopnchek.application.shopper;

import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.ibiscus.shopnchek.domain.admin.Shopper.GENDER.FEMALE;
import static com.ibiscus.shopnchek.domain.admin.Shopper.GENDER.MALE;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

public class ImportShoppersFileCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImportShoppersFileCommand.class);

	private static final DateFormat BIRTHDAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private static final Integer ARGENTINA = 1;

	private static final Integer LOGIN_COLUMN = 1;
	private static final Integer FIRST_NAME_COLUMN = 3;
	private static final Integer MIDDLE_NAME_COLUMN = 4;
	private static final Integer SURNAME_COLUMN = 5;
	private static final Integer BIRTHDAY_COLUMN = 6;
	private static final Integer GENDER_COLUMN = 8;
	private static final Integer ADDRESS_COLUMN = 9;
    private static final Integer CITY_COLUMN = 11;
	private static final Integer STATE_COLUMN = 12;
	private static final Integer POSTAL_CODE_COLUMN = 16;
	private static final Integer PARTICULAR_PHONE_COLUMN = 17;
	private static final Integer WORK_PHONE_COLUMN = 18;
	private static final Integer CELL_PHONE_COLUMN = 19;
	private static final Integer EMAIL_COLUMN = 20;
	private static final Integer EDUCATION_COLUMN = 22;
	private static final Integer CONFIDENTIALITY_COLUMN = 23;

	private ShopperRepository shopperRepository;

	private SaveShopperCommand saveShopperCommand;

	private CreateShopperCommand createShopperCommand;

	ImportShoppersFileCommand() {
	}

	public List<ImportItemResult> execute(MultipartFile file) throws IOException {
		List<ImportItemResult> result = newArrayList();
		String name = file.getOriginalFilename();
		LOGGER.info("Start to import file: {}", name);
		try {
			Sheet sheet = getExcelSheet(file.getInputStream());
			Iterator<Row> rowIterator = getRowIterator(sheet);

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				long startTime = System.currentTimeMillis();
				result.add(importRow(row));
				LOGGER.debug("Row import time: {} ms", System.currentTimeMillis() - startTime);
			}
		} catch (Exception e) {
			LOGGER.error(String.format("Cannot import file %s", name), e);
		}
		LOGGER.info("Finish to import file: {}", name);
		return result;
	}

	private Iterator<Row> getRowIterator(Sheet sheet) {
		Iterator<Row> rowIterator = sheet.rowIterator();
		//skeep the header
		rowIterator.next();
		return rowIterator;
	}

	@Transactional
	public ImportItemResult importRow(Row row) {
		Cell cell = row.getCell(LOGIN_COLUMN);
		String rowDescriptor = getRowDescriptor(row);
		LOGGER.info("Importing row for shopper: {}", cell.getStringCellValue());
		try {
			Shopper shopper = shopperRepository.findByLogin(cell.getStringCellValue(), false);
			if (shopper != null) {
				LOGGER.info("Updating shopper: {}", cell.getStringCellValue());
				EditedShopper editedShopper = createUpdatedShopper(shopper.getId(), row);
				saveShopperCommand.executeFromShopmetrics(editedShopper);
			} else {
				LOGGER.info("Creating shopper: {}", cell.getStringCellValue());
				NewShopper newShopper = createNewShopper(row);
				createShopperCommand.execute(newShopper);
			}
			return new ImportItemResult(rowDescriptor);
		} catch (Exception e) {
			return new ImportItemResult(rowDescriptor, e.getMessage());
		}
	}

	private String getRowDescriptor(Row row) {
		StringBuilder builder = new StringBuilder();
		Cell cell = row.getCell(SURNAME_COLUMN);
		builder.append(cell.getStringCellValue());
		builder.append(", ");
		cell = row.getCell(FIRST_NAME_COLUMN);
		builder.append(cell.getStringCellValue());
		builder.append(" (");
		cell = row.getCell(LOGIN_COLUMN);
		builder.append(cell.getStringCellValue());
		builder.append(")");

		return builder.toString();
	}

	private EditedShopper createUpdatedShopper(Long id, Row row) {
		EditedShopper editedShopper = new EditedShopper();
		editedShopper.setId(id);
		Cell cellLoginShopmetrics = row.getCell(LOGIN_COLUMN);
		editedShopper.setLoginShopmetrics(cellLoginShopmetrics.getStringCellValue());
		Cell cellSurname = row.getCell(SURNAME_COLUMN);
		editedShopper.setSurname(cellSurname.getStringCellValue());
		Cell cellFirstName = row.getCell(FIRST_NAME_COLUMN);
		Cell cellMiddleName = row.getCell(MIDDLE_NAME_COLUMN);
		StringBuilder firstNameBuilder = new StringBuilder(cellFirstName.getStringCellValue());
		if (isNotEmpty(cellMiddleName.getStringCellValue())) {
			firstNameBuilder.append(" ").append(cellMiddleName.getStringCellValue());
		}
		editedShopper.setFirstName(firstNameBuilder.toString());
		Cell cellAddress = row.getCell(ADDRESS_COLUMN);
		editedShopper.setAddress(cellAddress.getStringCellValue());
		Cell cellCity = row.getCell(CITY_COLUMN);
		editedShopper.setRegion(cellCity.getStringCellValue());
		Cell cellState = row.getCell(STATE_COLUMN);
		editedShopper.setState(cellState.getStringCellValue());
		editedShopper.setCountry(ARGENTINA);
		Cell cellPostalCode = row.getCell(POSTAL_CODE_COLUMN);
		editedShopper.setPostalCode(cellPostalCode.getStringCellValue());
		Cell cellWorkPhone = row.getCell(WORK_PHONE_COLUMN);
		editedShopper.setWorkPhone(cellWorkPhone.getStringCellValue());
		Cell cellParticularPhone = row.getCell(PARTICULAR_PHONE_COLUMN);
		editedShopper.setParticularPhone(cellParticularPhone.getStringCellValue());
		Cell cellMobilePhone = row.getCell(CELL_PHONE_COLUMN);
		editedShopper.setCellPhone(cellMobilePhone.getStringCellValue());
		Cell cellEmail = row.getCell(EMAIL_COLUMN);
		editedShopper.setEmail(cellEmail.getStringCellValue());
		Cell cellBirthday = row.getCell(BIRTHDAY_COLUMN);
		Date birthDate = getBirthDate(cellBirthday);
		editedShopper.setBirthDate(birthDate);
		Cell cellGender = row.getCell(GENDER_COLUMN);
		editedShopper.setGender(cellGender.getStringCellValue());
		Cell cellEducation = row.getCell(EDUCATION_COLUMN);
		editedShopper.setEducation(cellEducation.getStringCellValue());
		Cell cellConfidentiality = row.getCell(CONFIDENTIALITY_COLUMN);
		editedShopper.setConfidentiality(cellConfidentiality.getCellType() != Cell.CELL_TYPE_BLANK ? true : false);
		return editedShopper;
	}

	private NewShopper createNewShopper(Row row) {
		NewShopper newShopper = new NewShopper();
		Cell cellLoginShopmetrics = row.getCell(LOGIN_COLUMN);
		newShopper.setLoginShopmetrics(cellLoginShopmetrics.getStringCellValue());
		Cell cellSurname = row.getCell(SURNAME_COLUMN);
		newShopper.setSurname(cellSurname.getStringCellValue());
		Cell cellFirstName = row.getCell(FIRST_NAME_COLUMN);
		Cell cellMiddleName = row.getCell(MIDDLE_NAME_COLUMN);
		StringBuilder firstNameBuilder = new StringBuilder(cellFirstName.getStringCellValue());
		if (isNotEmpty(cellMiddleName.getStringCellValue())) {
			firstNameBuilder.append(" ").append(cellMiddleName.getStringCellValue());
		}
		newShopper.setFirstName(firstNameBuilder.toString());
		Cell cellAddress = row.getCell(ADDRESS_COLUMN);
		newShopper.setAddress(cellAddress.getStringCellValue());
        Cell cellCity = row.getCell(CITY_COLUMN);
		newShopper.setRegion(cellCity.getStringCellValue());
		Cell cellState = row.getCell(STATE_COLUMN);
		newShopper.setState(cellState.getStringCellValue());
		newShopper.setCountry(ARGENTINA);
		Cell cellPostalCode = row.getCell(POSTAL_CODE_COLUMN);
		newShopper.setPostalCode(cellPostalCode.getStringCellValue());
		Cell cellWorkPhone = row.getCell(WORK_PHONE_COLUMN);
		newShopper.setWorkPhone(cellWorkPhone.getStringCellValue());
		Cell cellParticularPhone = row.getCell(PARTICULAR_PHONE_COLUMN);
		newShopper.setParticularPhone(cellParticularPhone.getStringCellValue());
		Cell cellMobilePhone = row.getCell(CELL_PHONE_COLUMN);
		newShopper.setCellPhone(cellMobilePhone.getStringCellValue());
		Cell cellEmail = row.getCell(EMAIL_COLUMN);
		newShopper.setEmail(cellEmail.getStringCellValue());
		Cell cellBirthday = row.getCell(BIRTHDAY_COLUMN);
		Date birthDate = getBirthDate(cellBirthday);
		newShopper.setBirthDate(birthDate);
		Cell cellGender = row.getCell(GENDER_COLUMN);
        newShopper.setGender(FEMALE.getCode().equals(cellGender.getStringCellValue()) ? FEMALE.name() : MALE.name());
		Cell cellEducation = row.getCell(EDUCATION_COLUMN);
		newShopper.setEducation(cellEducation.getStringCellValue());
		Cell cellConfidentiality = row.getCell(CONFIDENTIALITY_COLUMN);
		newShopper.setConfidentiality(cellConfidentiality.getCellType() != Cell.CELL_TYPE_BLANK ? true : false);
		return newShopper;

	}

	private Date getBirthDate(Cell cellBirthday) {
		try {
			return BIRTHDAY_FORMAT.parse(cellBirthday.getStringCellValue());
		} catch (ParseException e) {
			throw new RuntimeException(String.format("Cannot parse date: %s", cellBirthday.getStringCellValue()));
		}
	}

	private Sheet getExcelSheet(InputStream inputStream) throws IOException, InvalidFormatException {
		OPCPackage opcPackage = OPCPackage.open(inputStream);
		Workbook workbook = WorkbookFactory.create(opcPackage);
		return workbook.getSheetAt(0);
	}

	public void setShopperRepository(ShopperRepository shopperRepository) {
        this.shopperRepository = shopperRepository;
    }

	public void setSaveShopperCommand(SaveShopperCommand saveShopperCommand) {
		this.saveShopperCommand = saveShopperCommand;
	}

	public void setCreateShopperCommand(CreateShopperCommand createShopperCommand) {
		this.createShopperCommand = createShopperCommand;
	}
}
