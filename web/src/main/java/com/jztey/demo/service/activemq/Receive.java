package com.jztey.demo.service.activemq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * 消息监听
 * 
 * @author yushi
 *
 *页面中查看ActiveMQ的地址http://10.2.106.223:8161/admin/queues.jsp
 *
 */
@Component
public class Receive {

	// @JmsListener用来简化JMS开发，只需要注解这个属性destination指定要监听的目的地，即可收到发送地的消息
	@JmsListener(destination = "my-destination")
	public void receiveMessage(String message) {
		System.out.println("接收到：<" + message + ">");

	}

}
