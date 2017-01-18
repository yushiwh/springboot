package com.jztey.demo.service.banth;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.jztey.demo.entity.BanthPerson;

/**
 * 手动触发的类
 * 
 * @author yushi
 *
 */
@Configuration // 加上配置
@EnableBatchProcessing
public class TriggerBatchConfig {

	@Bean
	@StepScope
	public FlatFileItemReader<BanthPerson> reader(@Value("#{jobParameters['input.file.name']}") String pathToFile)
			throws Exception {// 注意此处的需要用FlatFileItemReader不能用ItemReader，因为ItemReader没有read方法
		FlatFileItemReader<BanthPerson> reader = new FlatFileItemReader<BanthPerson>(); // 1
		reader.setResource(new ClassPathResource(pathToFile)); // 2
		reader.setLineMapper(new DefaultLineMapper<BanthPerson>() {
			{ // 3
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames(new String[] { "name", "age", "nation", "address" });
					}
				});
				setFieldSetMapper(new BeanWrapperFieldSetMapper<BanthPerson>() {
					{
						setTargetType(BanthPerson.class);
					}
				});
			}
		});

		return reader;
	}

	@Bean
	public ItemProcessor<BanthPerson, BanthPerson> processor() {
		CsvItemProcessor processor = new CsvItemProcessor(); // 1
		processor.setValidator(csvBeanValidator()); // 2
		return processor;
	}

	@Bean
	public ItemWriter<BanthPerson> writer(DataSource dataSource) {// 1
		JdbcBatchItemWriter<BanthPerson> writer = new JdbcBatchItemWriter<BanthPerson>(); // 2
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<BanthPerson>());
		String sql = "insert into BanthPerson " + "( name,age,nation,address) "
				+ "values(  :name, :age, :nation,:address)";
		writer.setSql(sql); // 3
		writer.setDataSource(dataSource);
		return writer;
	}

	@Bean
	public JobRepository jobRepository(DataSource dataSource, PlatformTransactionManager transactionManager)
			throws Exception {
		JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
		jobRepositoryFactoryBean.setDataSource(dataSource);
		jobRepositoryFactoryBean.setTransactionManager(transactionManager);
		jobRepositoryFactoryBean.setDatabaseType("mysql");
		return jobRepositoryFactoryBean.getObject();
	}

	@Bean
	public SimpleJobLauncher jobLauncher(DataSource dataSource, PlatformTransactionManager transactionManager)
			throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository(dataSource, transactionManager));
		return jobLauncher;
	}

	@Bean
	public Job importJob(JobBuilderFactory jobs, Step s1) {
		return jobs.get("importJob").incrementer(new RunIdIncrementer()).flow(s1) // 1
				.end().listener(csvJobListener()) // 2
				.build();
	}

	@Bean
	public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<BanthPerson> reader,
			ItemWriter<BanthPerson> writer, ItemProcessor<BanthPerson, BanthPerson> processor) {
		return stepBuilderFactory.get("step1").<BanthPerson, BanthPerson> chunk(65000) // 1
				.reader(reader) // 2
				.processor(processor) // 3
				.writer(writer) // 4
				.build();
	}

	@Bean
	public CsvJobListener csvJobListener() {
		return new CsvJobListener();
	}

	@Bean
	public Validator<BanthPerson> csvBeanValidator() {
		return new CsvBeanValidator<BanthPerson>();
	}

}
