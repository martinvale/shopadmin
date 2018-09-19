package com.ibiscus.shopnchek.domain.debt;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ClientRepository extends HibernateDaoSupport {

	public Client get(final long id) {
		return getHibernateTemplate().get(Client.class, id);
	}

	public Long save(final Client client) {
		return (Long) getHibernateTemplate().save(client);
	}

	public void update(final Client client) {
		getHibernateTemplate().update(client);
	}

	@SuppressWarnings("unchecked")
	public List<Client> find(final String name) {
		Criteria criteria = getSession().createCriteria(Client.class);
		if (name != null) {
			criteria.add(Expression.like("name", name + "%"));
		}
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public Client getByName(final String name) {
		Client client = null;
		Criteria criteria = getSession().createCriteria(Client.class);
		criteria.add(Expression.eq("name", name));
		List<Client> clients = criteria.list();
		if (!clients.isEmpty()) {
			client = clients.get(0);
		}
		return client;
	}

	public void delete(Client client) {
		getHibernateTemplate().delete(client);
	}

	public int reassign(long clientId, long newClientId) {
		SQLQuery query = getSession().createSQLQuery("update branchs set client_id = :newClientId "
				+ "where client_id = :clientId");
		query.setLong("clientId", clientId);
		query.setLong("newClientId", newClientId);
		query.executeUpdate();

		query = getSession().createSQLQuery("delete from client_principal where client_id = :clientId");
		query.setLong("clientId", clientId);

		query = getSession().createSQLQuery("update deuda set client_id = :newClientId "
				+ "where client_id = :clientId");
		query.setLong("clientId", clientId);
		query.setLong("newClientId", newClientId);
		return query.executeUpdate();
	}
}
