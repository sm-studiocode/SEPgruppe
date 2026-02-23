package kr.or.ddit.works.attachFile.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.works.attachFile.vo.AttachFileVO;

@Service
public class AttachFileServiceImpl implements AttachFileService {

	// ✅ 업로드 루트 디렉토리(폴더)만 사용하도록 정리
	// - 기존처럼 같은 프로퍼티 키를 String/Resource/File에 동시에 주입하면
	//   환경에 따라 Resource 해석이 꼬일 수 있어서(특히 classpath/URL), File 하나로 통일하는 게 안전함.
	@Value("${file.attachFiles}")
	private File fileFolder;

	// 파일 업로드 메서드
	public void fileUpload(AttachFileVO file) {
		try {
			// AttachFileVO에서 업로드할 파일 가져오기
			MultipartFile noticeFile = file.getAttachFile();

			// 파일이 없을 경우 메서드 종료
			if (noticeFile == null) return;

			// ✅ 파일 객체는 있지만 내용이 비어있는 경우도 종료(폼만 전송된 케이스 방어)
			if (noticeFile.isEmpty()) return;

			// AttachFileVO에서 저장 경로 가져오기
			String filePath = file.getAttachFilePath();

			// ✅ 저장 경로가 비어있으면 저장 불가 (NPE/이상경로 방지)
			if (filePath == null || filePath.isBlank()) {
				throw new IllegalArgumentException("attachFilePath is blank");
			}

			// ✅ 경로 탈출(../) 및 절대경로 방지 (최소 방어)
			// - DB에 상대경로만 저장하는 규칙을 강제하는 게 안전함
			String normalized = filePath.replace("\\", "/");
			if (normalized.contains("..") || normalized.startsWith("/") || normalized.matches("^[A-Za-z]:/.*")) {
				throw new IllegalArgumentException("Invalid attachFilePath: " + filePath);
			}

			// 저장될 실제 파일 경로 객체 생성
			File destFile = new File(fileFolder, filePath);

			// 저장 폴더가 존재하지 않으면 생성
			if (!destFile.getParentFile().exists()) {
				destFile.getParentFile().mkdirs();
			}

			// 업로드된 파일의 입력 스트림을 통해 실제 파일로 저장
			FileCopyUtils.copy(noticeFile.getInputStream(), new FileOutputStream(destFile));

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 첨부파일 다운로드용 리소스를 반환하는 메서드
	 *
	 * @param file 다운로드할 파일 정보를 담고 있는 객체
	 * @return Resource 객체 (파일 스트림), 파일이 존재하지 않으면 null 반환
	 * @throws FileNotFoundException 파일이 실제로 존재하지 않을 경우 발생
	 */
	@Override
	public Resource getAttachFileDownload(AttachFileVO file) throws FileNotFoundException {

		// 파일 저장 경로 가져오기
		String storedFileName = file.getAttachFilePath();

		// ✅ 저장 경로가 없으면 다운로드 불가
		if (storedFileName == null || storedFileName.isBlank()) {
			return null;
		}

		// ✅ 경로 탈출(../) 및 절대경로 방지 (다운로드도 동일하게 방어)
		String normalized = storedFileName.replace("\\", "/");
		if (normalized.contains("..") || normalized.startsWith("/") || normalized.matches("^[A-Za-z]:/.*")) {
			throw new FileNotFoundException("Invalid attachFilePath: " + storedFileName);
		}

		// 실제 저장된 파일의 전체 경로 설정
		File targetFile = new File(fileFolder, storedFileName);

		// 파일이 존재하지 않으면 null 반환
		if (!targetFile.exists()) {
			return null;
		}

		// ✅ 파일이 폴더면 반환하지 않음(다운로드 안전)
		if (!targetFile.isFile()) {
			return null;
		}

		// 파일이 존재하면 해당파일을 반환
		return new InputStreamResource(new FileInputStream(targetFile));
	}

}