package com.ibiscus.shopnchek.domain.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.junit.Before;
import org.junit.Test;

import com.ibiscus.shopnchek.domain.tasks.BatchTaskStatus.STATUS;

public class BatchTaskStatusRepositoryTest {

	private SessionFactory sessionFactory;

	@Before
	public void setUp() {
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		configuration.addAnnotatedClass(BatchTaskStatus.class);
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
		configuration.setProperty("hibernate.connection.url", "jdbc:h2:./mem");
		configuration.setProperty("hibernate.hbm2ddl.auto", "create");
		sessionFactory = configuration.buildSessionFactory();
	}

	@Test
	public void basicOperations() {
		BatchTaskStatusRepository repository = new BatchTaskStatusRepository();
		repository.setSessionFactory(sessionFactory);

		BatchTaskStatus batchTaskStatus = new BatchTaskStatus("test_task");

		Long batchTaskStatusId = repository.save(batchTaskStatus);
		assertNotNull(batchTaskStatusId);

		BatchTaskStatus batchTaskStatusDb = repository.get(batchTaskStatusId);
		assertNotNull(batchTaskStatusDb);
		assertEquals("test_task", batchTaskStatusDb.getName());
		assertEquals(STATUS.WAITING, batchTaskStatusDb.getStatus());

		batchTaskStatusDb.start();
		repository.update(batchTaskStatusDb);

		batchTaskStatusDb = repository.get(batchTaskStatusId);
		assertNotNull(batchTaskStatusDb);
		assertEquals("test_task", batchTaskStatusDb.getName());
		assertEquals(STATUS.RUNNNIG, batchTaskStatusDb.getStatus());
	}

}
