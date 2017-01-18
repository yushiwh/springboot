package com.jztey.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jztey.demo.entity.User;

@RestController
@RequestMapping("/demo")
public class DemoController {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job importJob;
	public JobParameters jobParameters;

	/**
	 * http://localhost:8081/demo//imp?fileName=people
	 * 会被安全验证拦截，直接http请求会报连不上
	 * 输入wyf  wyf
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/imp")
	public String imp(String fileName) throws Exception {

		String path = fileName + ".csv";
		jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.addString("input.file.name", path).toJobParameters();
		jobLauncher.run(importJob, jobParameters);
		return "ok";
	}

	@RequestMapping(value = "/getDemo", method = RequestMethod.GET)
	public List<User> getDemo() {
		List<User> list = new ArrayList<User>();

		for (int i = 9; i < 13; i++) {
			User demo = new User();
			demo.setAddress("武汉" + i);
			demo.setAge(i);
			demo.setId(Long.parseLong(i + ""));
			demo.setName("姓名：" + i);
			demo.setSex(i);
			list.add(demo);
		}

		return list;
	}
}
