package com.ibiscus.shopnchek.domain.tasks;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class BatchTaskStatusRepository extends HibernateDaoSupport {

	private static Logger logger = LoggerFactory.getLogger(BatchTaskStatusRepository.class);

	public BatchTaskStatus get(final long id) {
		return getHibernateTemplate().get(BatchTaskStatus.class, id);
	}

	public BatchTaskStatus getByName(final String name) {
		logger.debug("Searching batch task status with name: {}", name);
		Criteria criteria = getSession().createCriteria(BatchTaskStatus.class);
		criteria.add(Expression.eq("name", name));
		return (BatchTaskStatus) criteria.uniqueResult();
	}

	public Long save(final BatchTaskStatus batchTaskStatus) {
		return (Long) getHibernateTemplate().save(batchTaskStatus);
	}

	public void update(final BatchTaskStatus BatchTaskStatus) {
		getHibernateTemplate().update(BatchTaskStatus);
	}

	@SuppressWarnings("unchecked")
	public List<BatchTaskStatus> find() {
		Criteria criteria = getSession().createCriteria(BatchTaskStatus.class);
		criteria.addOrder(Order.desc("creationDate"));
		criteria.setMaxResults(15);
		return criteria.list();
	}
}
