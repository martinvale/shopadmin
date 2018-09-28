package com.ibiscus.shopnchek.domain.security;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

public class ActivityRepository extends HibernateDaoSupport {

    @SuppressWarnings("unchecked")
    public List<Activity> findAll() {
        Criteria criteria = getSession().createCriteria(Activity.class);
        criteria.addOrder(Order.asc("creationTime"));
        return criteria.list();
    }

    public long save(Activity activity) {
        return (Long) getHibernateTemplate().save(activity);
    }
}
