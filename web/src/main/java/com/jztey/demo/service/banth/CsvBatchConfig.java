package com.jztey.demo.service.banth;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.jztey.demo.entity.BanthPerson;

/**
 * 配置
 * 
 * @author yushi 2017-01-18 
 */
// @Configuration//自动执行Job的配置，如果是手动启动则需要进行注释掉，如果要自动执行导入的语句需要进行打开处理，并且在配置文件中修改成enable
@EnableBatchProcessing // 开启批处理的支持
public class CsvBatchConfig {

	@Bean
	public ItemReader<BanthPerson> reader() throws Exception {
		FlatFileItemReader<BanthPerson> reader = new FlatFileItemReader<BanthPerson>(); // 1使用FlatFileItemReader读取文件
		reader.setResource(new ClassPathResource("people.csv")); // 2使用FlatFileItemReader的setResource的方法设置csv的文件路径
		reader.setLineMapper(new DefaultLineMapper<BanthPerson>() {// 3
																	// 在此处对csv文件的数据和模型类做对应映射
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames(new String[] { "name", "age", "nation", "address" });// 读取相应的字段名
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
		CsvItemProcessor processor = new CsvItemProcessor(); // 1使用我们自定义的ItemProcessor的实现CsvItemProcessor
		processor.setValidator(csvBeanValidator()); // 2为processor制定校验器为CsvBeanValidator
		return processor;
	}

	@Bean
	public ItemWriter<BanthPerson> writer(DataSource dataSource) {// 1Spring容器能让容器已有的Bean
																	// 以参数的形式注入，Spring
																	// Boot已经为我们定义好了dataSource
		JdbcBatchItemWriter<BanthPerson> writer = new JdbcBatchItemWriter<BanthPerson>(); // 2使用JDBC批处理的JdbcBatchItemWriter来写入到数据库
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<BanthPerson>());
		String sql = "insert into BanthPerson " + "(name,age,nation,address) "
				+ "values( :name, :age, :nation,:address)";
		writer.setSql(sql); // 3执行sql语句
		writer.setDataSource(dataSource);
		return writer;
	}

	/**
	 * JobRepository定义需要dataSource和transactionManager，SpringBoot已为我们自动配置两个类，
	 * Spring可以通过方法注入已有的Bean
	 * 
	 * @param dataSource
	 * @param transactionManager
	 * @return
	 * @throws Exception
	 */
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
		return jobs.get("importJob").incrementer(new RunIdIncrementer()).flow(s1) // 1为job指定Step
				.end().listener(csvJobListener()) // 2绑定监听器csvJobListener
				.build();
	}

	@Bean
	public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<BanthPerson> reader,
			ItemWriter<BanthPerson> writer, ItemProcessor<BanthPerson, BanthPerson> processor) {
		return stepBuilderFactory.get("step1").<BanthPerson, BanthPerson> chunk(65000) // 1批处理每次提交65000条数据
				.reader(reader) // 2给step绑定reader
				.processor(processor) // 3给step绑定processor
				.writer(writer) // 4给step绑定writer
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
