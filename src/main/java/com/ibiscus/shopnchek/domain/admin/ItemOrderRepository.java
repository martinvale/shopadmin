package com.ibiscus.shopnchek.domain.admin;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class ItemOrderRepository extends HibernateDaoSupport {

  @SuppressWarnings("unchecked")
  public List<ItemOrden> find(final String dniShopper) {
    Criteria criteria = getSession().createCriteria(ItemOrden.class);
    criteria.add(Expression.eq("shopperDni", dniShopper));
    return (List<ItemOrden>) criteria.list();
  }
}
