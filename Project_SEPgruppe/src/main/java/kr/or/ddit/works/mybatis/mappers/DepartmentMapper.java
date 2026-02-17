package kr.or.ddit.works.mybatis.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.works.organization.vo.DepartmentVO;

@Mapper
public interface DepartmentMapper {

	public List<DepartmentVO> selectListAllDepartment(@Param("companyNo") String companyNo);

	public int insertDepartment(DepartmentVO dept);

	public int deleteDepartment(@Param("companyNo") String companyNo, @Param("deptCd") String deptCd);

	public int updateDepartmentField(DepartmentVO dept);

	public String selectManagerDeptCd(@Param("deptCd") String deptCd, @Param("companyNo") String companyNo);

	public DepartmentVO selectDepartmentByCode(@Param("deptCd") String deptCd, @Param("companyNo") String companyNo);

	public int upsertDepartment(DepartmentVO dept);
}
