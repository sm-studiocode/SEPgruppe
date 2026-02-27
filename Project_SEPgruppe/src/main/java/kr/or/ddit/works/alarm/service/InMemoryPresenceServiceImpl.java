package kr.or.ddit.works.alarm.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InMemoryPresenceServiceImpl implements PresenceService {

    // empId -> lastSeenMillis
    private final Map<String, Long> lastSeenMap = new ConcurrentHashMap<>();

    @Value("${alarm.presence.ttlSeconds:90}")
    private long ttlSeconds;

    @Override
    public void touch(String empId) {
        if (empId == null || empId.isBlank()) return;
        lastSeenMap.put(empId, System.currentTimeMillis());
    }

    @Override
    public boolean isOnline(String empId) {
        if (empId == null || empId.isBlank()) return false;
        Long last = lastSeenMap.get(empId);
        if (last == null) return false;
        long diffMs = System.currentTimeMillis() - last;
        return diffMs <= (ttlSeconds * 1000L);
    }
}