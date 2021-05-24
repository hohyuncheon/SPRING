package com.kh.spring.memo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.demo.model.vo.Dev;
import com.kh.spring.memo.model.service.MemoService;
import com.kh.spring.memo.model.vo.Memo;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/memo")
public class MemoController {
	
	@Autowired
	private MemoService memoService;

	
	
	@GetMapping("/memo.do")
	public ModelAndView memo(ModelAndView mav) {
		
		log.debug("memoService = {}", memoService.getClass());
		
		
		//1. 업무로직 : memo목록 조회
	
		List<Memo> list = memoService.selectMemoList(); 
		log.debug("list = {}", list);
	 
		//2. jsp위임
		mav.addObject("list", list);
		 
		mav.setViewName("memo/memo");
		return mav;
	}
	
	@PostMapping("/insertMemo.do")
	public String insertMemo(Memo memo, RedirectAttributes redirectAttr){
		
		try {
			String context = memo.getMemo();
			log.info("memo = {}", memo);
			//1. 업무로직
			int result = memoService.insertMemo(memo);
			//2. 사용자피드백
			redirectAttr.addFlashAttribute("msg", "메모 작성성공");
			
		} catch (Exception e) {
			log.error("메모작성 오류!", e);
			throw e;
		}
		
		return "redirect:/memo/memo.do";
	}
	
}