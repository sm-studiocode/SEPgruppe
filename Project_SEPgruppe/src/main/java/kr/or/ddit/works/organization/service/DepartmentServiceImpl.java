package kr.or.ddit.works.organization.service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.works.mybatis.mappers.DepartmentMapper;
import kr.or.ddit.works.mybatis.mappers.EmployeeMapper;
import kr.or.ddit.works.organization.vo.DepartmentVO;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentMapper mapper;

    @Autowired
    private EmployeeMapper empMapper;

    // 🔥 셀 타입 강제변환 대신 문자열 추출 전용 포맷터
    private final DataFormatter formatter = new DataFormatter();


    @Override
    public List<DepartmentVO> selectListAllDepartment(String companyNo) {
        return mapper.selectListAllDepartment(companyNo);
    }

    @Override
    public int addDepartment(DepartmentVO dept) {
        return mapper.insertDepartment(dept);
    }

    @Override
    public int deleteDepartment(String companyNo, String deptCd) {
        return mapper.deleteDepartment(companyNo, deptCd);
    }

    @Override
    public int updateDepartmentField(DepartmentVO dept) {

        String managerEmpId = mapper.selectManagerDeptCd(dept.getDeptCd(), dept.getCompanyNo());

        int result = mapper.updateDepartmentField(dept);

        // 기존 부서장 제거
        if (managerEmpId != null && !managerEmpId.equals(dept.getManagerEmpId())) {
            empMapper.clearDeptCd(managerEmpId);
        }

        // 새 부서장 설정
        if (dept.getManagerEmpId() != null && !dept.getManagerEmpId().isEmpty()) {
            empMapper.updateDeptCd(dept.getManagerEmpId(), dept.getDeptCd());
        }

        return result;
    }

    @Override
    public List<DepartmentVO> parseExcel(MultipartFile file, String companyNo) throws Exception {

        List<DepartmentVO> list = new ArrayList<>();

        try (InputStream is = file.getInputStream()) {

            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            int startRowIndex = -1;

            // 🔍 "부서코드" 헤더 찾기
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                for (Cell cell : row) {
                    if ("부서코드".equals(formatter.formatCellValue(cell).trim())) {
                        startRowIndex = i + 1;
                        break;
                    }
                }

                if (startRowIndex != -1) break;
            }

            if (startRowIndex == -1) {
                throw new IllegalArgumentException("엑셀 파일에서 '부서코드' 헤더를 찾을 수 없습니다.");
            }

            // 📄 실제 데이터 읽기
            for (int i = startRowIndex; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null) continue;

                DepartmentVO dept = new DepartmentVO();
                dept.setDeptCd(getCellValue(row.getCell(0)));
                dept.setParentDeptCd(getCellValue(row.getCell(1)));
                dept.setDeptName(getCellValue(row.getCell(3)));
                dept.setManagerEmpId(getCellValue(row.getCell(4)));
                dept.setCreateAt(LocalDate.now().toString());
                dept.setCompanyNo(companyNo);

                if (dept.getDeptCd() != null && !dept.getDeptCd().isEmpty()) {
                    list.add(dept);
                }
            }
        }

        return list;
    }

    // 🔥 POI 권장 방식 (타입 상관없이 문자열로 읽음)
    private String getCellValue(Cell cell) {
        if (cell == null) return null;
        return formatter.formatCellValue(cell).trim();
    }

    /**
     * ✅ 2-pass 업서트
     *  - 1차: parentDeptCd 없는 최상위 부서 먼저 처리
     *  - 2차: parentDeptCd 있는 하위 부서 처리
     *
     * 전제:
     *  - DEPARTMENT PK가 (COMPANY_NO, DEPT_CD) 복합키로 구성되어 있어야 함
     *  - DEPARTMENT 부모 FK도 (COMPANY_NO, PARENT_DEPT_CD) -> (COMPANY_NO, DEPT_CD) 형태로 맞춰져 있어야 함
     *  - mapper.upsertDepartment(dept) (MERGE) 가 있어야 함
     */
    @Override
    public int bulkInsertDepartments(List<DepartmentVO> deptList) {

        int count = 0;

        // 0) 안전장치: null/빈 리스트 방어
        if (deptList == null || deptList.isEmpty()) {
            return 0;
        }

        // 1) 1차: 최상위(부모 없는) 먼저 처리
        for (DepartmentVO dept : deptList) {
            if (dept == null) continue;

            // deptCd 없으면 스킵
            if (isBlank(dept.getDeptCd())) {
                dept.setStatus("실패: 부서코드(deptCd) 없음");
                continue;
            }

            // parentDeptCd가 있으면 2차에서 처리
            if (!isBlank(dept.getParentDeptCd())) continue;

            try {
                // ✅ MERGE (있으면 UPDATE, 없으면 INSERT)
                mapper.upsertDepartment(dept);
                dept.setStatus("업서트 성공(상위)");
                count++;
            } catch (Exception e) {
                dept.setStatus("실패: " + e.getMessage());
            }
        }

        // 2) 2차: 하위(부모 있는) 처리
        for (DepartmentVO dept : deptList) {
            if (dept == null) continue;

            if (isBlank(dept.getDeptCd())) {
                // 1차에서 이미 status 찍혔을 수도 있음
                if (isBlank(dept.getStatus())) {
                    dept.setStatus("실패: 부서코드(deptCd) 없음");
                }
                continue;
            }

            // parentDeptCd 없는 애는 1차에서 끝났으니 스킵
            if (isBlank(dept.getParentDeptCd())) continue;

            try {
                // 부모가 DB에 없으면 FK로 터질 수 있음 → 이 경우 실패 status로 남김
                mapper.upsertDepartment(dept);
                dept.setStatus("업서트 성공(하위)");
                count++;
            } catch (Exception e) {
                dept.setStatus("실패: " + e.getMessage());
            }
        }

        return count;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
