package com.kh.spring.demo.model.service;

import java.sql.Connection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.demo.model.dao.DemoDao;
import com.kh.spring.demo.model.vo.Dev;

@Service
public class DemoServiceimpl implements DemoService {
	
	@Autowired
	private DemoDao demoDao;

	
	/*
	 * Spring Aop기술을 이용해서 Session 객체 관리(시작 ~ 끝), 트랜잭션 처리
	 * 
	 */
	@Override
	public int insertDev(Dev dev) {
		//1. SqlSession 객체 생성
		//2. dao 업무요청
		//3. transaction처리
		//4. SqlSession반납
		
		return demoDao.insertDev(dev);
	}


	
	@Override
	public List<Dev> selectDevList() {
		return demoDao.selectDevList();
	}



	@Override
	public Dev selectDevOne(int no) {
		return demoDao.selectDevOne(no);
	}



	@Override
	public int updateDev(Dev dev) {
		return demoDao.updateDev(dev);
	}



	@Override
	public int deleteDev(int no) {
		return demoDao.deleteDev(no);
	}
	
	
	
	
	
	
	
	
	

}
