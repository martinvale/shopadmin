package com.ibiscus.shopnchek.domain.admin;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class ProgramRepository extends HibernateDaoSupport {

  public Programa get(final long id) {
    return (Programa) getSession().get(Programa.class, id);
  }

  @SuppressWarnings("unchecked")
  public List<Programa> find() {
    Criteria criteria = getSession().createCriteria(Programa.class);
    criteria.addOrder(Order.asc("nombre"));
    return criteria.list();
  }

}
