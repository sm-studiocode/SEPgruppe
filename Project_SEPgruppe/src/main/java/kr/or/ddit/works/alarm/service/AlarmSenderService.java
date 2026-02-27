package kr.or.ddit.works.alarm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import kr.or.ddit.works.alarm.vo.AlarmHistoryVO;
import kr.or.ddit.works.mybatis.mappers.AlarmMapper;
import kr.or.ddit.works.notice.vo.NoticeVO;
import lombok.extern.slf4j.Slf4j;

/**
 * 알람 전송(WS) 전용 서비스
 * - Controller가 아닌 Service 계층에서 사용 가능하도록 분리
 */
@Slf4j
@Service
public class AlarmSenderService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private AlarmMapper alarmMapper; // ✅ 추가: 알림함 저장용

    @Autowired
    private OneSignalService oneSignalService; // ✅ 추가: 웹푸시

    @Autowired
    private PresenceService presenceService; // ✅ 추가: 접속중이면 push 스킵

    /**
     * 공지사항 알람 전송
     * @param notice
     * @param receiverEmpId
     */
    public void sendNoticeAlarm(NoticeVO notice, String receiverEmpId) {
        Long categoryNo = alarmMapper.selectAlarmCategoryNoByCd("NOTICE");
        if (categoryNo == null) {
            throw new IllegalStateException("ALARM_CATEGORY_CD=NOTICE not found");
        }

        AlarmHistoryVO alarm = new AlarmHistoryVO();
        alarm.setEmpId(receiverEmpId);
        alarm.setAlarmCategoryNo(categoryNo);

        alarm.setRefNo((long) notice.getNoticeNo());
        alarm.setRefUrl("/notice/" + notice.getNoticeNo());

        alarm.setAlarmNm(notice.getNoticeTitle());
        alarm.setAlarmContent("새 공지: " + notice.getNoticeTitle());
        alarm.setIsAlarmRead("N");
        alarm.setAlarmReadTime(null);

        // ✅ 1) 알림함 저장은 항상
        alarmMapper.insertAlarm(alarm);

        // ✅ 2) 전송은 옵션
        boolean online = presenceService.isOnline(receiverEmpId);

        if (online) {
            sendWs("/topic/notice/" + receiverEmpId, alarm);
            return;
        }

        // offline이면 push(구독 있으면)
        List<String> playerIds = alarmMapper.selectPlayerIdsByEmpId(receiverEmpId);
        if (playerIds != null && !playerIds.isEmpty()) {
            oneSignalService.sendNotification(alarm.getAlarmContent(), playerIds);
        }
    }
    /**
     * ✅ 범용 알림 전송 (다른 기능에서도 재사용)
     * - 저장(DB) + 중복방지(online이면 WS / offline이면 Push)
     */
    public void sendAlarm(String receiverEmpId, String title, String content, Long categoryNo) {
        AlarmHistoryVO alarm = new AlarmHistoryVO();
        alarm.setEmpId(receiverEmpId);
        alarm.setAlarmCategoryNo(categoryNo);
        alarm.setAlarmNm(title != null ? title : "알림");
        alarm.setAlarmContent(content);
        alarm.setIsAlarmRead("N");
        alarm.setAlarmReadTime(null);

        // ✅ 알림함 저장
        alarmMapper.insertAlarm(alarm);

        boolean online = presenceService.isOnline(receiverEmpId);

        if (online) {
            sendWs("/topic/alarm/" + receiverEmpId, alarm);
        } else {
            List<String> playerIds = alarmMapper.selectPlayerIdsByEmpId(receiverEmpId);
            if (playerIds != null && !playerIds.isEmpty()) {
                oneSignalService.sendNotification(content, playerIds);
            }
        }
    }

    /**
     * 공용 WS 전송
     */
    public void sendWs(String destination, Object payload) {
        try {
            messagingTemplate.convertAndSend(destination, payload);
        } catch (Exception e) {
            log.error("WebSocket send fail: dest={}", destination, e);
        }
    }
}