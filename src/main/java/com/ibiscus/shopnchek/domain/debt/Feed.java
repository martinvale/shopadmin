package com.ibiscus.shopnchek.domain.debt;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "feed")
public class Feed {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "code", nullable = false, length = 20)
	private String code;

	@Column(name = "last_processed_date")
	private Date lastProcessedDate;

	Feed() {
	}

	public void update(final Date lastProcessedDate) {
		this.lastProcessedDate = lastProcessedDate;
	}

	public long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public Date getLastExecutionTime() {
		return lastProcessedDate;
	}
}
