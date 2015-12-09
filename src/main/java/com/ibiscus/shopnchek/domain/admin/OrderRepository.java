package com.ibiscus.shopnchek.domain.admin;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class OrderRepository extends HibernateDaoSupport {

  public Long save(final OrdenPago order) {
    return (Long) getHibernateTemplate().save(order);
  }

  public void updateItem(final ItemOrden itemOrden) {
    getSession().update(itemOrden);
  }

  public long saveItem(final ItemOrden itemOrden) {
    long numeroItem = (Long) getSession().save(itemOrden);
    return numeroItem;
  }

  public OrdenPago get(final long id) {
    return (OrdenPago) getSession().get(OrdenPago.class, id);
  }

  public TipoPago getTipoPago(final long id) {
    return (TipoPago) getSession().get(TipoPago.class, id);
  }

  public OrderState getOrderState(final long id) {
    return (OrderState) getSession().get(OrderState.class, id);
  }

  @SuppressWarnings("unchecked")
  public List<OrderState> findOrderStates() {
    Criteria criteria = getSession().createCriteria(OrderState.class);
    criteria.addOrder(Order.asc("description"));
    return (List<OrderState>) criteria.list();
  }

  public MedioPago getMedioPago(final long id) {
    return (MedioPago) getSession().get(MedioPago.class, id);
  }

  @SuppressWarnings("unchecked")
  public List<MedioPago> findMediosPago() {
    Criteria criteria = getSession().createCriteria(MedioPago.class);
    criteria.addOrder(Order.asc("description"));
    return (List<MedioPago>) criteria.list();
  }

  public boolean removeItem(final ItemOrden itemOrden) {
    getSession().delete(itemOrden);
    return true;
  }

  public ItemOrden getItem(final long id) {
    return (ItemOrden) getSession().get(ItemOrden.class, id);
  }

  public AsociacionMedioPago findAsociacion(final int titularTipo,
      final long titularNro) {
    Criteria criteria = getSession().createCriteria(AsociacionMedioPago.class);
    criteria.add(Expression.eq("asociacionMedioPagoPk.titularTipo", titularTipo));
    criteria.add(Expression.eq("asociacionMedioPagoPk.titularNro", titularNro));
    return (AsociacionMedioPago) criteria.uniqueResult();
  }

  public void save(final AsociacionMedioPago asociacionMedioPago) {
    getSession().save(asociacionMedioPago);
  }

  public void update(final AsociacionMedioPago asociacionMedioPago) {
    getSession().merge(asociacionMedioPago);
  }

  @SuppressWarnings("unchecked")
  public List<OrdenPago> find(final Integer start, final Integer count,
      final String orderBy, final boolean asc, final Integer tipoTitular,
      final Integer titularId, final String dniShopper, final String numeroCheque,
      final OrderState estado) {
    Criteria criteria = getCriteria(tipoTitular, titularId, dniShopper,
        numeroCheque, estado);
    if (start != null) {
      criteria.setFirstResult(start);
    }
    if (count != null) {
      criteria.setMaxResults(count);
    }
    if (orderBy != null) {
      if (asc) {
        criteria.addOrder(Order.asc(orderBy));
      } else {
        criteria.addOrder(Order.desc(orderBy));
      }
    }
    return criteria.list();
  }

  public Integer getCount(final Integer tipoTitular, final Integer titularId,
      final String dniShopper, final String numeroCheque, final OrderState estado) {
    Criteria criteria = getCriteria(tipoTitular, titularId, dniShopper,
            numeroCheque, estado);
    return (Integer) criteria.setProjection(Projections.rowCount()).uniqueResult();
  }

  private Criteria getCriteria(final Integer tipoTitular, final Integer titularId,
      final String dniShopper, final String numeroCheque, final OrderState estado) {
    Criteria criteria = getSession().createCriteria(OrdenPago.class);
    if (tipoTitular != null && titularId != null) {
      criteria.add(Expression.eq("tipoProveedor", tipoTitular));
      criteria.add(Expression.eq("proveedor", titularId));
    }
    if (dniShopper != null && !dniShopper.isEmpty()) {
      criteria.createCriteria("items").add(Expression.eq("shopperDni", dniShopper));
    }
    if (!StringUtils.isBlank(numeroCheque)) {
      criteria.add(Expression.eq("numeroCheque", numeroCheque));
    }
    if (estado != null) {
      criteria.add(Expression.eq("estado", estado));
    }
    criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    return criteria;
  }

}
