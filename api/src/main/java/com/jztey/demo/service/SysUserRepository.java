package com.jztey.demo.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jztey.demo.entity.SysUser;

public interface SysUserRepository extends JpaRepository<SysUser, Long>{
	
	SysUser findByUsername(String username);

}