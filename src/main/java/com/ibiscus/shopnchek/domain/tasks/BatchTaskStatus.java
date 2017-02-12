package com.ibiscus.shopnchek.domain.tasks;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.Validate;

@Entity
@Table(name="batch_task_status")
public class BatchTaskStatus {

	public enum STATUS {
		WAITING, RUNNNIG, ERROR, FINISHED
	}

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name", nullable = false, length = 200)
	private String name;

	@Column(name = "status", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private STATUS status;

	@Column(name = "error_description", length = 300)
	private String errorDescription;

	@Column(name = "additional_info")
	private String additionalInfo;

	@Column(name = "porcentage", nullable = false)
	private int porcentage = 0;

	@Column(name = "creation_date", nullable = false)
	private Date creationDate;

	@Column(name = "modification_date", nullable = false)
	private Date modificationDate;

	BatchTaskStatus() {
	}

	public BatchTaskStatus(final String name) {
		this.name = name;
		this.status = STATUS.WAITING;
		this.creationDate = new Date();
		this.modificationDate = new Date(creationDate.getTime());
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public STATUS getStatus() {
		return status;
	}

	public void start() {
		this.status = STATUS.RUNNNIG;
		this.modificationDate = new Date();
	}

	public void finish() {
		this.status = STATUS.FINISHED;
		this.porcentage = 100;
		this.modificationDate = new Date();
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void error(final String errorDescription) {
		this.errorDescription = errorDescription;
		this.status = STATUS.ERROR;
		this.modificationDate = new Date();
	}

	public int getPorcentage() {
		return porcentage;
	}

	public void setProcentage(final int porcentage) {
		Validate.isTrue(porcentage >= 0 && porcentage <= 100, "The porcentage must be "
				+ " between 0 and 100");
		this.porcentage = porcentage;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public Date getCreationDate() {
		return new Date(creationDate.getTime());
	}

	public Date getModificationDate() {
		return new Date(modificationDate.getTime());
	}
}
