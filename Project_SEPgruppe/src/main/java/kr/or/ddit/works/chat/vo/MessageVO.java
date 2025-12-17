package kr.or.ddit.works.chat.vo;

import java.io.Serializable;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "msgId")
public class MessageVO implements Serializable{
	
	private Long msgId;         // 메시지 번호
	private String roomId;      // 채팅방 ID
	private String senderEmpId; // 송신자 ID
	private String msgContent;  // 메시지 내용
	private String sendDate;    // 메시지 전송일자
	private String sendTime;    // 메시지 전송 시간
	
}
