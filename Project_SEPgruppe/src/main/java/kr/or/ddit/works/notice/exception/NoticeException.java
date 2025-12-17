package kr.or.ddit.works.notice.exception;

/**
notice에서 RuntimeException 생길 수 있는 경우들

공지 없음 (존재하지 않는 글 조회)

권한 없음 (작성자/관리자 아님)

비공개 공지 접근

이미 삭제된 공지

잘못된 공지 상태 (예약/게시 기간 오류)

필수값 누락 (제목, 내용 없음)

중복 정책 위반 (상단 고정 중복 등)

첨부파일 문제 (파일 그룹 없음, 접근 불가)

잘못된 파라미터 (noticeId null, 음수 등)

즐겨찾기/조회수 처리 중 상태 오류

**/
public class NoticeException extends RuntimeException{

}
