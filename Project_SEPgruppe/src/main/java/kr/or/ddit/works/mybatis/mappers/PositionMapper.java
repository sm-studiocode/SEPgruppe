package kr.or.ddit.works.mybatis.mappers;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import kr.or.ddit.works.organization.vo.PositionVO;

@Mapper
public interface PositionMapper {

    public List<PositionVO> selectPositionList();

    public String selectPositionName(@Param("positionCd") String positionCd);

    public int deletePositions(@Param("positionCds") List<String> positionCds);

    public List<PositionVO> selectPositionListCount();

    public int insertPosition(PositionVO positionVO);

    public int updateSortOrder(@Param("positionCd") String positionCd, @Param("sortOrder") int sortOrder);
}
