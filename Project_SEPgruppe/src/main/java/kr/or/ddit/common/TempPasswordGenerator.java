package kr.or.ddit.common;

import java.security.SecureRandom;


/**
 * 임시 비밀번호 만들어주는 유틸 클래스
 */
public final class TempPasswordGenerator {

	// 대문자 + 소문자 + 숫자만 사용
	// 특수문자 사용하지 않음
    private static final String CHARS =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    // 임시 비밀번호 길이 설정
    private static final int DEFAULT_LENGTH = 12;

    // 난수 생성기
    private static final SecureRandom random = new SecureRandom();

    private TempPasswordGenerator() {}

    // 기본 비밀번호 생성 메서드
    public static String generate() {
        return generate(DEFAULT_LENGTH);
    }

    // 길이 지정 생성
    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = random.nextInt(CHARS.length());
            sb.append(CHARS.charAt(idx));
        }
        return sb.toString();
    }
}
