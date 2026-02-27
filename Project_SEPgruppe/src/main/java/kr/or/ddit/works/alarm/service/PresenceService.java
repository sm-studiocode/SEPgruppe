package kr.or.ddit.works.alarm.service;

public interface PresenceService {

    /** 접속중 표시(마지막 활동 시간 갱신) */
    public void touch(String empId);

    /** 최근 활동이 있으면 true (예: 90초 이내) */
    public boolean isOnline(String empId);
}