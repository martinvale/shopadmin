package com.ibiscus.shopnchek.domain.admin;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ShopperRepository extends HibernateDaoSupport {

  @SuppressWarnings("unchecked")
  public List<Shopper> find(final String pattern) {
    Criteria criteria = getSession().createCriteria(Shopper.class);
    criteria.add(Expression.eq("pais", 1));
    if (pattern != null && !pattern.isEmpty()) {
      criteria.add(Expression.like("name", pattern + "%"));
    }
    criteria.addOrder(Order.asc("name"));
    return (List<Shopper>) criteria.list();
  }

  @SuppressWarnings("unchecked")
  public Shopper findByDni(final String dni) {
    Validate.notNull(dni, "The shopper dni cannot be null");
    Criteria criteria = getSession().createCriteria(Shopper.class);
    criteria.add(Expression.eq("dni", dni));
    Shopper shopper = null;
    //TODO Volver a usar unique result cuando arreglen la base de shoppers
    List<Shopper> shoppers = criteria.list();
    if (shoppers.size() > 0) {
    	shopper = shoppers.get(0);
    }
    return shopper;
  }

  public Shopper get(final long id) {
    return (Shopper) getSession().get(Shopper.class, id);
  }
}
