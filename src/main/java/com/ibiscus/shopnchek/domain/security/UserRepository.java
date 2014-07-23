package com.ibiscus.shopnchek.domain.security;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class UserRepository extends HibernateDaoSupport {

  public User findByUsername(final String username) {
    Criteria criteria = getSession().createCriteria(User.class);
    criteria.add(Expression.eq("username", username));
    return (User) criteria.uniqueResult();
  }

  public Long save(final User user) {
    return (Long) getSession().save(user);
  }

}
