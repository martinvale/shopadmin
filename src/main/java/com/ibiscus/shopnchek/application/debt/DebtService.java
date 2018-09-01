package com.ibiscus.shopnchek.application.debt;

import com.ibiscus.shopnchek.domain.debt.Client;
import com.ibiscus.shopnchek.domain.debt.Debt;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class DebtService {

    private final DebtRepository debtRepository;

    public DebtService(DebtRepository debtRepository) {
        this.debtRepository = debtRepository;
    }

    public List<Debt> getDebt(String shopperDni, Client client, Date from, Date to) {
        return debtRepository.find(0, null, "fecha",
                true, shopperDni, null, from, to, null, null, null, client);
    }
}
