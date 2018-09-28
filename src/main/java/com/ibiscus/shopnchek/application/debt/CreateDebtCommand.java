package com.ibiscus.shopnchek.application.debt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.ibiscus.shopnchek.application.email.CommunicationService;
import com.ibiscus.shopnchek.domain.security.UserRepository;
import org.apache.commons.lang.Validate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
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
import com.ibiscus.shopnchek.domain.security.User;

import static com.google.common.collect.Lists.newArrayList;
import static com.ibiscus.shopnchek.domain.debt.Debt.State.creada;

public class CreateDebtCommand implements Command<List<Debt>> {

	private static final String ADDITIONAL_ROUTE = "debt/edit/";

	private CommunicationService communicationService;

	private DebtRepository debtRepository;

	private BranchRepository branchRepository;

	private ClientRepository clientRepository;

	private ShopperRepository shopperRepository;

	private UserRepository userRepository;

	private String from;

	private String site;

	private VisitaDto visitaDto;

	private User operator;

	public CreateDebtCommand() {
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<Debt> execute() {
		Validate.notNull(visitaDto, "The visit DTO cannot be null");
		List<Debt> debts = new ArrayList<Debt>();

		TipoItem tipoItem = TipoItem.manual;
		String clientDescription = visitaDto.getClientDescription();
		Client client = null;
		if (visitaDto.getClientId() != null) {
			client = clientRepository.get(visitaDto.getClientId());
			clientDescription = client.getName();
		}
		String branchDescription = visitaDto.getBranchDescription();
		Branch branch = null;
		if (visitaDto.getBranchId() != null) {
			branch = branchRepository.get(visitaDto.getBranchId());
			branchDescription = branch.getAddress();
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date fecha;
		try {
			fecha = dateFormat.parse(visitaDto.getFecha());
		} catch (ParseException e) {
			throw new RuntimeException("Cannot parse visit date", e);
		}
		for (DebtDto debtDto : visitaDto.getItems()) {
			TipoPago tipoPago = TipoPago.valueOf(debtDto.getTipoPago());
            String route = null;
            if (TipoPago.otrosgastos == tipoPago) {
                route = visitaDto.getRoute();
            }
			Debt debt = new Debt(tipoItem, tipoPago, creada, visitaDto.getShopperDni(),
					debtDto.getImporte(), fecha, debtDto.getObservacion(),
					null, client, clientDescription, branch, branchDescription,
					route, null, operator.getUsername());
			debtRepository.save(debt);
			Shopper shopper = shopperRepository.findByDni(debt.getShopperDni());
			debt.updateShopper(shopper);
			sendMail(debt);
			debts.add(debt);
		}
		return debts;
	}

	private void sendMail(Debt debt) {
		Iterable<User> users = getUserToSendNotification();
		String emailContent = getEmailBody(debt);
		for (User user : users) {
			communicationService.sendMail(from, user.getEmail(), "Aprobacion de adicional", emailContent);
		}
	}

	private String getEmailBody(Debt debt) {
		StringBuilder builder = new StringBuilder("El siguiente adicional necesita ser revisado para su aprobacion:\n\n");
		builder.append(site);
		builder.append(ADDITIONAL_ROUTE);
		builder.append(debt.getId());
		return builder.toString();
	}

	private Iterable<User> getUserToSendNotification() {
		return newArrayList(userRepository.findByUsername("fede"));
	}

	public void setDebtRepository(final DebtRepository debtRepository) {
		this.debtRepository = debtRepository;
	}

	public void setClientRepository(final ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}

	public void setBranchRepository(final BranchRepository branchRepository) {
		this.branchRepository = branchRepository;
	}

	public void setShopperRepository(final ShopperRepository shopperRepository) {
		this.shopperRepository = shopperRepository;
	}

	public void setVisitaDto(final VisitaDto visitaDto) {
		this.visitaDto = visitaDto;
	}

	public void setOperator(final User operator) {
		this.operator = operator;
	}

	public void setCommunicationService(CommunicationService communicationService) {
		this.communicationService = communicationService;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
}
