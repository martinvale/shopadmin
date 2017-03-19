package com.ibiscus.shopnchek.domain.admin;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class ProveedorRepository extends HibernateDaoSupport {

  @SuppressWarnings("unchecked")
  public List<Proveedor> find(final int start, final int size,
      final String pattern) {
    Criteria criteria = getSession().createCriteria(Proveedor.class);
    if (pattern != null && !pattern.isEmpty()) {
      criteria.add(Expression.like("description", "%" + pattern + "%"));
    }
    criteria.addOrder(Order.asc("description"));
    criteria.setFirstResult(start);
    criteria.setMaxResults(size);
    return criteria.list();
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

  public int getProveedoresCount(final String name) {
    Criteria criteria = getSession().createCriteria(Proveedor.class);
    if (name != null && !name.isEmpty()) {
      criteria.add(Expression.like("description", name + "%"));
    }
    return criteria.list().size();
  }

  private Proveedor getLastProveedor() {
    Criteria criteria = getSession().createCriteria(Proveedor.class);
    criteria.addOrder(Order.desc("id"));
    criteria.setMaxResults(1);
    return (Proveedor) criteria.list().get(0);
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public Long save(final Proveedor proveedor) {
    Proveedor lastProveedor = getLastProveedor();
    proveedor.updateId(lastProveedor.getId() + 1);
    return (Long) getSession().save(proveedor);
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void update(final Proveedor proveedor) {
    getSession().update(proveedor);
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void delete(final long id) {
    Proveedor proveedor = get(id);
    getSession().delete(proveedor);
  }

}
