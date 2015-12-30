package com.ibiscus.shopnchek.application.feed;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.debt.Debt;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;
import com.ibiscus.shopnchek.domain.debt.Feed;
import com.ibiscus.shopnchek.domain.debt.FeedRepository;
import com.ibiscus.shopnchek.domain.debt.TipoItem;

public class SaveFeedCommand implements Command<Feed> {

	private FeedRepository feedRepository;

	private DebtRepository debtRepository;

	private String code;

	private Date from;

	public SaveFeedCommand() {
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Feed execute() {
		Feed feed = feedRepository.getByCode(code);
		Validate.notNull(feed, "Cannot find the feed with code " + code);

		Long lastProcessedId = null;
		List<Debt> debts = debtRepository.find(0, 1, "externalId", false, null,
				null, null, from, null, TipoItem.mcd, null);
		if (!debts.isEmpty()) {
			lastProcessedId = debts.get(0).getExternalId();
		}
		feed.update(lastProcessedId);
		return feed;
	}

	public void setFeedRepository(final FeedRepository feedRepository) {
		this.feedRepository = feedRepository;
	}

	public void setDebtRepository(final DebtRepository debtRepository) {
		this.debtRepository = debtRepository;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public void setFrom(final Date from) {
		this.from = from;
	}
}
