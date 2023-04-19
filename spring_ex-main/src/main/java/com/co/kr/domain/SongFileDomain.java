package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName = "builder")
public class SongFileDomain {
	private Integer SongSeq;
	private String mbId;
	private String upOrginalFileName;
	private String upNewFileName;
	
	private String upFilePath;
	private Integer upFileSize;
}
