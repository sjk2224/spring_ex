package com.co.kr.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.SongListDomain;

@Mapper
public interface SongUploadMapper {
	public List<SongListDomain> SongList();
}
