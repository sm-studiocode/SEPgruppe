package kr.or.ddit.works.widget.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"empId", "widgetId"})
public class WidgetPositionVO {
	
	private String empId;
    private String widgetId;
    private int positionNo;
    private String columnName;
    
}
