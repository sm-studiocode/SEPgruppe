package kr.or.ddit.works.alarm.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.ddit.works.alarm.vo.AlarmHistoryVO;
import kr.or.ddit.works.alarm.vo.AlarmNotificationVO;
import kr.or.ddit.works.mybatis.mappers.AlarmMapper;

@Service
public class AlarmServiceImpl implements AlarmService {

	@Autowired
    private AlarmMapper alarmMapper;
	
	@Autowired
    private OneSignalService oneSignalService;

    // ✅ 기존 메서드: "저장 + (가능하면) OneSignal 푸시"
    @Override
    public int saveAlarm(String receiverId, String message) {

        AlarmHistoryVO alarm = new AlarmHistoryVO();
        alarm.setEmpId(receiverId);
        alarm.setAlarmNm("알림");            // 필요하면 여기서 제목 넣기
        alarm.setAlarmContent(message);
        alarm.setIsAlarmRead("N");           // DB에서 NVL로 처리해도 되지만 명시가 안전
        alarm.setAlarmReadTime(null);        // 읽기 전이라 null

        int inserted = alarmMapper.insertAlarm(alarm);

        // ✅ playerIds(=subscriptionId) 있으면 푸시
        List<String> playerIds = alarmMapper.selectPlayerIdsByEmpId(receiverId);
        if (playerIds != null && !playerIds.isEmpty()) {
            oneSignalService.sendNotification(message, playerIds);
        }

        return inserted;
    }

    @Override
    public int insertAlarm(AlarmHistoryVO alarm) {
        return alarmMapper.insertAlarm(alarm);
    }

    @Override
    public List<AlarmHistoryVO> getAlarmList(String empId, String onlyUnread, Integer offset, Integer limit) {
        Map<String, Object> param = new HashMap<>();
        param.put("empId", empId);
        param.put("onlyUnread", onlyUnread); // "Y"면 미읽음만
        param.put("offset", offset);
        param.put("limit", limit);
        return alarmMapper.selectAlarmList(param);
    }

    @Override
    public int getAlarmCount(String empId, String onlyUnread) {
        Map<String, Object> param = new HashMap<>();
        param.put("empId", empId);
        param.put("onlyUnread", onlyUnread);
        return alarmMapper.selectAlarmCount(param);
    }

    @Override
    public int getUnreadCount(String empId) {
        Map<String, Object> param = new HashMap<>();
        param.put("empId", empId);
        return alarmMapper.selectUnreadCount(param);
    }

    @Override
    public int readAlarm(Long alarmNo, String empId) {
        Map<String, Object> param = new HashMap<>();
        param.put("alarmNo", alarmNo);
        param.put("empId", empId);
        return alarmMapper.updateAlarmRead(param);
    }

    @Override
    public int readAll(String empId) {
        return alarmMapper.updateAlarmReadAll(empId);
    }

    @Override
    public int upsertSubscription(AlarmNotificationVO vo) {
        return alarmMapper.upsertSubscription(vo);
    }

    @Override
    public List<String> getPlayerIdsByEmpId(String empId) {
        return alarmMapper.selectPlayerIdsByEmpId(empId);
    }

    @Override
    public List<String> getPlayerIdsByEmpIds(List<String> empIds) {
        Map<String, Object> param = new HashMap<>();
        param.put("empIds", empIds);
        return alarmMapper.selectPlayerIdsByEmpIds(param);
    }

    @Override
    public int deleteSubscriptionByEmpId(String empId) {
        return alarmMapper.deleteSubscriptionByEmpId(empId);
    }
}
