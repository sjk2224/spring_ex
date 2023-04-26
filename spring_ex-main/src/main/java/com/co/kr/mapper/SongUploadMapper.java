package com.co.kr.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.SongFileDomain;
import com.co.kr.domain.SongListDomain;
import com.co.kr.domain.SongURLDomain;

@Mapper
public interface SongUploadMapper {
	public List<SongListDomain> SongList();
	
	public List<SongFileDomain> SongFileList();
	
	//URL Insert
	public void URLUpload(SongURLDomain songURLDomain);
	
	//file Insert
	public void SongFileUpload(SongFileDomain songFileDomain);
	
	//URL Update
	public void SongURLUpdate(SongURLDomain songURLDomain);
	
	//file Update
	public void SongFileUpdate(SongFileDomain songFileDomain);
	
	//URL delete
	public void SongURLRemove(HashMap<String, Object> map);
	
	//file Delete
	public void SongFileRemove(SongFileDomain songFileDomain);
	
	//select one
	public SongListDomain SongSelectOne(HashMap<String, Object> map);
	
	//select one File
	public List<SongFileDomain> SongSelectOneFile(HashMap<String, Object>map);
}
