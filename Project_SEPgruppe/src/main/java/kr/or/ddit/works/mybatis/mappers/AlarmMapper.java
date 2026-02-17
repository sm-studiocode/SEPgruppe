package kr.or.ddit.works.mybatis.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.or.ddit.works.alarm.vo.AlarmHistoryVO;
import kr.or.ddit.works.alarm.vo.AlarmNotificationVO;

@Mapper
public interface AlarmMapper {

    // 1) 알림 히스토리 저장
    public int insertAlarm(AlarmHistoryVO alarm);

    // 2) 알림함 목록 (전체/미읽음/페이징)
    public List<AlarmHistoryVO> selectAlarmList(Map<String, Object> param);
    public int selectAlarmCount(Map<String, Object> param);

    // 3) 미읽음 개수 (뱃지)
    public int selectUnreadCount(Map<String, Object> param);

    // 4) 읽음 처리
    public int updateAlarmRead(Map<String, Object> param);

    // 전체 읽음
    public int updateAlarmReadAll(String empId);

    // 5) OneSignal 구독정보 (playerId/subscriptionId)
    public int upsertSubscription(AlarmNotificationVO vo);

    // 한 사람의 playerId 목록 (보통 1개지만, 기기 여러개면 여러개일 수 있음)
    public List<String> selectPlayerIdsByEmpId(String empId);

    // 여러 사람 empIds -> playerIds 조회 (bulk push)
    public List<String> selectPlayerIdsByEmpIds(Map<String, Object> param);

    // 로그아웃/탈퇴 등 구독 삭제
    public int deleteSubscriptionByEmpId(String empId);
}
