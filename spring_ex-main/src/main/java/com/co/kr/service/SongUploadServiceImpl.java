package com.co.kr.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.code.Code;
import com.co.kr.domain.SongFileDomain;
import com.co.kr.domain.SongListDomain;
import com.co.kr.domain.SongURLDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.mapper.SongUploadMapper;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.SongFileListVO;

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

	@Override
	public int SongfileProcess(SongFileListVO songfileListVO, MultipartHttpServletRequest request,
			HttpServletRequest httpreq) {
		HttpSession session = httpreq.getSession();
		
		SongURLDomain songURLDomain = SongURLDomain.builder()
				.mbId(session.getAttribute("id").toString())
				.SongTitle(songfileListVO.getSongtitle())
				.SongSinger(songfileListVO.getSongSinger())
				.SongURL(songfileListVO.getSongURL())
				.build();
		
		
		if(songfileListVO.getSongisEdit() != null) {
			songURLDomain.setSongSeq(Integer.parseInt(songfileListVO.getSongseq()));
			System.out.println("수정 업데이트");
		}else {
			songUploadMapper.URLUpload(songURLDomain);
			System.out.println("db 인서트");
		}
		
		int SongSeq = songURLDomain.getSongSeq();
		System.out.println(songURLDomain);
		
		String mbId = songURLDomain.getMbId();
		
		List<MultipartFile> multipartFiles = request.getFiles("files");
		
		if(songfileListVO.getSongisEdit() != null) {
			List<SongFileDomain> fileList = null;
			
			for(MultipartFile multipartFile : multipartFiles) {
				if(!multipartFile.isEmpty()) {
					if (session.getAttribute("files") != null) {
						fileList = (List<SongFileDomain>) session.getAttribute("files");
						
						for(SongFileDomain list: fileList) {
							list.getUpFilePath();
							Path filePath = Paths.get(list.getUpFilePath());
							
							try {
								Files.deleteIfExists(filePath);
								SongFileRemove(list);
							}catch(DirectoryNotEmptyException e ) {
								throw RequestException.fire(Code.E404,"디렉토리가 존재 하지 않습니다.",HttpStatus.NOT_FOUND);
							}catch(IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		
		Path rootPath = Paths.get(new File("C://").toString(),"upload",File.separator).toAbsolutePath().normalize();
		File pathCheck = new File(rootPath.toString());
		
		if(!pathCheck.exists()) pathCheck.mkdirs();
		
		for(MultipartFile multipartFile : multipartFiles) {
			if(!multipartFile.isEmpty()) {
				
				String originalFileExtension;
				String contentType = multipartFile.getContentType();
				String origFileName = multipartFile.getOriginalFilename();
				
				if(ObjectUtils.isEmpty(contentType)) {
					break;
				}else {
					if(contentType.contains("image/jpeg")) {
						originalFileExtension = ".jpg";
					}else if(contentType.contains("image/png")) {
						originalFileExtension = ".png";
					}else {
						break;
					}
				}
				

				String uuid = UUID.randomUUID().toString();
				String current = CommonUtils.currentTime();
				String newFileName = uuid + current + originalFileExtension;
				
				Path targetPath = rootPath.resolve(newFileName);
				
				File file = new File(targetPath.toString());
				
				try {
					multipartFile.transferTo(file);
					file.setWritable(true);
					file.setReadable(true);
					
					SongFileDomain songFileDomain = SongFileDomain.builder()
							.SongSeq(SongSeq)
							.mbId(mbId)
							.upOrginalFileName(origFileName)
							.upNewFileName("resources/upload/"+newFileName)
							.upFilePath(targetPath.toString())
							.upFileSize((int)multipartFile.getSize())
							.build();
					
					songUploadMapper.SongFileUpdate(songFileDomain);
					System.out.println("upload done");
				}catch(IOException e) {
					throw RequestException.fire(Code.E404,"잘못된 업로드 파일", HttpStatus.NOT_FOUND);
				}
			}
			
		}
		
		
		return SongSeq;
	}

	@Override
	public void SongURLRemove(HashMap<String, Object> map) {
		// TODO Auto-generated method stub
		songUploadMapper.SongURLRemove(map);
	}

	@Override
	public void SongFileRemove(SongFileDomain songFileDomain) {
		// TODO Auto-generated method stub
		songUploadMapper.SongFileRemove(songFileDomain);
	}

	@Override
	public SongListDomain SongSelectOne(HashMap<String, Object> map) {
		// TODO Auto-generated method stub
		return songUploadMapper.SongSelectOne(map);
	}

	@Override
	public List<SongFileDomain> SongSelectOneFile(HashMap<String, Object> map) {
		// TODO Auto-generated method stub
		return songUploadMapper.SongSelectOneFile(map);
	}
	
}
