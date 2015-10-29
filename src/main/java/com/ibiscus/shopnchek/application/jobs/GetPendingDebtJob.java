package com.ibiscus.shopnchek.application.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.ibiscus.shopnchek.application.Command;

@DisallowConcurrentExecution
public class GetPendingDebtJob extends QuartzJobBean {

	private Command<Boolean> command;

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		try {
			boolean exit = command.execute();
			while (!exit) {
				exit = command.execute();
			}
		} catch (Exception e) {
			throw new JobExecutionException("Cannot import the MCD pending items",
					e);
		}
	}

	public void setCommand(final Command<Boolean> command) {
		this.command = command;
	}
}
