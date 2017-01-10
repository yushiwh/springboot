package com.jztey.demo.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by yushi on 2017/1/10.
 */
@ConfigurationProperties(prefix = "demoN", locations = { "classpath:test.properties" })
public class Config {

	String userNameN;// 配置文件的一个属性值

	public String getUserNameN() {
		return userNameN;
	}

	public void setUserNameN(String userNameN) {
		this.userNameN = userNameN;
	}

}
