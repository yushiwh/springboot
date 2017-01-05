package com.jztey.demo.entity;

import java.util.List;

/**
 * Created by yushi on 2016/12/2.
 */
public class DemoGetReponse {

	private String success;
	private String code;
	private String message;
	private List<Demo> data;

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<Demo> getData() {
		return data;
	}

	public void setData(List<Demo> data) {
		this.data = data;
	}

}
