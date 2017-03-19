package com.ibiscus.shopnchek.domain.account;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class AccountRepository extends HibernateDaoSupport {

  @SuppressWarnings("unchecked")
  public List<Account> find(final int start, final int size,
      final String pattern) {
    Criteria criteria = getSession().createCriteria(Account.class);
    if (pattern != null && !pattern.isEmpty()) {
      criteria.add(Expression.like("description", "%" + pattern + "%"));
    }
    criteria.addOrder(Order.asc("description"));
    criteria.setFirstResult(start - 1);
    criteria.setMaxResults(size);
    return criteria.list();
  }

  public Account findByCuit(final String cuit) {
    Validate.notNull(cuit, "The cuit cannot be null");
    Criteria criteria = getSession().createCriteria(Account.class);
    criteria.add(Expression.eq("cuit", cuit));
    return (Account) criteria.uniqueResult();
  }

  public Account findByTitular(final int titularTipo, final long titularId) {
    Validate.isTrue(titularTipo == 1 || titularTipo == 2, "The titular type is unknown");
    Criteria criteria = getSession().createCriteria(Account.class);
    criteria.add(Expression.eq("titularTipo", titularTipo));
    criteria.add(Expression.eq("titularId", titularId));
    return (Account) criteria.uniqueResult();
  }

  public Account get(final long id) {
    return (Account) getSession().get(Account.class, id);
  }

  public int getAccountCount(final String name) {
    Criteria criteria = getSession().createCriteria(Account.class);
    if (name != null && !name.isEmpty()) {
      criteria.add(Expression.like("description", name + "%"));
    }
    return criteria.list().size();
  }

  public Long save(final Account account) {
    return (Long) getSession().save(account);
  }

  public void update(final Account account) {
    getSession().update(account);
  }

  public void delete(final long id) {
    Account account = get(id);
    getSession().delete(account);
  }

}
