package com.ibiscus.shopnchek.domain.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
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
      final OrderState estado, final Date fechaPagoDesde, final Date fechaPagoHasta) {
    Criteria criteria = getCriteria(tipoTitular, titularId, dniShopper,
        numeroCheque, estado, fechaPagoDesde, fechaPagoHasta);
    if (StringUtils.isBlank(dniShopper)) {
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
    } else {
	    if (start != null) {
	      criteria.setFirstResult(start);
	    }
	    if (count != null) {
	      criteria.setMaxResults(count);
	    }
	    ProjectionList projectionList = Projections.projectionList();
	    projectionList.add(Projections.groupProperty("numero"));
	    if (orderBy != null) {
	      projectionList.add(Projections.groupProperty(orderBy));
	      if (asc) {
	        criteria.addOrder(Order.asc(orderBy));
	      } else {
	        criteria.addOrder(Order.desc(orderBy));
	      }
	    }
    	criteria.setProjection(projectionList);
    	List<Object[]> ids = criteria.list();
    	List<OrdenPago> orders = new ArrayList<OrdenPago>();
    	for (Object[] orderId : ids) {
    		orders.add(get((Long) orderId[0])); 
    	}
    	return orders;
    }
  }

  public Integer getCount(final Integer tipoTitular, final Integer titularId,
      final String dniShopper, final String numeroCheque, final OrderState estado,
      final Date fechaPagoDesde, final Date fechaPagoHasta) {
    Criteria criteria = getCriteria(tipoTitular, titularId, dniShopper,
            numeroCheque, estado, fechaPagoDesde, fechaPagoHasta);
    return (Integer) criteria.setProjection(Projections.countDistinct("id")).uniqueResult();
  }

  private Criteria getCriteria(final Integer tipoTitular, final Integer titularId,
      final String dniShopper, final String numeroCheque, final OrderState estado,
      final Date fechaPagoDesde, final Date fechaPagoHasta) {
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
    if (fechaPagoDesde != null) {
      criteria.add(Expression.ge("fechaPago", fechaPagoDesde));
    }
    if (fechaPagoHasta != null) {
      criteria.add(Expression.le("fechaPago", fechaPagoHasta));
    }
    criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    return criteria;
  }

  public OrdenPago getMajorBillNumber() {
      Query query = getSession().createSQLQuery("select top 1 * from ordenes "
              + "where not factura_nro is null and isnumeric(factura_nro) = 1 "
              + "order by convert(int, factura_nro) desc")
              .addEntity(OrdenPago.class);
      return (OrdenPago) query.uniqueResult();
  }
}
