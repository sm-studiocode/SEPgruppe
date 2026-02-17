package kr.or.ddit.works.alarm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import kr.or.ddit.works.alarm.service.AlarmService;
import kr.or.ddit.works.alarm.vo.AlarmHistoryVO;
import kr.or.ddit.works.alarm.vo.AlarmNotificationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/alarm")
@Controller
@Slf4j
public class GWAlarmController {

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("")
    public String alarmHome(Authentication authentication, Model model) {
        String userId = getUserId(authentication);
        model.addAttribute("userId", userId);
        return "gw:alarm/alarmHome";
    }

    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<List<AlarmHistoryVO>> list(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "N") String onlyUnread,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer limit
    ) {
        String userId = getUserId(authentication);
        List<AlarmHistoryVO> data = alarmService.getAlarmList(userId, onlyUnread, offset, limit);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<Integer> count(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "N") String onlyUnread
    ) {
        String userId = getUserId(authentication);
        int cnt = alarmService.getAlarmCount(userId, onlyUnread);
        return ResponseEntity.ok(cnt);
    }

    @GetMapping("/unreadCount")
    @ResponseBody
    public ResponseEntity<Integer> unreadCount(Authentication authentication) {
        String userId = getUserId(authentication);
        int cnt = alarmService.getUnreadCount(userId);
        return ResponseEntity.ok(cnt);
    }

    @PutMapping("/read/{alarmNo}")
    @ResponseBody
    public ResponseEntity<Void> readOne(
            Authentication authentication,
            @PathVariable("alarmNo") Long alarmNo
    ) {
        String userId = getUserId(authentication);
        int updated = alarmService.readAlarm(alarmNo, userId);
        return updated > 0 ? ResponseEntity.ok().build()
                           : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/readAll")
    @ResponseBody
    public ResponseEntity<Void> readAll(Authentication authentication) {
        String userId = getUserId(authentication);
        alarmService.readAll(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/subscription")
    @ResponseBody
    public ResponseEntity<Void> upsertSubscription(
            Authentication authentication,
            @RequestBody AlarmNotificationVO vo
    ) {
        String userId = getUserId(authentication);
        vo.setEmpId(userId); // DB 컬럼/VO가 empId로 되어있으니 여기만 empId에 userId 넣어줌(수신자ID)
        int r = alarmService.upsertSubscription(vo);
        return r > 0 ? ResponseEntity.ok().build()
                     : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @DeleteMapping("/subscription")
    @ResponseBody
    public ResponseEntity<Void> deleteSubscription(Authentication authentication) {
        String userId = getUserId(authentication);
        int r = alarmService.deleteSubscriptionByEmpId(userId);
        return r > 0 ? ResponseEntity.ok().build()
                     : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("/send/ws")
    @ResponseBody
    public ResponseEntity<Void> sendWsAlarm(@RequestBody AlarmHistoryVO alarm) {
        if (alarm == null || alarm.getEmpId() == null || alarm.getEmpId().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        sendWs("/topic/alarm/" + alarm.getEmpId(), alarm);
        return ResponseEntity.ok().build();
    }

    private void sendWs(String destination, Object payload) {
        try {
            messagingTemplate.convertAndSend(destination, payload);
        } catch (Exception e) {
            log.error("WebSocket send fail: dest={}", destination, e);
        }
    }

    private String getUserId(Authentication authentication) {
        return authentication == null ? null : authentication.getName();
    }
}
