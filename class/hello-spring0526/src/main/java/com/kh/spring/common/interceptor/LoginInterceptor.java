package com.kh.spring.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.kh.spring.member.model.vo.Member;

public class LoginInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//로그인 여부를 체크해서 로그인하지 않은 경우, return false;
		HttpSession session = request.getSession();
		Member loginMember = (Member) session.getAttribute("loginMember");
		if(loginMember == null) {
			//FlashMap을 통해서 redirect후 사용자피드백 전달하기
			FlashMap flashMap = new FlashMap();
			flashMap.put("msg", "로그인 후 사용하실 수 있습니다.");
			FlashMapManager manager = RequestContextUtils.getFlashMapManager(request);
			manager.saveOutputFlashMap(flashMap, request, response);
			
			//로그인후 최종이동할 url을 session속성 next 저장
			//http://localhost:9090/spring/board/boardDetail.do?no=60&q=abc
			
			
			String url = request.getRequestURL().toString(); //http://localhost:9090/spring/board/boardDetail.do?
			//getRequestURL 은 물음표 앞까지 나타낸다
			String queryString = request.getQueryString(); //no=60&q=abc
			//getQueryString 은 물음표 뒤부터 나타낸다
			session.setAttribute("next", url + "?" + queryString);
			
			response.sendRedirect(request.getContextPath() + "/member/memberLogin.do");
			return false;
		}
		
		return super.preHandle(request, response, handler);
	}

	
}