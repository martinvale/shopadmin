package com.ibiscus.shopnchek.application.feed;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.debt.Feed;
import com.ibiscus.shopnchek.domain.debt.FeedRepository;

public class GetFeedCommand implements Command<Feed> {

	private FeedRepository feedRepository;

	private String code;

	public GetFeedCommand() {
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public Feed execute() {
		Feed feed = feedRepository.getByCode(code);
		return feed;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public void setFeedRepository(final FeedRepository feedRepository) {
		this.feedRepository = feedRepository;
	}
}
