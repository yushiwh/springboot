package com.jztey.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jztey.demo.entity.Demo;
import com.jztey.demo.entity.User;

@RestController
@RequestMapping("/demo")
public class DemoController {
	@RequestMapping(value = "/getDemo", method = RequestMethod.GET)
	public List<User> getDemo() {
		List<User> list = new ArrayList<User>();
		
		for (int i = 9; i < 13; i++) {
			User demo = new User();
			demo.setAddress("武汉" + i);
			demo.setAge(i);
			demo.setId(Long.parseLong(i+""));
			demo.setName("姓名：" + i);
			demo.setSex(i);
			list.add(demo);
		}

		return list;
	}
}
