package kr.or.ddit.works.alarm.service;

import java.util.List;

import kr.or.ddit.works.alarm.vo.AlarmHistoryVO;
import kr.or.ddit.works.alarm.vo.AlarmNotificationVO;

public interface AlarmService {

    // ✅ 기존: 최소 기능(저장 + 선택적으로 푸시/실시간 전송)
    public int saveAlarm(String receiverId, String message);

    // 1) 알림 저장(상세)
    public int insertAlarm(AlarmHistoryVO alarm);

    // 2) 알림함 목록/카운트
    public List<AlarmHistoryVO> getAlarmList(String empId, String onlyUnread, Integer offset, Integer limit);
    public int getAlarmCount(String empId, String onlyUnread);
    public int getUnreadCount(String empId);

    // 3) 읽음 처리
    public int readAlarm(Long alarmNo, String empId);
    public int readAll(String empId);

    // 4) OneSignal 구독정보
    public int upsertSubscription(AlarmNotificationVO vo);
    public List<String> getPlayerIdsByEmpId(String empId);
    public List<String> getPlayerIdsByEmpIds(List<String> empIds);
    public int deleteSubscriptionByEmpId(String empId);
}
