package com.ibiscus.shopnchek.domain.admin;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class OrderRepository extends HibernateDaoSupport {

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public OrdenPago save(final OrdenPago ordenPago) {
    getSession().save(ordenPago);
    getSession().flush();
    return ordenPago;
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
}
