package kr.or.ddit.works.organization.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.ddit.works.mybatis.mappers.PositionMapper;
import kr.or.ddit.works.organization.vo.PositionVO;

@Service
public class PositionServiceImpl implements PositionService {

    @Autowired
    private PositionMapper mapper;

    @Override
    public List<PositionVO> selectPositionList() {
        return mapper.selectPositionList();
    }

    @Override
    public String selectPositionName(String positionCd) {
        return mapper.selectPositionName(positionCd);
    }

    @Override
    public int deletePositions(List<String> positionCds) {
        return mapper.deletePositions(positionCds);
    }

    @Override
    public List<PositionVO> selectPositionListCount() {
        return mapper.selectPositionListCount();
    }

    @Override
    public int insertPosition(PositionVO positionVO) {
        return mapper.insertPosition(positionVO);
    }

    @Override
    public int updateSortOrder(String positionCd, int sortOrder) {
        return mapper.updateSortOrder(positionCd, sortOrder);
    }
}
