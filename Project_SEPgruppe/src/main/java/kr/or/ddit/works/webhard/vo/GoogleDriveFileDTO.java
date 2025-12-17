package kr.or.ddit.works.webhard.vo;

import com.google.api.client.util.DateTime;

import lombok.Data;

/**
 * DB 저장용이 아닌, 데이터 전달용 DTO
 */

@Data
public class GoogleDriveFileDTO {
    private String id;
    private String name;
    private DateTime createdTime; 
    private String fileExtension;
    private long size;
    private String webViewLink;
}
