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

    @Override
    public int bulkInsertDepartments(List<DepartmentVO> deptList) {

        int count = 0;

        for (DepartmentVO dept : deptList) {

            try {
                DepartmentVO existing = mapper.selectDepartmentByCode(dept.getDeptCd(), dept.getCompanyNo());

                if (existing != null) {
                    mapper.updateDepartment(dept);
                    dept.setStatus("수정됨");
                } else {
                    mapper.insertDepartment(dept);
                    dept.setStatus("신규 등록");
                }

                count++;

            } catch (Exception e) {
                dept.setStatus("실패: " + e.getMessage());
            }
        }

        return count;
    }
}
