package com.ibiscus.shopnchek.domain.debt;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class DebtRepository extends HibernateDaoSupport {

	public Debt get(final long id) {
		return getHibernateTemplate().get(Debt.class, id);
	}

	public Long save(final Debt debt) {
		return (Long) getHibernateTemplate().save(debt);
	}

	public void update(final Debt debt) {
		getHibernateTemplate().update(debt);
	}

	public Criteria getCriteria(final String shopperDni, final Date from,
			final Date to) {
		Criteria criteria = getSession().createCriteria(Debt.class);
		if (!StringUtils.isBlank(shopperDni)) {
			criteria.add(Expression.eq("shopperDni", shopperDni));
		}
		if (from != null) {
			criteria.add(Expression.ge("fecha", from));
		}
		if (to != null) {
			criteria.add(Expression.le("fecha", to));
		}
		return criteria;
	}

	@SuppressWarnings("unchecked")
	public List<Debt> find(final int start, final int count,
			final String orderBy, final boolean asc,
			final String shopperDni, final Date from,
			final Date to) {
		Criteria criteria = getCriteria(shopperDni, from, to)
				.setFirstResult(start).setMaxResults(count);
		if (asc) {
			criteria.addOrder(Order.asc(orderBy));
		} else {
			criteria.addOrder(Order.desc(orderBy));
		}
		return criteria.list();
	}

	public Integer getCount(final String shopperDni, final Date from,
			final Date to) {
		Criteria criteria = getCriteria(shopperDni, from, to);
		return (Integer) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
}
