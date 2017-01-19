package com.jztey.demo.service.activemq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

/**
 * 消息定义
 * 
 * 定义JMS发送消息需要实现的MessageCreator接口，并且重写createMessage方法
 * 
 * @author yushi 2017-01-18
 *
 */
public class Msg implements MessageCreator {

	@Override
	public Message createMessage(Session session) throws JMSException {
		return session.createTextMessage("测试消息");
	}

}
