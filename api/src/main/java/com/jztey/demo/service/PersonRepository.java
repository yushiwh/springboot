package com.jztey.demo.service;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.jztey.demo.entity.Person;

public interface PersonRepository extends MongoRepository<Person, String> {
	Person findByName(String name);// 支持方法名查询

	@Query("{'age':?0}") // 支持Query查询，查询参数构造 json字符串即可
	List<Person> withQueryFindByAge(Integer age);

}
