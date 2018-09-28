package com.ibiscus.shopnchek.domain.security;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class UserRepository extends HibernateDaoSupport {

    public User findByUsername(final String username) {
        Criteria criteria = getSession().createCriteria(User.class);
        criteria.add(Expression.eq("username", username));
        return (User) criteria.uniqueResult();
    }

    public User findByRole(final String role) {
        Criteria criteria = getSession().createCriteria(User.class);
        criteria.add(Expression.eq("roles", role));
        return (User) criteria.uniqueResult();
    }

    public Criteria getCriteria(final String name) {
        Criteria criteria = getSession().createCriteria(User.class);
        if (!StringUtils.isBlank(name)) {
            criteria.add(Expression.like("name", "%" + name + "%"));
        }
        return criteria;
    }

    @SuppressWarnings("unchecked")
    public List<User> find(final int start, final int size,
                           final String orderBy, final boolean asc, final String name) {
        Criteria criteria = getCriteria(name);
        if (orderBy != null) {
            if (asc) {
                criteria.addOrder(Order.asc(orderBy));
            } else {
                criteria.addOrder(Order.desc(orderBy));
            }
        }
        criteria.setFirstResult(start);
        criteria.setMaxResults(size);
        return criteria.list();
    }

    public Integer getCount(final String name) {
        Criteria criteria = getCriteria(name);
        return (Integer) criteria.setProjection(Projections.rowCount()).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<User> find() {
        Criteria criteria = getSession().createCriteria(User.class);
        criteria.addOrder(Order.asc("name"));
        return criteria.list();
    }

    public int getUsersCount(final String name) {
        Criteria criteria = getSession().createCriteria(User.class);
        if (name != null && !name.isEmpty()) {
            criteria.add(Expression.like("name", name + "%"));
        }
        return criteria.list().size();
    }

    public Long save(final User user) {
        return (Long) getSession().save(user);
    }

    public void update(final User user) {
        getSession().update(user);
    }

    public void delete(final long id) {
        User user = get(id);
        getSession().delete(user);
    }

    public User get(final long id) {
        return (User) getSession().get(User.class, id);
    }
}
