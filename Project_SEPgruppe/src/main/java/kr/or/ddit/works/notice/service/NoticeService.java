package kr.or.ddit.works.notice.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.paging.PaginationInfo;
import kr.or.ddit.paging.SimpleCondition;
import kr.or.ddit.works.attachFile.vo.AttachFileVO;
import kr.or.ddit.works.notice.vo.NoticeDetailDTO;
import kr.or.ddit.works.notice.vo.NoticeFormDTO;
import kr.or.ddit.works.notice.vo.NoticeListRowDTO;
import kr.or.ddit.works.notice.vo.NoticeSearchCondition;
import kr.or.ddit.works.notice.vo.NoticeVO;
import kr.or.ddit.works.organization.vo.DepartmentVO;

public interface NoticeService {

	/**
	 * 공지사항 전체 조회 (페이징포함)
	 * @param paging
	 * @return
	 */
	public List<NoticeListRowDTO> selectAllNotice(PaginationInfo<NoticeSearchCondition> paging, String companyNo);

	public NoticeVO basicSelectAllWithCompany(NoticeVO notice);

	/**
	 * 페이징 처리를 위한 전체 레코드수 조회
	 * @return
	 */
	public int selectAllNoticeTotalRecord(PaginationInfo<NoticeSearchCondition> paging);

	/**
	 * 공지사항 등록
	 * @param notice
	 * @return
	 */
	public boolean insertNotice(NoticeVO notice);

	/**
	 * 임시저장 글 불러오기
	 * @param empId
	 * @return
	 */
	public List<NoticeVO> isDraftList(String empId);

	/**
	 * 임시저장 글 개수 가져오기
	 * @param empId
	 * @return
	 */
	public int isDraftCnt(String empId);

	/**
	 * 공지사항 삭제
	 * @param empid
	 * @return
	 */
	public boolean deleteNotice(Map<String, Object> params);

	/**
	 * 공지사항 수정
	 * @param params
	 * @return
	 */
	public boolean updateNotice(NoticeVO notice);

	/**
	 * 공지사항 조회수 증가
	 * @param noticeNo
	 * @return
	 */
	public int noticeViewCnt(int noticeNo);

	/**
	 * 첨부파일 다운로드
	 * @param attachFileNo
	 * @return
	 */
	public AttachFileVO selectByFileNo(String attachFileNo);

	public List<NoticeVO> selectRecentNoticeList(String companyNo);

	/**
	 * 공지사항 등록 권한을 위한 로그인 계정 조회
	 * @param empId
	 * @return
	 */
	public DepartmentVO selectLogin(String managerEmpId);

	public NoticeDetailDTO getNoticeDetailWithCompany(int noticeNo, String companyNo, boolean increaseViewCnt);

	public void createNoticeWithFilesAndAlarm(NoticeFormDTO notice, long fileGroupNo);

	public void updateNoticeWithFiles(int noticeNo, NoticeFormDTO notice, long fileGroupNo);

	public void deleteNoticesWithAuth(String noticeNoStr, String empId, String companyNo);

	public byte[] buildNoticeExcelBytes(String companyNo, String deptCd, String category, SimpleCondition condition);
}