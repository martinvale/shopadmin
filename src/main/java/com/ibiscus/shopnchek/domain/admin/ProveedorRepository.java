package com.ibiscus.shopnchek.domain.admin;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ProveedorRepository extends HibernateDaoSupport {

  @SuppressWarnings("unchecked")
  public List<Proveedor> find(final String pattern) {
    Validate.notNull(pattern, "The pattern cannot be null");
    Criteria criteria = getSession().createCriteria(Proveedor.class);
    criteria.add(Expression.like("description", pattern + "%"));
    return (List<Proveedor>) criteria.list();
  }

  public Proveedor findByCuit(final String cuit) {
    Validate.notNull(cuit, "The cuit cannot be null");
    Criteria criteria = getSession().createCriteria(Proveedor.class);
    criteria.add(Expression.eq("cuit", cuit));
    return (Proveedor) criteria.uniqueResult();
  }

  public Proveedor get(final long id) {
    return (Proveedor) getSession().get(Proveedor.class, id);
  }
}
