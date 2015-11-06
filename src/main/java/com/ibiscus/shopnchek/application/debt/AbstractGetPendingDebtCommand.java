package com.ibiscus.shopnchek.application.debt;

import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.debt.Branch;
import com.ibiscus.shopnchek.domain.debt.BranchRepository;
import com.ibiscus.shopnchek.domain.debt.Client;
import com.ibiscus.shopnchek.domain.debt.ClientRepository;
import com.ibiscus.shopnchek.domain.debt.Debt;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;
import com.ibiscus.shopnchek.domain.debt.Feed;
import com.ibiscus.shopnchek.domain.debt.FeedRepository;

public abstract class AbstractGetPendingDebtCommand implements Command<Boolean> {

	private static Logger logger = Logger.getLogger(AbstractGetPendingDebtCommand.class.getName());

	private DataSource dataSource;

	private DebtRepository debtRepository;

	private ClientRepository clientRepository;

	private BranchRepository branchRepository;

	private FeedRepository feedRepository;

	public AbstractGetPendingDebtCommand() {
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Boolean execute() {
		logger.info("Get pending debt for feed " + getCode());
		boolean endReached = true;
		Feed feed = feedRepository.getByCode(getCode());
		if (!feed.isActive()) {
			return true;
		}
		Long lastProcessedId = feed.getLastProcessedId();
		if (lastProcessedId == null) {
			lastProcessedId = 0L;
		}

		List<Debt> items = getDebt(lastProcessedId);
		if (!items.isEmpty()) {
			endReached = false;
			for (Debt debt : items) {
				debtRepository.save(debt);
				if (debt.getExternalId() > lastProcessedId) {
					lastProcessedId = debt.getExternalId();
				}
			}
			feed.update(lastProcessedId);
		}
		return endReached;
	}

	protected abstract String getCode();

	protected Client getClient(final String name) {
		Client client = clientRepository.getByName(name);
		if (client == null) {
			client = new Client(name);
			clientRepository.save(client);
		}
		return client;
	}

	protected Branch getBranch(final Client client, final String address) {
		Branch branch = (Branch) CollectionUtils.find(client.getBranchs(), new Predicate() {

			@Override
			public boolean evaluate(Object item) {
				boolean result = false;
				Branch branch = (Branch) item;
				if (branch.getAddress() != null) {
					result = branch.getAddress().equals(address);
				}
				return result;
			}

		});
		if (branch == null) {
			branch = new Branch(client, null, address);
			branchRepository.save(branch);
		}
		return branch;
	}

	protected abstract List<Debt> getDebt(final Long lastProcessedId);

	protected DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setDebtRepository(final DebtRepository debtRepository) {
		this.debtRepository = debtRepository;
	}

	public void setClientRepository(ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}

	public void setBranchRepository(BranchRepository branchRepository) {
		this.branchRepository = branchRepository;
	}

	public void setFeedRepository(FeedRepository feedRepository) {
		this.feedRepository = feedRepository;
	}
}
