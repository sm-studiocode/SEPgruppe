package kr.or.ddit.works.attachFile.service;

import java.io.FileNotFoundException;

import org.springframework.core.io.Resource;

import kr.or.ddit.works.attachFile.vo.AttachFileVO;

public interface AttachFileService {

	public Resource getAttachFileDownload(AttachFileVO file) throws FileNotFoundException;

    public void fileUpload(AttachFileVO file);


}
