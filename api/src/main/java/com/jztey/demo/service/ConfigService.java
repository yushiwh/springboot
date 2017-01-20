package com.jztey.demo.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jztey.demo.service.UserService.RestfulResultUser;
import com.jztey.framework.mvc.RestfulResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RequestMapping("/config")
@Api(tags = "配置文件")
@ResponseBody
public interface ConfigService {
	@RequestMapping(value = "/pb/getConfig/application", method = RequestMethod.GET)
	@ApiOperation(value = "直接取得配置文件application.properties", response = RestfulResultUser.class)
	public RestfulResult<String> getConfigApplication() throws Exception;

	@RequestMapping(value = "/pb/getConfig/test", method = RequestMethod.GET)
	@ApiOperation(value = "通过指定路径取得配置文件test.properties", response = RestfulResultUser.class)
	public RestfulResult<String> getConfigTest() throws Exception;

	@RequestMapping(value = "/pb/getConfig/autowired", method = RequestMethod.GET)
	@ApiOperation(value = "通过注入方式取得配置文件test.properties", response = RestfulResultUser.class)
	public RestfulResult<String> getConfigAutowired() throws Exception;
	
	@RequestMapping(value = "/pb/loader", method = RequestMethod.GET)
	@ApiOperation(value = "热部署看看", response = RestfulResultUser.class)
	public RestfulResult<String> loader() throws Exception;
	

}
