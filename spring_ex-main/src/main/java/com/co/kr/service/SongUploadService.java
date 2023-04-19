package com.co.kr.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.domain.SongFileDomain;
import com.co.kr.domain.SongListDomain;
import com.co.kr.vo.SongFileListVO;

public interface SongUploadService {
	
	public List<SongListDomain> SongList();
	
	//인서트 및 업데이트(노래)
	public int SongfileProcess(SongFileListVO songfileListVO, MultipartHttpServletRequest request,HttpServletRequest httpreq);
	
	//하나 삭제
	public void SongURLRemove(HashMap<String, Object> map);
	
	public void SongFileRemove(SongFileDomain songFileDomain);
	
	public SongListDomain SongSelectOne(HashMap<String, Object> map);
	
	public List<SongFileDomain> SongSelectOneFile(HashMap<String, Object>map);

}
