package com.ibiscus.shopnchek.domain.admin;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.domain.security.User;

@Transactional(readOnly = true)
public class ItemOrderRepository extends HibernateDaoSupport {

  @SuppressWarnings("unchecked")
  public List<ItemOrden> find(final String dniShopper) {
    Criteria criteria = getSession().createCriteria(ItemOrden.class);
    criteria.add(Expression.eq("shopperDni", dniShopper));
    return (List<ItemOrden>) criteria.list();
  }

  public ClienteShopmetrics getCliente(final int id) {
    return (ClienteShopmetrics) getSession().get(ClienteShopmetrics.class, id);
  }

  @SuppressWarnings("unchecked")
  public List<ClienteShopmetrics> findClientes() {
    Criteria criteria = getSession().createCriteria(ClienteShopmetrics.class);
    criteria.addOrder(Order.asc("nombre"));
    return criteria.list();
  }

  @SuppressWarnings("unchecked")
  public List<TipoPago> findTiposDePago() {
    Criteria criteria = getSession().createCriteria(TipoPago.class);
    criteria.addOrder(Order.asc("description"));
    return criteria.list();
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public Object[] getAdicionalNumero() {
    Query query = getSession().createSQLQuery("SELECT item_adicional_nro, grupo_adicional_nro FROM parametros");
    Object[] result = (Object[]) query.uniqueResult();

    query = getSession().createSQLQuery("UPDATE parametros SET item_adicional_nro = item_adicional_nro + 1, "
        + "grupo_adicional_nro = grupo_adicional_nro + 1");
    query.executeUpdate();

    return result;
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public int saveAdicionalAutorizado(final AutorizacionAdicional unAdicional) {
    Object[] ids = getAdicionalNumero();
    unAdicional.updateId(((Integer) ids[0]) + 1);
    if (unAdicional.getGroup() == null) {
      unAdicional.updateGroup(((Integer) ids[1]) + 1);
    }
    getSession().save(unAdicional);
    return unAdicional.getGroup();
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public int updateAdicionalAutorizado(final AutorizacionAdicional unAdicional) {
    getSession().update(unAdicional);
    return unAdicional.getGroup();
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void deleteAdicionalAutorizado(final int id) {
    AutorizacionAdicional adicional = getAdicionalAutorizado(id);
    getSession().delete(adicional);
  }

  public AutorizacionAdicional getAdicionalAutorizado(final int id) {
    return (AutorizacionAdicional) getSession().get(AutorizacionAdicional.class, id);
  }

  @SuppressWarnings("unchecked")
  public List<AutorizacionAdicional> findAdicionalesByGroup(final int group) {
    Criteria criteria = getSession().createCriteria(AutorizacionAdicional.class);
    criteria.add(Expression.eq("group", group));
    return criteria.list();
  }

  @SuppressWarnings("unchecked")
  public List<AutorizacionAdicional> findAdicionales(final int start,
      final int size, final String shopperDni, final Integer mes,
      final Integer anio, final User user) {
    Criteria criteria = getSession().createCriteria(AutorizacionAdicional.class);
    if (shopperDni != null && !shopperDni.isEmpty()) {
      criteria.add(Expression.eq("shopperDni", shopperDni));
    }
    if (mes != null) {
      criteria.add(Expression.eq("mes", mes));
    }
    if (anio != null) {
      criteria.add(Expression.eq("anio", anio));
    }
    if (user != null) {
      criteria.add(Expression.eq("username", user.getUsername()));
    }
    criteria.setFirstResult(start - 1);
    if (size > -1) {
      criteria.setMaxResults(size);
    }
    return criteria.list();
  }

  public int findAdicionalesCount(final String shopperDni, final Integer mes,
      final Integer anio, final User user) {
    return findAdicionales(1, -1, shopperDni, mes, anio, user).size();
  }
}
