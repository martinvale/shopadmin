package com.ibiscus.shopnchek.domain.admin;

import java.util.List;

import org.hibernate.Criteria;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class TipoPagoRepository extends HibernateDaoSupport {

	public TipoPago get(final long id) {
		return getHibernateTemplate().get(TipoPago.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<TipoPago> find() {
		Criteria criteria = getSession().createCriteria(TipoPago.class);
		return criteria.list();
	}
}
