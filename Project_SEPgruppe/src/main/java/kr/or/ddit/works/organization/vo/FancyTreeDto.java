package kr.or.ddit.works.organization.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FancyTreeDto<T> {
    private String key;
    private String title;
    private Boolean folder;   // department면 true
    private Boolean lazy;     // folder면 true
    private T data;           // 추가 데이터(emp/dep VO)
}