package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName = "builder")
public class SongURLDomain {
	private int SongSeq;
	private String mbId;
	
	private String SongTitle;
	private String SongSinger;
	private String SongURL;
}
