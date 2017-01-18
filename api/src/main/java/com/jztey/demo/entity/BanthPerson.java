package com.jztey.demo.entity;

import javax.validation.constraints.Size;

/**
 * Created by yushi on 2017/01/17
 */
public class BanthPerson {

	@Size(max = 4, min = 2) // 使用JSR-303注解来验证数据
	private String name;

	private int age;

	private String nation;

	private String address;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
