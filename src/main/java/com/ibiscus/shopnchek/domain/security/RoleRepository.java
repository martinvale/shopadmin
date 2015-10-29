package com.ibiscus.shopnchek.domain.security;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class RoleRepository extends HibernateDaoSupport {

  @SuppressWarnings("unchecked")
  public List<Role> find() {
    Criteria criteria = getSession().createCriteria(Role.class);
    criteria.addOrder(Order.asc("name"));
    return criteria.list();
  }

  public int getRolesCount() {
    Criteria criteria = getSession().createCriteria(Role.class);
    return criteria.list().size();
  }

  public Role get(final long id) {
    return (Role) getSession().get(Role.class, id);
  }
}
