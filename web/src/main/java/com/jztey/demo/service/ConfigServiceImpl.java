package com.jztey.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import com.jztey.demo.entity.Config;
import com.jztey.demo.entity.ConfigOther;
import com.jztey.framework.mvc.RestfulResult;

@Service
@com.alibaba.dubbo.config.annotation.Service
@ConfigurationProperties(prefix = "demoN", locations = { "classpath:test.properties" }) // 双重配置文件读取，自定义

public class ConfigServiceImpl implements ConfigService {

	@Value("${demo.account}") // 直接读取application.properties
	private String configaccount;
	@Value("${demo.remark}")
	private String remark;

	private String userNameN;// 自定义读取配置文件的变量

	public String getUserNameN() {
		return userNameN;
	}

	public void setUserNameN(String userNameN) {
		this.userNameN = userNameN;
	}

	@Autowired
	private Config config;// 注入的方式获取

	@Autowired
	private ConfigOther configOther;// 注入的方式获取

	@Override
	public RestfulResult<String> getConfigApplication() throws Exception {

		String configvalue = "自动读取application.properties:" + configaccount + "----" + remark;

		return new RestfulResult<>(configvalue);
	}

	@Override
	public RestfulResult<String> getConfigTest() throws Exception {
		String configvalueN = "指定配置文件的：" + userNameN;
		return new RestfulResult<>(configvalueN);
	}

	@Override
	public RestfulResult<String> getConfigAutowired() throws Exception {

		String configvalueN = "自动注入的：" + config.getUserNameN() + "---另外一个test1.properties:"
				+ configOther.getUserNameN();
		return new RestfulResult<>(configvalueN);

	}

	@Override
	public RestfulResult<String> loader() throws Exception {
		String str = "随意更改这里的文字，热部署哦,第二种方法测试一下";
		return new RestfulResult<>(str);
	}

}
