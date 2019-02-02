package com.ibiscus.shopnchek.domain.admin;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

public class ShopperRepository extends HibernateDaoSupport {

  @SuppressWarnings("unchecked")
  public List<Shopper> find(final String pattern) {
    Criteria criteria = getSession().createCriteria(Shopper.class);
    criteria.add(Expression.eq("country", 1));
    if (isNotEmpty(pattern)) {
      criteria.add(Expression.like("surname", pattern + "%"));
    }
    criteria.addOrder(Order.asc("surname"));
    criteria.addOrder(Order.asc("firstName"));
    return (List<Shopper>) criteria.list();
  }

  @SuppressWarnings("unchecked")
  public Shopper findByDni(final String dni) {
    Validate.notNull(dni, "The shopper dni cannot be null");
    Criteria criteria = getSession().createCriteria(Shopper.class);
    criteria.add(Expression.eq("identityId", dni));
    return (Shopper) criteria.uniqueResult();
  }

  @SuppressWarnings("unchecked")
  public Shopper findByLogin(final String login) {
    Validate.notNull(login, "The login id cannot be null");
    Criteria criteria = getSession().createCriteria(Shopper.class);
    criteria.add(Expression.eq("loginShopmetrics", login));
    return (Shopper) criteria.uniqueResult();
  }

  public Shopper get(final long id) {
    return (Shopper) getSession().get(Shopper.class, id);
  }

    public void save(Shopper shopper) {
      getSession().saveOrUpdate(shopper);
    }
}
