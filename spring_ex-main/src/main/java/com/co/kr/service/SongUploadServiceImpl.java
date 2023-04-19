package com.co.kr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.co.kr.domain.SongListDomain;
import com.co.kr.mapper.SongUploadMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SongUploadServiceImpl implements SongUploadService{
	
	@Autowired(required = false)
	SongUploadMapper songUploadMapper;
	
	@Override
	public List<SongListDomain> SongList() {
		// TODO Auto-generated method stub
		return songUploadMapper.SongList();
	}
	
}
