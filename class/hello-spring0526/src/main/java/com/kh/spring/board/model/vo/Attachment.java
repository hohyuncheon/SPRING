package com.kh.spring.board.model.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {
	
	private int no;
	private int boardNo;
	private String originalFilename;
	private String renamedFilename;
	private Date uploadDate;
	private int downloadCount;
	private boolean status;	// status da에 있는 컬럼은 YN으로 관리됨, 그래서 자동으로 인식못함 1과 0 만 가능. 그래서 타입핸들러 써야함.

}
