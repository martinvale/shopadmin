package com.ibiscus.shopnchek.domain.debt;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ClientRepository extends HibernateDaoSupport {

	public Client get(final long id) {
		return getHibernateTemplate().get(Client.class, id);
	}

	public Client save(final Client client) {
		return (Client) getHibernateTemplate().save(client);
	}

	@SuppressWarnings("unchecked")
	public List<Client> find(final String name) {
		Criteria criteria = getSession().createCriteria(Client.class);
		criteria.add(Expression.like("name", name + "%"));
		return criteria.list();
	}
}
