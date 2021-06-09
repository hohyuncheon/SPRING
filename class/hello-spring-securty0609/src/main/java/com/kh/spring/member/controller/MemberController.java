package com.kh.spring.member.controller;

import java.beans.PropertyEditor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.member.model.vo.Member;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/member")
public class MemberController {

	@GetMapping("/memberLogin.do")
	public void memberLogin() {}
	
	/**
	 * Authentication
	 * 	- Principal : 인증된 사용자객체
	 *  - Credentials : 인증에 필요한 비밀번호
	 * 	- Authorities : 인증된 사용자가 가진 권한
	 */
//	@GetMapping("/memberDetail.do")
//	public void memberDetail(Model model) {
//		//1. SecurityContextHolder로부터 가져오기
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		Member principal = (Member) authentication.getPrincipal();
//		model.addAttribute("loginMember", principal);
//		
//		log.debug("authentication = {}", authentication);
//		// authentication = org.springframework.security.authentication.UsernamePasswordAuthenticationToken@23abe407: Principal: Member(id=honggd, password=$2a$10$qHHeJGgQ9teamJyIJFXbyOBtl7nIsQ37VP2jrz89dnDA7LgzS.nYi, name=카길동, gender=M, birthday=2021-05-04, email=honggd@naver.com, phone=01012341234, address=서울시 강남구, hobby=[운동,  등산], enrollDate=2021-05-20, authorities=[ROLE_USER], enabled=true); Credentials: [PROTECTED]; Authenticated: true; Details: org.springframework.security.web.authentication.WebAuthenticationDetails@166c8: RemoteIpAddress: 0:0:0:0:0:0:0:1; SessionId: B95C1041773474D93729781512D4490A; Granted Authorities: ROLE_USER
//		log.debug("principal = {}", principal);
//	}
	
	/** 
	 * 2. HandlerMapping에 security인증된 사용자 Authentication 요청하기
	 */
	@GetMapping("/memberDetail.do")
	public void memberDetail(Authentication authentication, Model model) {
		Member principal = (Member) authentication.getPrincipal();
		model.addAttribute("loginMember", principal);
		
		log.debug("authentication = {}", authentication);
		// authentication = org.springframework.security.authentication.UsernamePasswordAuthenticationToken@23abe407: Principal: Member(id=honggd, password=$2a$10$qHHeJGgQ9teamJyIJFXbyOBtl7nIsQ37VP2jrz89dnDA7LgzS.nYi, name=카길동, gender=M, birthday=2021-05-04, email=honggd@naver.com, phone=01012341234, address=서울시 강남구, hobby=[운동,  등산], enrollDate=2021-05-20, authorities=[ROLE_USER], enabled=true); Credentials: [PROTECTED]; Authenticated: true; Details: org.springframework.security.web.authentication.WebAuthenticationDetails@166c8: RemoteIpAddress: 0:0:0:0:0:0:0:1; SessionId: B95C1041773474D93729781512D4490A; Granted Authorities: ROLE_USER
		log.debug("principal = {}", principal);
	}
	
	@PostMapping("/memberUpdate.do")
	public String memberUpdate(
			Member updateMember,
			Authentication oldAuthentication,
			RedirectAttributes redirectAttr) {
		log.debug("updateMember = {}", updateMember); // password, authorites누락
		
		//1. 업무로직 : db의 member객체를 수정
		
		//2. SecurityContext의 authentication객체를 수정
		//updateMember에 누락된 정보 password, authorities 추가
		updateMember.setPassword(((Member) oldAuthentication.getPrincipal()).getPassword());
		
		Collection<? extends GrantedAuthority> oldAuthorities = 
				oldAuthentication.getAuthorities();
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		for(GrantedAuthority auth : oldAuthorities) {
			SimpleGrantedAuthority simpleAuth = 
					new SimpleGrantedAuthority(auth.getAuthority()); // 문자열을 인자로 auth객체생성 
			authorities.add(simpleAuth);
		}
		updateMember.setAuthorities(authorities);
		
		//새로운 authentication객체 생성
		Authentication newAuthentication = 
				new UsernamePasswordAuthenticationToken(
					updateMember,
					oldAuthentication.getCredentials(),
					oldAuthentication.getAuthorities()
				);
		//SecurityContextHolder - SecurityContext 하위에 설정
		SecurityContextHolder.getContext().setAuthentication(newAuthentication);
		
		//3. 사용자피드백 & 리다이렉트
		
		return "redirect:/member/memberDetail.do";
	}
	
	
	/**
	 * 커맨드객체 이용시 사용자입력값(String)을 특정필드타입으로 변환할 editor객체를 설정
	 * 
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		//Member.birthday:java.sql.Date 타입 처리
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//커스텀에디터 생성 : allowEmpty - true (빈문자열을 null로 변환처리 허용)
		PropertyEditor editor = new CustomDateEditor(sdf, true);
		binder.registerCustomEditor(java.sql.Date.class, editor);
	}
	
	
}