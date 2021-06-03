package com.kh.spring.board.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
	
	@Autowired
	private ResourceLoader resourceLoader; // 자원관리 file down할대 사용
	
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
	
	//1번쨰 방법
	//@GetMapping(value = "/fileDownload.do", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody // 응답메세지에 return객체를 직접 출력 (쓰기작업을 대신해준다)
	public Resource fileDownload(@RequestParam int no, HttpServletResponse response) throws UnsupportedEncodingException {
		
		
		//1. 업무로직 :db에서 첨부파일 정보 조회
		
		Attachment attach = boardService.selectOneAttachment(no);
		log.debug("attach = {} " , attach);
		
		if(attach == null) {
			throw new IllegalArgumentException("해당 첨부파일은 존재하지 않습니다 : " + no);
		}
		
		//2. Resource객체를 리턴 (응답메세지 출력 대신해줌) : 응답메세지에서 출력은 spring-container가 처리
		
		String originalFilename = attach.getOriginalFilename();
		String renamedFilename = attach.getRenamedFilename();
		String saveDirectory = application.getRealPath("/resources/upload/board");
		File downFile = new File(saveDirectory, renamedFilename);
		
		//웹상의 자원, 서버컴퓨터의 자원(파일, 이미지, 등등)를 모두 다룰수 있는 스프링의 추상화 layer (psa의 단편적인 예)
		
		//String location = "https://docs.oracle.com/javase/8/docs/api/java/lang/String.html"; // 웹상파일경로 test
		String location = "file:" + downFile.toString(); //파일경로를 알려줌
		log.debug("location = {} " , location);
		
		Resource resource = resourceLoader.getResource(location); 
		
		
		//3. 응답헤더
		
		//한글깨짐방지처리
		//originalFilename = "String.html"; //웹상파일경로 test
		originalFilename = new String(originalFilename.getBytes("utf-8"), "iso-8859-1"); //utf-8인데 8859로 변환해주세요
		/* response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE); *///작동안해서 주석 대신 위에 mapping에 써줌
		response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + originalFilename);
		
		
		return resource;
		
		
	}
	
	
	
	//2번째 방법responseEntity써보기
	/**
	 * ResponseEntity
	 * 1. status code 커스터마이징
	 * 2. 응답 header 커스터마이징
	 * 3. @ResponseBody 기능 포함
	 * 
	 */
	@GetMapping("/fileDownload.do")
	public ResponseEntity<Resource> fileDownloadWithResponseEntity(@RequestParam int no) 
			throws UnsupportedEncodingException{
		ResponseEntity<Resource> responseEntity = null;
		try {
			//1. 업무로직 : db조회
			Attachment attach = boardService.selectOneAttachment(no);
			if(attach == null) {
				return ResponseEntity.notFound().build();
			}
			
			//2. Resource객체
			String saveDirectory = application.getRealPath("/resources/upload/board");
			File downFile = new File(saveDirectory, attach.getRenamedFilename());
			Resource resource = resourceLoader.getResource("file:" + downFile);
			String filename = new String(attach.getOriginalFilename().getBytes("utf-8"), "iso-8859-1");
			
			//3. ResponseEntity객체 생성 및 리턴
			//builder패턴
			responseEntity = 
					ResponseEntity
						.ok()
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename)
						.body(resource);
		} catch (Exception e) {
			log.error("파일 다운로드 오류", e);
			throw e;
		}
		return responseEntity;
	}
	
	

	@GetMapping("/searchTitle.do")
	@ResponseBody
	public Map<String, Object> searchTitle(@RequestParam String searchTitle){
		log.debug("searchTitle = {}", searchTitle);
		
		//1. 업무로직 : 검색어로 board 조회
		List<Board> list = boardService.searchTitle(searchTitle);
		log.debug("list = {}", list);

		//2. map에 검색결과 담아서 전송
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("searchTitle", searchTitle);
		return map;
	}
	

}
