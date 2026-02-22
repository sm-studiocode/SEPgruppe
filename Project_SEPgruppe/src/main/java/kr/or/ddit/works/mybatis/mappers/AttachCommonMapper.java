package kr.or.ddit.works.mybatis.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.works.attachFile.vo.AttachFileVO;

@Mapper
public interface AttachCommonMapper {

	/**
	 * 첨부파일 시퀀스(ATTACH_FILE_NO 생성용) 가져오기
	 * - 기존 NOTICE 쪽에서 쓰던 ATTACH_FILE_SEQ.NEXTVAL 그대로 사용
	 * @return
	 */
	public long seqAttachFile();

	/**
	 * POST_ATTACH_FILE 시퀀스 가져오기
	 * @return
	 */
	public long seqPostAttachFile();

	/**
	 * 첨부파일 메타데이터(ATTACH_FILE) 일괄 INSERT
	 * - 실제 파일은 DB에 저장하지 않음(경로/메타만 저장)
	 * @param fileMap fileList 포함(Map)
	 * @return
	 */
	public int insertAttachFiles(Map<String, Object> fileMap);

	/**
	 * 공지사항(NOTICE) - 첨부파일 매핑(POST_ATTACH_FILE) 일괄 INSERT
	 * @param noticeNo
	 * @param fileNos
	 * @return
	 */
	public int insertNoticePostAttachFiles(@Param("noticeNo") int noticeNo, @Param("fileNos") List<String> fileNos);

	/**
	 * 공지사항(NOTICE) - 첨부파일 매핑(POST_ATTACH_FILE) 삭제
	 * - 공지 수정에서 특정 파일만 지울 때 사용
	 * @param noticeNo
	 * @param attachFileNo
	 * @return
	 */
	public int deleteNoticePostAttachFile(@Param("noticeNo") int noticeNo, @Param("attachFileNo") String attachFileNo);

	/**
	 * 첨부파일(ATTACH_FILE) 삭제
	 * - DB 메타데이터 삭제 (실제 파일 삭제는 서비스에서 별도 처리 필요)
	 * @param attachFileNo
	 * @return
	 */
	public int deleteAttachFile(String attachFileNo);

	/**
	 * 첨부파일 단건 조회(다운로드용)
	 * @param attachFileNo
	 * @return
	 */
	public AttachFileVO selectAttachFileByNo(@Param("attachFileNo") String attachFileNo);

	/**
	 * 특정 공지사항에 연결된 첨부파일 목록 조회(상세화면용)
	 * @param noticeNo
	 * @return
	 */
	public List<AttachFileVO> selectAttachFilesByNoticeNo(@Param("noticeNo") int noticeNo);
}