package com.ibiscus.shopnchek.domain.admin;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ShopperRepository extends HibernateDaoSupport {

  @SuppressWarnings("unchecked")
  public List<Shopper> find(final String pattern) {
    Validate.notNull(pattern, "The pattern cannot be null");
    Criteria criteria = getSession().createCriteria(Shopper.class);
    criteria.add(Expression.like("name", pattern + "%"));
    return (List<Shopper>) criteria.list();
  }

  public Shopper findByDni(final String dni) {
    Validate.notNull(dni, "The shopper dni cannot be null");
    Criteria criteria = getSession().createCriteria(Shopper.class);
    criteria.add(Expression.eq("dni", dni));
    return (Shopper) criteria.uniqueResult();
  }

  public Shopper get(final long id) {
    return (Shopper) getSession().get(Shopper.class, id);
  }
}
