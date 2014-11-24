package com.ibiscus.shopnchek.domain.admin;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class SucursalMCDRepository extends HibernateDaoSupport {

  public SucursalMCD get(final String id) {
    return (SucursalMCD) getSession().get(SucursalMCD.class, id);
  }

  @SuppressWarnings("unchecked")
  public List<SucursalMCD> find() {
    Criteria criteria = getSession().createCriteria(SucursalMCD.class);
    criteria.addOrder(Order.asc("city"));
    return criteria.list();
  }

}
