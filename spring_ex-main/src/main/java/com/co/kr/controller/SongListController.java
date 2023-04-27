package com.co.kr.controller;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.co.kr.code.Code;
import com.co.kr.domain.SongFileDomain;
import com.co.kr.domain.SongListDomain;
import com.co.kr.exception.RequestException;
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
		System.out.println("fileLIst : "+fileList);
		
		for(SongFileDomain list: fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}
		System.out.println("fileLIst : "+fileList);
		
		mav.addObject("SongDetail",songListDomain);
		mav.addObject("files", fileList);
		
		session.setAttribute("files", fileList);
		
		return mav;
	}
	
	@GetMapping("SongDetail")
	public ModelAndView SongDetail(@ModelAttribute("songFileListVO") SongFileListVO songFileListVO, @RequestParam("SongSeq") String SongSeq, HttpServletRequest request) throws IOException{
		ModelAndView mav = new ModelAndView();
		mav = SongSelectOneCall(songFileListVO, SongSeq, request);
		
		System.out.println(mav);
		mav.setViewName("board/songList.html");
		return mav;
	}
	
	@GetMapping("SongEdit")
	public ModelAndView SongEdit(SongFileListVO songFileListVO, @RequestParam("SongSeq") String SongSeq, HttpServletRequest request)throws IOException{
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String,Object>();
		HttpSession session = request.getSession();
		
		map.put("SongSeq", Integer.parseInt(SongSeq));
		System.out.println(map);
		SongListDomain songListDomain = songUploadService.SongSelectOne(map);
		System.out.println("SongListDomain " + songListDomain);
		List<SongFileDomain> fileList = songUploadService.SongSelectOneFile(map);
		System.out.println("fileLIst : "+fileList);
		
		for(SongFileDomain list: fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}
		System.out.println("fileLIst : "+fileList);
		
		songFileListVO.setSongseq(songListDomain.getSongSeq());
		songFileListVO.setSongSinger(songListDomain.getSongSinger());
		songFileListVO.setSongURL(songListDomain.getSongURL());
		songFileListVO.setSongtitle(songListDomain.getSongTitle());
		songFileListVO.setSongisEdit("SongEdit");
		
		mav.addObject("SongDetail",songListDomain);
		mav.addObject("files", fileList);
		mav.addObject("fileLen",fileList.size());
		
		mav.setViewName("board/songEditList.html");
		
		session.setAttribute("files", fileList);
		
		return mav;
	}
	
	@PostMapping("SongEditSave")
	public ModelAndView SongEditSave(@ModelAttribute("songFileListVO") SongFileListVO songFileListVO, MultipartHttpServletRequest request, HttpServletRequest httReq) throws IOException{
		ModelAndView mav = new ModelAndView();
		
		//저장 
		songUploadService.SongfileProcess(songFileListVO, request, httReq);
		
		mav = SongSelectOneCall(songFileListVO, songFileListVO.getSongseq(), request);
		songFileListVO.setSongURL("");
		songFileListVO.setSongSinger("");
		songFileListVO.setSongtitle("");
		System.out.println(mav);
		mav.setViewName("board/songList.html");
		return mav;
		
	}
	
	@GetMapping("SongRemove")
	public ModelAndView SongRemove(@RequestParam("SongSeq") String SongSeq, HttpServletRequest request) throws IOException{
		ModelAndView mav = new ModelAndView();
		
		HttpSession session = request.getSession();
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<SongFileDomain> songFileList = null;
		if(session.getAttribute("files") != null) {
			songFileList = (List<SongFileDomain>) session.getAttribute("files");
		}
		map.put("SongSeq", Integer.parseInt(SongSeq));
		
		//내용삭제
		songUploadService.SongURLRemove(map);
		for(SongFileDomain list : songFileList) 
		{
			list.getUpFilePath();
			Path filePath = Paths.get(list.getUpFilePath());
			
			try {
				//파일 물리 삭제
				Files.deleteIfExists(filePath);
				//db삭제
				songUploadService.SongFileRemove(list);
			}catch (DirectoryNotEmptyException e) {
				// TODO: handle exception
				throw RequestException.fire(Code.E404,"디렉토리가 존재하지 않습니다.",HttpStatus.NOT_FOUND);
			}catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		session.removeAttribute("files");
		mav = songListCall();
		mav.setViewName("redirect:/songList");
		return mav;
	}
	
	//리스트 가져오기 함수
	public ModelAndView songListCall() {
		ModelAndView mav = new ModelAndView();
		List<SongListDomain> items = songUploadService.SongList();
		mav.addObject("items",items);
		return mav;
	}
}
