package kr.or.ddit.works.alarm.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OneSignalService {

    @Value("${onesignal.restApiKey}")
    private String restApiKey;

    @Value("${onesignal.appId}")
    private String appId;

    @Value("${onesignal.apiUrl:https://onesignal.com/api/v1/notifications}")
    private String apiUrl;

    public void sendNotification(String message, List<String> playerIds) {
        if (playerIds == null || playerIds.isEmpty()) {
            log.debug("OneSignal skip: playerIds empty");
            return;
        }

        try {
        	ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> payload = new HashMap<>();
            payload.put("app_id", appId);

            Map<String, String> contents = new HashMap<>();
            contents.put("en", message); 
            payload.put("contents", contents);

            payload.put("include_player_ids", playerIds);

            String jsonPayload = mapper.writeValueAsString(payload);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Basic " + restApiKey);

            HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            log.info("OneSignal Response: {} / {}", response.getStatusCodeValue(), response.getBody());

        } catch (Exception e) {
            log.error("OneSignal sendNotification error", e);
        }
    }
}
