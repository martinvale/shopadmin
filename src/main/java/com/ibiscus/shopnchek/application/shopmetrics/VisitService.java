package com.ibiscus.shopnchek.application.shopmetrics;

import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;
import com.ibiscus.shopnchek.domain.debt.*;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class VisitService {

    private static final Logger logger = LoggerFactory.getLogger(VisitService.class);

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

    private DebtRepository debtRepository;

    private ClientRepository clientRepository;

    private BranchRepository branchRepository;

    private ShopperRepository shopperRepository;

    @Transactional
    public boolean importRow(final Map<Integer, Integer> headers, final Row row) {
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
                    long startGetTime = System.currentTimeMillis();
                    Debt debt = debtRepository.getByExternalId(
                            surveyIdValue.longValue(), TipoItem.shopmetrics,
                            TipoPago.honorarios);
                    logger.debug("Getting honorarios in {} ms",
                            System.currentTimeMillis() - startGetTime);
                    if (honorarios != null) {
                        if (debt == null) {
                            debt = new Debt(TipoItem.shopmetrics,
                                    TipoPago.honorarios, shopper.getIdentityId(),
                                    honorarios, fecha, null, subCuestionario,
                                    client, client.getName(), branch, null,
                                    surveyIdValue.longValue(), null);
                            debtRepository.save(debt);
                        } else {
                            debt.update(TipoItem.shopmetrics, TipoPago.honorarios,
                                    shopper.getIdentityId(), honorarios, fecha, null,
                                    subCuestionario, client, client.getName(),
                                    branch, null, null, surveyIdValue.longValue(),
                                    null);
                            if (debt.isCancelled()) {
                                debt.reopen();
                            }
                            long startUpdateTime = System.currentTimeMillis();
                            debtRepository.update(debt);
                            logger.debug("Updating honorarios in {} ms",
                                    System.currentTimeMillis() - startUpdateTime);
                        }
                        logger.debug("Saving honorarios in {} ms",
                                System.currentTimeMillis() - startTime);
                    } else {
                        if (debt != null && debt.isPending()) {
                            debtRepository.remove(debt);
                        }
                    }

                    startTime = System.currentTimeMillis();
                    startGetTime = System.currentTimeMillis();
                    debt = debtRepository.getByExternalId(
                            surveyIdValue.longValue(), TipoItem.shopmetrics,
                            TipoPago.reintegros);
                    logger.debug("Getting reintegros in {} ms",
                            System.currentTimeMillis() - startGetTime);
                    if (reintegros != null) {
                        if (debt == null) {
                            debt = new Debt(TipoItem.shopmetrics,
                                    TipoPago.reintegros, shopper.getIdentityId(),
                                    reintegros, fecha, null, subCuestionario,
                                    client, client.getName(), branch, null,
                                    surveyIdValue.longValue(), null);
                            debtRepository.save(debt);
                        } else {
                            debt.update(TipoItem.shopmetrics, TipoPago.reintegros,
                                    shopper.getIdentityId(), reintegros, fecha, null,
                                    subCuestionario, client, client.getName(),
                                    branch, null, null, surveyIdValue.longValue(),
                                    null);
                            if (debt.isCancelled()) {
                                debt.reopen();
                            }
                            long startUpdateTime = System.currentTimeMillis();
                            debtRepository.update(debt);
                            logger.debug("Updating reintegros in {} ms",
                                    System.currentTimeMillis() - startUpdateTime);
                        }
                        logger.debug("Saving reintegros in {} ms",
                                System.currentTimeMillis() - startTime);
                    } else {
                        if (debt != null && debt.isPending()) {
                            debtRepository.remove(debt);
                        }
                    }
                }
            } else {
                String honorariosValue = row
                        .getCell(headers.get(ColHonorarios))
                        .getStringCellValue();
                if (honorariosValue != null && !honorariosValue.isEmpty()) {
                    Debt debt = debtRepository.getByExternalId(
                            surveyIdValue.longValue(), TipoItem.shopmetrics,
                            TipoPago.honorarios);
                    if (debt != null) {
                        debt.anulada();
                        debtRepository.update(debt);
                    }
                }
                String reintegrosValue = row
                        .getCell(headers.get(ColReintegros))
                        .getStringCellValue();
                if (reintegrosValue != null && !reintegrosValue.isEmpty()) {
                    Debt debt = debtRepository.getByExternalId(
                            surveyIdValue.longValue(), TipoItem.shopmetrics,
                            TipoPago.reintegros);
                    if (debt != null) {
                        debt.anulada();
                        debtRepository.update(debt);
                    }
                }
            }
        }
        return end;
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
}
