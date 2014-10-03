package com.ibiscus.shopnchek.domain.admin;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class SucursalShopmetricsRepository extends HibernateDaoSupport {

  public SucursalShopmetrics get(final String id) {
    return (SucursalShopmetrics) getSession().get(SucursalShopmetrics.class, id);
  }

  @SuppressWarnings("unchecked")
  public List<SucursalShopmetrics> find() {
    Criteria criteria = getSession().createCriteria(SucursalShopmetrics.class);
    criteria.addOrder(Order.asc("location"));
    return criteria.list();
  }

}
