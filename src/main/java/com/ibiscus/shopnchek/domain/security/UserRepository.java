package com.ibiscus.shopnchek.domain.security;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UserRepository extends HibernateDaoSupport {

  public User findByUsername(final String username) {
    Criteria criteria = getSession().createCriteria(User.class);
    criteria.add(Expression.eq("username", username));
    return (User) criteria.uniqueResult();
  }

  @SuppressWarnings("unchecked")
  public List<User> find(final int start, final int size,
      final String name) {
    Criteria criteria = getSession().createCriteria(User.class);
    if (name != null && !name.isEmpty()) {
      criteria.add(Expression.like("name", name + "%"));
    }
    criteria.addOrder(Order.asc("name"));
    criteria.setFirstResult(start - 1);
    criteria.setMaxResults(size);
    return criteria.list();
  }

  public int getUsersCount(final String name) {
    Criteria criteria = getSession().createCriteria(User.class);
    if (name != null && !name.isEmpty()) {
      criteria.add(Expression.like("name", name + "%"));
    }
    return criteria.list().size();
  }

  private User getLastUser() {
    Criteria criteria = getSession().createCriteria(User.class);
    criteria.addOrder(Order.desc("id"));
    criteria.setMaxResults(1);
    return (User) criteria.list().get(0);
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public Long save(final User user) {
    User lastUser = getLastUser();
    user.updateId(lastUser.getId() + 1);
    return (Long) getSession().save(user);
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void update(final User user) {
    getSession().update(user);
  }

  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void delete(final long id) {
    User user = get(id);
    getSession().delete(user);
  }

  public User get(final long id) {
    return (User) getSession().get(User.class, id);
  }
}
