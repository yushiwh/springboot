package com.jztey.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jztey.framework.mvc.RestfulResult;

/**
 * Created by Charles on 2016/4/2.
 */
@Controller
@ResponseBody
@RequestMapping("/ok.htm")
public class IndexController {

	@RequestMapping(method = RequestMethod.GET)
	public RestfulResult<String> index() {
		RestfulResult test = new RestfulResult();
		String str = "ok";
		return new RestfulResult<>(str);
	}

}