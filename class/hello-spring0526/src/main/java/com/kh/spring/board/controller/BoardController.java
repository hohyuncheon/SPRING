package com.kh.spring.board.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.Attachment;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardExt;
import com.kh.spring.common.util.HelloSpringUtils;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/board")
@Slf4j
public class BoardController {
	
	
	@Autowired
	private BoardService boardService;
	@Autowired
	private ServletContext application; //서버시작했을때부터 끝날때까지 존재하는 생명주기 제일 긴녀석
	
	@GetMapping("/boardList.do")
	public String boardList(@RequestParam (required= true, defaultValue = "1") int cpage,  Model model, HttpServletRequest request) { //없으면 1 default 라는뜻
		
		
		try {
			//1. 업무로직 : board목록 조회
			
			log.debug("cpage = {}", cpage);
			final int limit = 10;
			final int offset = (cpage - 1) * limit;
			Map<String, Object> param = new HashMap<>();
			param.put("limit", limit);
			param.put("offset", offset);
			
			//1. 업무로직 : content 영역 - rowbounds
			
			
			List<Board> list = boardService.selectBoardList(param); 
			log.debug("list = {}", list);
			
			
			int totalContents = boardService.selectBoardTotalContents();
			String url = request.getRequestURI();
			log.debug("totalContents = {}, url = {}", totalContents, url);
			String pageBar = HelloSpringUtils.getPageBar(totalContents, cpage, limit, url);
			
			//2. jsp에 위임
			model.addAttribute("list", list);
			model.addAttribute("pageBar", pageBar);
		} catch (Exception e) {
			log.error("게시글 조회 오류", e);
			throw e;
		}
		
		return "board/boardList";
	}
	
	
	
	
	@GetMapping("/boardForm.do")
	public void boardForm() {
		
	}
	
	
	@PostMapping("/boardEnroll.do")
	public String boardEnroll(
						@ModelAttribute BoardExt board,
						@RequestParam(name = "upFile") MultipartFile[] upFiles,
						RedirectAttributes redirectAttr
					) throws Exception {
		
		try {
			log.debug("board = {}", board);
			//1. 파일 저장 : 절대경로 /resources/upload/board
			//pageContext:PageContext - request:HttpServletRequest - session:HttpSession - application:ServletContext
			String saveDirectory = application.getRealPath("/resources/upload/board");
			log.debug("saveDirectory = {}", saveDirectory);
			
			//디렉토리 생성
			File dir = new File(saveDirectory);
			if(!dir.exists())
				dir.mkdirs(); // 복수개의 디렉토리를 생성
			
			List<Attachment> attachList = new ArrayList<>();
			
			for(MultipartFile upFile : upFiles) {
				//input[name=upFile]로부터 비어있는 upFile이 넘어온다.
				if(upFile.isEmpty()) continue;
				
				String renamedFilename = 
						HelloSpringUtils.getRenamedFilename(upFile.getOriginalFilename());
				
				//a.서버컴퓨터에 저장
				File dest = new File(saveDirectory, renamedFilename);
				upFile.transferTo(dest); // 파일이동
				
				//b.저장된 데이터를 Attachment객체에 저장 및 list에 추가
				Attachment attach = new Attachment();
				attach.setOriginalFilename(upFile.getOriginalFilename());
				attach.setRenamedFilename(renamedFilename);
				attachList.add(attach);
			}
			
			log.debug("attachList = {}", attachList);
			//board객체에 설정
			board.setAttachList(attachList);
			
			//2. 업무로직 : db저장 board, attachment
			int result = boardService.insertBoard(board);
			
			//3. 사용자피드백 &  리다이렉트
			redirectAttr.addFlashAttribute("msg", "게시글등록 성공!");
			
			
			
			/*
			 * for(MultipartFile upFile : upFiles) { log.debug("upFile = {}", upFile);
			 * log.debug("upFile.name = {}", upFile.getOriginalFilename());
			 * log.debug("upFile.size = {}", upFile.getSize());
			 * log.debug("--------------------------------------------"); }
			 */
			
		} catch (Exception e) {
			log.error("게시글 등록 오류!", e);
			throw e;
		}
		return "redirect:/board/boardDetail.do?no="+ board.getNo();
	}
	
	
	
	@GetMapping("/boardDetail.do")
	public void selectOneBoard(@RequestParam int no, Model model) {
		
		//1. 업무로직
		//BoardExt board = boardService.selectOneBoard(no);
		BoardExt board = boardService.selectOneBoardCollection(no);
		
		log.debug("board = {}" , board);
		
		//2. 위임
		
		model.addAttribute("board", board);
	}
	
	@GetMapping("/fileDownload.do")
	public void fileDownload(@RequestParam int no) {
		
		
		//1. 업무로직
		
		
		
		//2. 위임
		
	}

}
