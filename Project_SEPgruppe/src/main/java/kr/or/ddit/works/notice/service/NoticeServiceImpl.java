package kr.or.ddit.works.notice.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.paging.PaginationInfo;
import kr.or.ddit.paging.SimpleCondition;
import kr.or.ddit.works.alarm.service.AlarmSenderService;
import kr.or.ddit.works.attachFile.service.AttachFileService;
import kr.or.ddit.works.attachFile.vo.AttachFileVO;
import kr.or.ddit.works.mybatis.mappers.AttachCommonMapper; // ✅ 추가
import kr.or.ddit.works.mybatis.mappers.NoticeMapper;
import kr.or.ddit.works.mybatis.mappers.OrganizationMapper;
import kr.or.ddit.works.notice.vo.NoticeDetailDTO;
import kr.or.ddit.works.notice.vo.NoticeFormDTO;
import kr.or.ddit.works.notice.vo.NoticeListRowDTO;
import kr.or.ddit.works.notice.vo.NoticeSearchCondition;
import kr.or.ddit.works.notice.vo.NoticeVO;
import kr.or.ddit.works.organization.vo.DepartmentVO;
import kr.or.ddit.works.organization.vo.EmployeeVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NoticeServiceImpl implements NoticeService {

	@Autowired
	private NoticeMapper mapper;

	@Autowired
	private AttachCommonMapper attachMapper; // ✅ 추가 (공통 파일 mapper)

	@Autowired
	private AttachFileService attachFileService;

	@Autowired
	private OrganizationMapper organiMapper;

	@Autowired
	private AlarmSenderService alarmSenderService;

	@Override
	public List<NoticeListRowDTO> selectAllNotice(PaginationInfo<NoticeSearchCondition> paging, String companyNo) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("startRow", paging.getStartRow());
		paramMap.put("endRow", paging.getEndRow());
		paramMap.put("simpleCondition", paging.getSimpleCondition());
		paramMap.put("detailCondition", paging.getDetailCondition());
		paramMap.put("companyNo", companyNo);
		return mapper.selectAllNotice(paramMap);
	}

	@Override
	public int selectAllNoticeTotalRecord(PaginationInfo<NoticeSearchCondition> paging) {
		return mapper.selectAllNoticeTotalRecord(paging);
	}

	@Override
	public boolean insertNotice(NoticeVO notice) {
		return mapper.insertNotice(notice) > 0;
	}

	@Override
	public List<NoticeVO> isDraftList(String empId) {
		return mapper.isDraftList(empId);
	}

	@Override
	public int isDraftCnt(String empId) {
		return mapper.isDraftCnt(empId);
	}

	@Override
	public boolean deleteNotice(Map<String, Object> params) {
		return mapper.deleteNotice(params) > 0;
	}

	@Override
	public boolean updateNotice(NoticeVO notice) {
		return mapper.updateNotice(notice) > 0;
	}

	@Override
	public int noticeViewCnt(int noticeNo) {
		return mapper.noticeViewCnt(noticeNo);
	}

	/**
	 * ✅ (기존 public 유지)
	 * 기존엔 NoticeMapper.selectByFileNo를 탔지만,
	 * 이제 공통 AttachCommonMapper로 위임해서 컨트롤러 수정 없이 그대로 사용 가능.
	 */
	@Override
	public AttachFileVO selectByFileNo(String attachFileNo) {
		return attachMapper.selectAttachFileByNo(attachFileNo);
	}

	@Override
	public List<NoticeVO> selectRecentNoticeList(String companyNo) {
		return mapper.selectRecentNoticeList(companyNo);
	}

	@Override
	public NoticeVO basicSelectAllWithCompany(NoticeVO notice) {
		return mapper.basicSelectAllWithCompany(notice);
	}

	@Override
	public DepartmentVO selectLogin(String managerEmpId) {
		return mapper.selectLogin(managerEmpId);
	}

	private List<AttachFileVO> buildAttachFileList(List<MultipartFile> uploadFiles, String empId, long fileGroupNo) {
		List<AttachFileVO> fileList = new ArrayList<>();
		if (uploadFiles == null || uploadFiles.isEmpty()) return fileList;

		for (MultipartFile file : uploadFiles) {
			if (file == null || file.isEmpty()) continue;

			AttachFileVO attachFile = new AttachFileVO();
			attachFile.setFileGroupNo(fileGroupNo);
			attachFile.setAttachFile(file);
			attachFile.setEmpId(empId);
			attachFile.setAttachFileDate(LocalDate.now().toString());
			attachFile.setAttachFileStatus("Y");

			if (attachFile.getAttachFileName() != null) {
				fileList.add(attachFile);
			}
		}
		return fileList;
	}

	/**
	 * ✅ 공지 등록 + 파일 메타 저장 + 게시글-파일 매핑 + 실제 파일 저장 + 알람
	 */
	@Override
	@Transactional
	public void createNoticeWithFilesAndAlarm(NoticeFormDTO form, long fileGroupNo) {
		int noticeNo = mapper.seqNotice();

		NoticeVO notice = new NoticeVO();
		notice.setNoticeNo(noticeNo);
		notice.setEmpId(form.getEmpId());
		notice.setCompanyNo(form.getCompanyNo());
		notice.setNoticeCategory(form.getNoticeCategory());
		notice.setNoticeTitle(form.getNoticeTitle());
		notice.setNoticeContent(form.getNoticeContent());
		notice.setIsDraft(form.getIsDraft());

		mapper.insertNotice(notice);

		// 1) 업로드 파일 리스트 구성
		List<AttachFileVO> fileList = buildAttachFileList(form.getUploadFiles(), form.getEmpId(), fileGroupNo);

		// 2) 파일이 있으면: ATTACH_FILE + POST_ATTACH_FILE 저장 + 디스크 저장
		if (!fileList.isEmpty()) {

			// ✅ ATTACH_FILE_NO 생성 (NOTICE_ + SEQ) : 기존 포맷 유지 가능
			for (AttachFileVO f : fileList) {
				long seq = attachMapper.seqAttachFile();
				f.setAttachFileNo("NOTICE_" + seq);
			}

			// ✅ ATTACH_FILE 메타데이터 INSERT
			Map<String, Object> fileMap = new HashMap<>();
			fileMap.put("fileList", fileList);
			attachMapper.insertAttachFiles(fileMap);

			// ✅ 실제 파일 저장 (디스크)
			for (AttachFileVO f : fileList) {
				attachFileService.fileUpload(f);
			}

			// ✅ POST_ATTACH_FILE 매핑 INSERT
			List<String> fileNos = new ArrayList<>();
			for (AttachFileVO f : fileList) fileNos.add(f.getAttachFileNo());
			attachMapper.insertNoticePostAttachFiles(noticeNo, fileNos);
		}

		// 3) 알람 (controller 호출 X, sender 서비스 사용)
		List<EmployeeVO> allEmp = organiMapper.selectAllEmployees(form.getCompanyNo());
		for (EmployeeVO emp : allEmp) {
			alarmSenderService.sendNoticeAlarm(notice, emp.getEmpId());
		}
	}

	/**
	 * ✅ 공지 수정 + 파일 삭제/추가 처리
	 */
	@Override
	@Transactional
	public void updateNoticeWithFiles(int noticeNo, NoticeFormDTO form, long fileGroupNo) {

		NoticeVO notice = new NoticeVO();
		notice.setNoticeNo(noticeNo);
		notice.setEmpId(form.getEmpId());
		notice.setCompanyNo(form.getCompanyNo());
		notice.setNoticeCategory(form.getNoticeCategory());
		notice.setNoticeTitle(form.getNoticeTitle());
		notice.setNoticeContent(form.getNoticeContent());
		notice.setIsDraft(form.getIsDraft());

		mapper.updateNotice(notice);

		// 1) 삭제할 파일 처리 (attachFileNo="A,B,C" 형태)
		String attachFileNo = form.getAttachFileNo();
		if (attachFileNo != null && !attachFileNo.isEmpty()) {
			String[] fileNosToDelete = attachFileNo.split(",");
			for (String fileNoToDelete : fileNosToDelete) {
				if (fileNoToDelete == null || fileNoToDelete.isBlank()) continue;

				String fno = fileNoToDelete.trim();

				// ✅ 매핑(POST_ATTACH_FILE) 먼저 삭제
				attachMapper.deleteNoticePostAttachFile(noticeNo, fno);

				// ✅ 메타(ATTACH_FILE) 삭제
				attachMapper.deleteAttachFile(fno);

				// ⚠️ 실제 디스크 파일 삭제까지 하고 싶으면
				// attachFileService에 delete 메서드를 추가해서 처리해야 함 (지금은 upload/download만 있음)
			}
		}

		// 2) 추가 업로드 파일 처리
		List<AttachFileVO> fileList = buildAttachFileList(form.getUploadFiles(), form.getEmpId(), fileGroupNo);

		if (!fileList.isEmpty()) {

			for (AttachFileVO f : fileList) {
				long seq = attachMapper.seqAttachFile();
				f.setAttachFileNo("NOTICE_" + seq);
			}

			Map<String, Object> fileMap = new HashMap<>();
			fileMap.put("fileList", fileList);
			attachMapper.insertAttachFiles(fileMap);

			for (AttachFileVO f : fileList) {
				attachFileService.fileUpload(f);
			}

			List<String> fileNos = new ArrayList<>();
			for (AttachFileVO f : fileList) fileNos.add(f.getAttachFileNo());
			attachMapper.insertNoticePostAttachFiles(noticeNo, fileNos);
		}
	}

	@Override
	public NoticeDetailDTO getNoticeDetailWithCompany(int noticeNo, String companyNo, boolean increaseViewCnt) {
		NoticeDetailDTO detail = mapper.selectNoticeDetailWithCompany(noticeNo, companyNo);
		if (detail == null) return null;

		if (increaseViewCnt) {
			mapper.noticeViewCnt(noticeNo);
			if (detail.getNoticeViewCount() == null) detail.setNoticeViewCount(1L);
			else detail.setNoticeViewCount(detail.getNoticeViewCount() + 1);
		}

		// 상세에서 첨부파일 목록 필요하면 여기서 채워 넣을 수 있음
		detail.setFile(attachMapper.selectAttachFilesByNoticeNo(noticeNo));

		return detail;
	}

	@Override
	@Transactional
	public void deleteNoticesWithAuth(String noticeNoStr, String empId, String companyNo) {
		if (noticeNoStr == null || noticeNoStr.isBlank()) return;

		String[] noticeNoArray = noticeNoStr.split(",");
		int[] noticeNos = new int[noticeNoArray.length];
		for (int i = 0; i < noticeNoArray.length; i++) {
			noticeNos[i] = Integer.parseInt(noticeNoArray[i].trim());
		}

		for (int nNo : noticeNos) {
			NoticeVO condition = new NoticeVO();
			condition.setNoticeNo(nNo);
			condition.setCompanyNo(companyNo);

			NoticeVO noticeVo = mapper.basicSelectAllWithCompany(condition);
			if (noticeVo == null) return;
			if (noticeVo.getEmpId() == null || !noticeVo.getEmpId().equals(empId)) return;

			// 공지 삭제 시 첨부파일도 같이 정리
			 List<AttachFileVO> files = attachMapper.selectAttachFilesByNoticeNo(nNo);
			 for (AttachFileVO f : files) {
			     attachMapper.deleteNoticePostAttachFile(nNo, f.getAttachFileNo());
			     attachMapper.deleteAttachFile(f.getAttachFileNo());
			 }
		}

		Map<String, Object> params = new HashMap<>();
		params.put("noticeNo", noticeNos);
		params.put("empId", empId);
		mapper.deleteNotice(params);
	}

	@Override
	public byte[] buildNoticeExcelBytes(String companyNo, String deptCd, String category, SimpleCondition condition) {
		NoticeSearchCondition detail = new NoticeSearchCondition();
		detail.setCompanyNo(companyNo);
		detail.setDeptCd(deptCd);
		detail.setCategory(category);

		PaginationInfo<NoticeSearchCondition> paging = new PaginationInfo<>();
		paging.setSimpleCondition(condition);
		paging.setDetailCondition(detail);

		List<NoticeListRowDTO> noticeList = selectAllNotice(paging, companyNo);

		try (Workbook workbook = new XSSFWorkbook();
			 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

			Sheet sheet = workbook.createSheet("공지사항 목록");
			Row headerRow = sheet.createRow(0);
			String[] columns = {"번호", "제목", "내용", "작성자", "작성일", "조회수"};
			for (int i = 0; i < columns.length; i++) {
				headerRow.createCell(i).setCellValue(columns[i]);
			}

			int rowIdx = 1;
			for (NoticeListRowDTO n : noticeList) {
				Row row = sheet.createRow(rowIdx++);
				row.createCell(0).setCellValue(n.getRnum());
				row.createCell(1).setCellValue(n.getNoticeTitle());
				row.createCell(2).setCellValue("");
				row.createCell(3).setCellValue(n.getEmpNm());
				row.createCell(4).setCellValue(n.getNoticeCreatedAt() == null ? "" : n.getNoticeCreatedAt().toString());
				row.createCell(5).setCellValue(n.getNoticeViewCount() == null ? 0 : n.getNoticeViewCount());
			}

			workbook.write(baos);
			return baos.toByteArray();
		} catch (IOException e) {
			log.error("Excel build failed", e);
			throw new RuntimeException(e);
		}
	}
}