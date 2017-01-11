package com.jztey.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jztey.demo.entity.PersonRedis;
import com.jztey.demo.service.RedisService;

@RestController
public class RedisController {

	@Autowired
	RedisService redisService;

	@RequestMapping("/set")
	public void set() {
		PersonRedis person = new PersonRedis("1", "ys", 32);
		redisService.save(person);
		redisService.stringRedisTemplateDemo();
	}

	@RequestMapping("/getStr")
	public String getStr() {

		return redisService.getString();
	}

	@RequestMapping("/getPerson")
	public PersonRedis getPerson() {

		return redisService.getPersonRedis();
	}
}
