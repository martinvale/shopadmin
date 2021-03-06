package com.ibiscus.shopnchek.domain.debt;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class BranchRepository extends HibernateDaoSupport {

	public Branch get(final long id) {
		return getHibernateTemplate().get(Branch.class, id);
	}

	public Long save(final Branch Branch) {
		return (Long) getHibernateTemplate().save(Branch);
	}

	@SuppressWarnings("unchecked")
	public List<Branch> find(final String name) {
		Criteria criteria = getSession().createCriteria(Branch.class);
		criteria.add(Expression.eq("name", name));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public Branch findByCode(final Client client, final String code) {
		Criteria criteria = getSession().createCriteria(Branch.class);
		criteria.add(Expression.eq("code", code));
		criteria.add(Expression.eq("client", client));
		Branch branch = null;
		List<Branch> branchs = criteria.list();
		if (branchs.size() > 0) {
			branch = branchs.get(0);
		}
		return branch;
	}
}
