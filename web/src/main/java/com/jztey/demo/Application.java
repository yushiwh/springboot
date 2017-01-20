package com.jztey.demo;

import static java.lang.System.getProperty;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.dsl.file.Files;
import org.springframework.integration.dsl.mail.Mail;
import org.springframework.integration.feed.inbound.FeedEntryMessageSource;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.jztey.demo.entity.Config;
import com.jztey.demo.entity.ConfigOther;
import com.jztey.demo.service.activemq.Msg;
import com.jztey.framework.boot.ApplicationDruid;
import com.jztey.framework.boot.ApplicationInterfaceMvc;
import com.jztey.framework.boot.ApplicationMonitoring;
import com.rometools.rome.feed.synd.SyndEntry;

/**
 * Created by Charles on 2016/8/8.
 */

@SpringBootApplication
@Import({ ApplicationInterfaceMvc.class //
		, ApplicationCaching.class
		// , ApplicationDubbo.class//导入dubbo
		, ApplicationDruid.class// 导入Druid
		, ApplicationMonitoring.class, ApplicationInterfaceMvc.class// 统一的异常处理
})
@EnableConfigurationProperties({ Config.class, ConfigOther.class }) // 加载多个实体
// springboot提供CommandLineRunner接口，用于程序启动后执行的代码，通过重写其run方法执行
public class Application implements CommandLineRunner {
	@Value("https://spring.io/blog.atom") // 通过@Value注解注入https://spring.io/blog.atom的资源
	Resource resource;

	@Autowired
	JmsTemplate jmsTemplate;// 注入springboot为我们配置好的bean

	@Autowired
	RabbitTemplate rabbitTemplate;// 可注入SprinfBoot为我们自动配置好的RabbitTemplate

	@Bean
	public WebMvcConfigurer corsConfigurer() { // 允许跨域
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST", "DELETE", "PUT",
						"OPTIONS");
			}
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	// redis配置一下
	@Bean
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
			throws UnknownHostException {
		RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

	// 定义目的地及队列，队列的名称为my-queue
	@Bean
	public Queue wiselyQueue() {
		return new Queue("my-queue");
	}

	@Override
	public void run(String... arg0) throws Exception {
		// 通过jmsTemplate的send方法向my-destination目的地发送Msg消息，
		// 也等于是在消息代理上面定义一个目的地叫my-destination
		jmsTemplate.send("my-destination", new Msg());

		rabbitTemplate.convertAndSend("my-queue", "来自RabbitMQ的问候");// 通过rabbitTemplate的convertAndSend方法向队列发送消息

	}

	// ***************************读取流程开始*********************//
	@Bean(name = PollerMetadata.DEFAULT_POLLER)
	public PollerMetadata poller() { // 2
		return Pollers.fixedRate(500).get();
	}

	@Bean
	public FeedEntryMessageSource feedMessageSource() throws IOException { // 3
		// FeedEntryMessageSource实际上是feed:inbound-channel-adapter,此处作为通道的适配器数据输入
		FeedEntryMessageSource messageSource = new FeedEntryMessageSource(resource.getURL(), "news");
		return messageSource;
	}

	@Bean
	public IntegrationFlow myFlow() throws IOException {
		return IntegrationFlows.from(feedMessageSource()) // 4流程从flow开始
				// 5通过路由方法route来选择路由，消息体(PlyLoad)的类型为SyndEntry，作为条件判断类型为String，判断值是通过payload获得的分类
				.<SyndEntry, String> route(payload -> payload.getCategories().get(0).getName(), // 5
						// 6通过不同的分类的值转向不同的消息通道
						mapping -> mapping.channelMapping("releases", "releasesChannel") // 6
								.channelMapping("engineering", "engineeringChannel")
								.channelMapping("news", "newsChannel"))

				.get(); // 7通过get方法获得IntegrationFlows的实体，配置的Spring的Bean
	}

	// ******************读取流程结束********************************//

	// *******************Release流程开始***************************//
	@Bean
	public IntegrationFlow releasesFlow() {
		return IntegrationFlows.from(MessageChannels.queue("releasesChannel", 10)) // 1从消息通道releasesChannel开始获取数据
				.<SyndEntry, String> transform(
						// 2使用transform方法进行数据转换。payload类型为SyndEntry，将其转换成字符串类型，并且自定义数据格式
						payload -> "《" + payload.getTitle() + "》 " + payload.getLink() + getProperty("line.separator")) // 2
				.handle(Files.outboundAdapter(new File("e:/springblog")) // 3//
																			// 3用handle方法处理file的出战适配器。File类是有Spring
																			// Integration
																			// Java
						// DSL提供的fluent API用来构造文件的输出的适配器
						.fileExistsMode(FileExistsMode.APPEND) // 4
						.charset("UTF-8") // 5
						.fileNameGenerator(message -> "releases.txt") // 6
						.get())
				.get();
	}

	// *************engineeringFlow流程*********************//
	@Bean
	public IntegrationFlow engineeringFlow() {
		return IntegrationFlows.from(MessageChannels.queue("engineeringChannel", 10))
				.<SyndEntry, String> transform(
						payload -> "《" + payload.getTitle() + "》 " + payload.getLink() + getProperty("line.separator"))
				.handle(Files.outboundAdapter(new File("e:/springblog")).fileExistsMode(FileExistsMode.APPEND)
						.charset("UTF-8").fileNameGenerator(message -> "engineering.txt").get())
				.get();
	}

	// **********************************news流程***********************************
	@Bean
	public IntegrationFlow newsFlow() {
		return IntegrationFlows.from(MessageChannels.queue("newsChannel", 10))
				.<SyndEntry, String> transform(
						payload -> "《" + payload.getTitle() + "》 " + payload.getLink() + getProperty("line.separator"))
				.enrichHeaders( // 1
						Mail.headers().subject("来自Spring的新闻").to("94179094@qq.com").from("94179094@qq.com"))
				.handle(Mail.outboundAdapter("smtp.qq.com") // 2
						.port(25).protocol("smtp").credentials("94179094@qq.com", "******")
						.javaMailProperties(p -> p.put("mail.debug", "false")), e -> e.id("smtpOut"))
				.get();
	}

}
