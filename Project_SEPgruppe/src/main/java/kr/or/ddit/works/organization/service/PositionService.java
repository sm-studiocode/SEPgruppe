package kr.or.ddit.works.organization.service;

import java.util.List;
import kr.or.ddit.works.organization.vo.PositionVO;

public interface PositionService {

    /** 관리자 - 직위 목록 조회 */
    public List<PositionVO> selectPositionList();

    /** 직위명 단건 조회 */
    public String selectPositionName(String positionCd);

    /** 관리자 - 직위 삭제 */
    public int deletePositions(List<String> positionCds);

    /** 직위 + 인원수 포함 목록 */
    public List<PositionVO> selectPositionListCount();

    /** 관리자 - 직위 등록 */
    public int insertPosition(PositionVO positionVO);

    /** 관리자 - 정렬 순서 변경 */
    public int updateSortOrder(String positionCd, int sortOrder);
}
