package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName = "builder")
public class SongListDomain {

	private String SongSeq;
	private String mbId;
	private String SongTitle;
	private String SongSinger;
	private String SongURL;
	private String SongCreateAt;
	private String SongUpdateAt;

}
