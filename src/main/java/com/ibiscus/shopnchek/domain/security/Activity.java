package com.ibiscus.shopnchek.domain.security;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "activity")
public class Activity {

	public enum Code {
		ORDER_CREATION("Orden creada por: "),
		ORDER_EDITION("Orden editada por: "),
		ORDER_VERIFIED("Orden verificada por: "),
		ORDER_PAYED("Orden pagada por: "),
		ORDER_CLOSED("Orden cerrada por: "),
		ORDER_PAUSED("Orden pausada por: "),
		ORDER_CANCELLED("Orden cancelada por: "),
		ORDER_REOPENED("Orden reabierta por: ");

		private final String detail;

		Code(String detail) {
			this.detail = detail;
		}

		public String getDetail() {
			return detail;
		}
	}

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "owner_id")
	private long ownerId;

	@ManyToOne
	@JoinColumn(name = "author_id", nullable = false)
	private User author;

	@Column(name = "code", nullable = false, length = 30)
	@Enumerated(EnumType.STRING)
	private Code code;

	@Column(name = "detail", length = 300)
	private String detail;

	@Column(name = "creation_time", nullable = false)
	private Date creationTime;

	Activity() {
	}

	public Activity(long ownerId, User author, Code code) {
		this.ownerId = ownerId;
		this.author = author;
		this.code = code;
		this.detail = code.getDetail() + author.getName();
		this.creationTime = new Date();
	}

	public long getId() {
		return id;
	}

	public User getAuthor() {
		return author;
	}

	public Code getCode() {
		return code;
	}

	public String getDetail() {
		return detail;
	}

	public Date getCreationTime() {
		return creationTime;
	}
}
