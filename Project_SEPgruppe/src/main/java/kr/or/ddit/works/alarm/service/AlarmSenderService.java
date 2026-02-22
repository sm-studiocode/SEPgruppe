package kr.or.ddit.works.alarm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import kr.or.ddit.works.alarm.vo.AlarmHistoryVO;
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

    /**
     * 공지사항 알람 전송
     * @param notice
     * @param receiverEmpId
     */
    public void sendNoticeAlarm(NoticeVO notice, String receiverEmpId) {
        AlarmHistoryVO alarm = new AlarmHistoryVO();
        alarm.setEmpId(receiverEmpId);
        alarm.setAlarmCategoryNo((long) notice.getNoticeNo());
        alarm.setAlarmNm(notice.getNoticeTitle());

        sendWs("/topic/notice/" + receiverEmpId, alarm);
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