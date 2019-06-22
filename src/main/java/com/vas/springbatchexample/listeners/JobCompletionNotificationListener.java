package com.vas.springbatchexample.listeners;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.vas.springbatchexample.models.Log;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
	private static final Logger logger = LoggerFactory
			.getLogger(JobCompletionNotificationListener.class);
	private final JdbcTemplate jdbcTemplate;

	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			logger.info("!!! JOB FINISHED! Time to verify the results");

			List<Log> results = this.jdbcTemplate.query(
					"SELECT data, ip, request, status, user_agent FROM log",
					(rs, row) -> new Log(rs.getDate(1), rs.getString(2), rs.getString(3),
							rs.getShort(4), rs.getString(5)));

			for (Log log : results) {
				logger.info("Found <" + log.toString() + "> in the database.");
			}
			logger.info("Count <" + results.size() + "> in the database.");
		}
	}
}
