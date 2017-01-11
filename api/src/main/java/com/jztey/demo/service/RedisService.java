package com.jztey.demo.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jztey.demo.entity.Person;
import com.jztey.demo.entity.PersonRedis;
import com.jztey.demo.service.UserService.RestfulResultUser;
import com.jztey.framework.mvc.RestfulResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
 
@Api(tags = "redis配置")
@ResponseBody
public interface RedisService {
	public void stringRedisTemplateDemo();

	public String getString();

	public PersonRedis getPersonRedis() ;
	public void save(PersonRedis personRedis);

}
