package com.vas.springbatchexample.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.vas.springbatchexample.listeners.JobCompletionNotificationListener;
import com.vas.springbatchexample.models.Log;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final DataSource dataSource;

	public SpringBatchConfig(JobBuilderFactory jobBuilderFactory,
			StepBuilderFactory stepBuilderFactory,
			DataSource dataSource) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
		this.dataSource = dataSource;
	}

	@Bean
	public FlatFileItemReader<Log> reader() {
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setNames("data", "ip", "request", "status", "user_agent");
		tokenizer.setDelimiter("|");
		DefaultLineMapper<Log> defaultLineMapper = new DefaultLineMapper<>();
		defaultLineMapper.setLineTokenizer(tokenizer);
		defaultLineMapper.setFieldSetMapper(new RecordFieldSetMapper());
		FlatFileItemReader<Log> reader = new FlatFileItemReader<>();
		reader.setResource(new ClassPathResource("access.log"));
		reader.setLineMapper(defaultLineMapper);
		return reader;
	}

	@Bean
	public JdbcBatchItemWriter<Log> writer() {
		JdbcBatchItemWriter<Log> writer = new JdbcBatchItemWriter<>();
		writer.setItemSqlParameterSourceProvider(
				new BeanPropertyItemSqlParameterSourceProvider<>());
		writer.setSql(
				"INSERT INTO log (data, ip, request, status, user_agent) VALUES (:data, :ip, :request, :status, :userAgent);");
		writer.setDataSource(this.dataSource);
		return writer;
	}

	@Bean
	public Job importLogJob(JobCompletionNotificationListener listener) {
		return jobBuilderFactory.get("importLogsJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(step1())
				.end()
				.build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.<Log, Log>chunk(100)
				.reader(reader())
				.writer(writer())
				.build();
	}

}
