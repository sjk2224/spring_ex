package com.co.kr.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.co.kr.domain.SongFileDomain;
import com.co.kr.domain.SongListDomain;
import com.co.kr.service.SongUploadService;
import com.co.kr.vo.SongFileListVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SongListController {
	
	@Autowired(required = false)
	SongUploadService songUploadService;
	
	@PostMapping(value = "SongUpload")
	public ModelAndView SongUpload(SongFileListVO songFileListVO, MultipartHttpServletRequest request, HttpServletRequest httRequest) throws IOException, ParseException {
		ModelAndView mav = new ModelAndView();
		
		int SongSeq = songUploadService.SongfileProcess(songFileListVO, request, httRequest);
		songFileListVO.setSongURL("");
		songFileListVO.setSongtitle("");
		
		mav = SongSelectOneCall(songFileListVO,String.valueOf(SongSeq),request);
		mav.setViewName("board/songList.html");
		
		return mav;
	}
	
	public ModelAndView SongSelectOneCall(@ModelAttribute("songFileListVO") SongFileListVO songFileListVO, String SongSeq, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String,Object>();
		HttpSession session = request.getSession();
		
		map.put("SongSeq", Integer.parseInt(SongSeq));
		System.out.println(map);
		SongListDomain songListDomain = songUploadService.SongSelectOne(map);
		System.out.println("SongListDomain " + songListDomain);
		List<SongFileDomain> fileList = songUploadService.SongSelectOneFile(map);
		
		for(SongFileDomain list: fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}
		
		mav.addObject("detail",songListDomain);
		mav.addObject("files", fileList);
		
		session.setAttribute("files", fileList);
		
		return mav;
	}
}
