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
    private AlarmSenderService alarmSenderService; // ✅ 추가: 중복방지 정책 한 곳으로

    // ✅ 기존 메서드: "저장 + (가능하면) OneSignal 푸시"
    @Override
    public int saveAlarm(String receiverId, String message) {

        // ✅ 중복방지 포함한 공통 전송으로 위임
        alarmSenderService.sendAlarm(receiverId, "알림", message, null);

        // 기존 반환형 유지용 (정확한 inserted가 필요하면 sendAlarm이 insert 결과를 반환하도록 개선 가능)
        return 1;
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