package com.kh.spring.board.model.vo;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class BoardExt extends Board {
	
	
	private boolean hasAttachment;
	
	private List<Attachment> attachList;

}
