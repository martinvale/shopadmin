package com.ibiscus.shopnchek.domain.debt;

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

	@Column(name = "last_processed_id")
	private Long lastProcessedId;

	@Column(name = "active", nullable = false)
	private boolean active;

	Feed() {
	}

	public void update(final Long lastProcessedId) {
		this.lastProcessedId = lastProcessedId;
	}

	public void update(final Long lastProcessedId, final boolean active) {
		this.lastProcessedId = lastProcessedId;
		this.active = active;
	}

	public long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public Long getLastProcessedId() {
		return lastProcessedId;
	}

	public boolean isActive() {
		return active;
	}
}
