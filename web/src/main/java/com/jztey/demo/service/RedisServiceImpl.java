package com.jztey.demo.service;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.jztey.demo.entity.Config;
import com.jztey.demo.entity.ConfigOther;
import com.jztey.demo.entity.Person;
import com.jztey.demo.entity.PersonRedis;
import com.jztey.framework.mvc.RestfulResult;

@Service
public class RedisServiceImpl implements RedisService {

	@Autowired
	StringRedisTemplate stringRedisTemplate;// 直接植入StringRedisTemplate

	@Resource(name = "stringRedisTemplate")
	ValueOperations<String, String> valOpsStr;// 使用@Resource指定stringRedisTemplate，可注入基于字符串的简单属性操作方法

	@Autowired
	RedisTemplate<Object, Object> redisTemplate;// 直接植入RedisTemplate

	@Resource(name = "redisTemplate")
	ValueOperations<Object, Object> valOps;// 使用@Resource指定redisTemplate，可注入基于对象的简单属性操作方法

	public void stringRedisTemplateDemo() {
		valOpsStr.set("xx", "yy");
	}
	
	public void save(PersonRedis personRedis) {
		valOps.set(personRedis.getId(), personRedis);
	}

	public String getString() {
		return valOpsStr.get("xx");
	}

	public PersonRedis getPersonRedis() {
		return (PersonRedis) valOps.get("1");
	}

}
