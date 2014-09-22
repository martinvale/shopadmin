package com.ibiscus.shopnchek.domain.admin;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class OrderRepository extends HibernateDaoSupport {

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public long save(final OrdenPago ordenPago) {
    long numeroOrden = (Long) getSession().save(ordenPago);
    getSession().flush();
    return numeroOrden;
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public long saveItem(final ItemOrden itemOrden) {
    long numeroItem = (Long) getSession().save(itemOrden);
    return numeroItem;
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void update(final OrdenPago ordenPago) {
    getSession().update(ordenPago);
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

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public boolean removeItem(final ItemOrden itemOrden) {
    getSession().delete(itemOrden);
    return true;
  }

  public AsociacionMedioPago findAsociacion(final int titularTipo,
      final long titularNro) {
    Criteria criteria = getSession().createCriteria(AsociacionMedioPago.class);
    criteria.add(Expression.eq("asociacionMedioPagoPk.titularTipo", titularTipo));
    criteria.add(Expression.eq("asociacionMedioPagoPk.titularNro", titularNro));
    return (AsociacionMedioPago) criteria.uniqueResult();
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void save(final AsociacionMedioPago asociacionMedioPago) {
    getSession().save(asociacionMedioPago);
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void update(final AsociacionMedioPago asociacionMedioPago) {
    getSession().merge(asociacionMedioPago);
  }

  @SuppressWarnings("unchecked")
  public List<OrdenPago> findOrdenes(Integer tipoTitular, Integer titularId,
      String dniShopper, String numeroCheque, OrderState estado) {
    Criteria criteria = getSession().createCriteria(OrdenPago.class);
    if (tipoTitular != null && titularId != null) {
      criteria.add(Expression.eq("proveedor", titularId));
    }
    if (dniShopper != null && !dniShopper.isEmpty()) {
      criteria.createCriteria("items")
          .add(Expression.eq("shopperDni", dniShopper));
    }
    if (numeroCheque != null && !numeroCheque.isEmpty()) {
      criteria.add(Expression.eq("numeroCheque", numeroCheque));
    }
    if (estado != null) {
      criteria.add(Expression.eq("estado", estado));
    }
    criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    return (List<OrdenPago>) criteria.list();
  }
}
