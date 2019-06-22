package com.vas.springbatchexample;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringBatchExampleApplication {

	public static void main(String[] args)
			throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		args = new String[] { "--file", "teste" };
		if (args.length % 2 == 0) {
			Map<String, String> params = new HashMap<>();
			for (int i = 0; i < args.length; i += 2) {
				String key = args[i];
				String value = args[i + 1];
				params.put(key.replace("-", ""), value);
			}
			Properties property = new Properties();
			for (Map.Entry<String, String> set : params.entrySet()) {
				property.put(set.getKey(), set.getValue());
			}
			ConfigurableApplicationContext context = SpringApplication
					.run(SpringBatchExampleApplication.class, args);
			JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
			JobParameters jobParameters = new DefaultJobParametersConverter()
					.getJobParameters(property);
			Job job = (Job) context.getBean("importLogsJob");
			jobLauncher.run(job, jobParameters);
		} else {
			throw new RuntimeException(
					"Parameters should be --key value");
		}
	}

}
