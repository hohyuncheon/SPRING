package com.kh.spring.demo.model.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.spring.demo.model.service.DemoService;


@Repository
public class DemoDaoimpl implements DemoDao {
	
	@Autowired
	private DemoService demoService;

}
