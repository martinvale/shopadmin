package com.ibiscus.shopnchek.domain.debt;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class FeedRepository extends HibernateDaoSupport {

	public void update(final Feed feed) {
		getHibernateTemplate().update(feed);
	}

	public Feed getByCode(final String code) {
		Criteria criteria = getSession().createCriteria(Feed.class);
		criteria.add(Expression.eq("code", code));
		return (Feed) criteria.uniqueResult();
	}
}
