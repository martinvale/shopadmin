package com.ibiscus.shopnchek.domain.admin;

import java.util.List;

import org.hibernate.Criteria;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class TipoItemRepository extends HibernateDaoSupport {

	public TipoItem get(final long id) {
		return getHibernateTemplate().get(TipoItem.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<TipoItem> find() {
		Criteria criteria = getSession().createCriteria(TipoItem.class);
		return criteria.list();
	}
}
