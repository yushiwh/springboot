package com.jztey.demo;

import java.net.UnknownHostException;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
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

}
