
package com.kh.spring.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kh.spring.user.model.service.UserService;

//이거쓰면 스프링이 관리해줌 bean등록 인터페이스에 쓰지말고 class에 쓴다 즉 여기
@Component
@Scope("prototype")
@Lazy // lazy-init="true"과 동일
public class UserController {
	
	/**
	 * 1.field
	 */
	//@Autowired
	private UserService userService;
	
	/**
	 * 2. setter
	 * 
	 */
//	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	
	public void getUserDetail(String id) {
		userService.getUserDetail(id);
	}
	
}