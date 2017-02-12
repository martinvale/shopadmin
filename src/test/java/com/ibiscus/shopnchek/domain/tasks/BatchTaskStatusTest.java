package com.ibiscus.shopnchek.domain.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import com.ibiscus.shopnchek.domain.tasks.BatchTaskStatus.STATUS;

public class BatchTaskStatusTest {

	@Test
	public void constructor() {
		BatchTaskStatus batchTaskStatus = new BatchTaskStatus("test_name");

		assertEquals("test_name", batchTaskStatus.getName());
		assertEquals(STATUS.WAITING, batchTaskStatus.getStatus());
		assertNotNull(batchTaskStatus.getCreationDate());
		assertNotNull(batchTaskStatus.getModificationDate());
	}

	@Test
	public void update() throws InterruptedException {
		BatchTaskStatus batchTaskStatus = new BatchTaskStatus("test_name");
		Date creationDate = batchTaskStatus.getCreationDate();
		Date modificationDate = batchTaskStatus.getModificationDate();

		Thread.sleep(100);
		batchTaskStatus.start();

		assertEquals("test_name", batchTaskStatus.getName());
		assertEquals(STATUS.RUNNNIG, batchTaskStatus.getStatus());
		assertEquals(creationDate, batchTaskStatus.getCreationDate());
		assertTrue(batchTaskStatus.getModificationDate().after(modificationDate));
	}
}
