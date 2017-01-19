package com.jztey.demo.service.activemq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.core.JmsTemplate;

/**
 * 消息发送及目的地定义
 * 
 * @author yushi
 *
 */
// springboot提供CommandLineRunner接口，用于程序启动后执行的代码，通过重写其run方法执行

public class Ch934Application implements CommandLineRunner {

	@Autowired
	JmsTemplate jmsTemplate;// 注入springboot为我们配置好的bean

	@Override
	public void run(String... arg0) throws Exception {
		// 通过jmsTemplate的send方法向my-destination目的地发送Msg消息，
		// 也等于是在消息代理上面定义一个目的地叫my-destination
		jmsTemplate.send("my-destination", new Msg());

	}

	public static void main(String[] args) {
		SpringApplication.run(Ch934Application.class, args);
	}

}
