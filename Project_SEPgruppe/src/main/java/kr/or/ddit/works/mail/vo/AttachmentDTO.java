package kr.or.ddit.works.mail.vo;

import lombok.Data;

/**
 * DB 저장용이 아닌, 데이터 전달용 DTO
 */

@Data
public class AttachmentDTO {

	private String attachmentId;
    private String filename;
    private String mimeType;
    private String data;
    
}
