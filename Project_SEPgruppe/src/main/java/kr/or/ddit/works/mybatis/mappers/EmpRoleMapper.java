package kr.or.ddit.works.mybatis.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmpRoleMapper {
    public int upsertAdminRole(@Param("empId") String empId);
    public int deleteRoleByEmpId(@Param("empId") String empId);
}
