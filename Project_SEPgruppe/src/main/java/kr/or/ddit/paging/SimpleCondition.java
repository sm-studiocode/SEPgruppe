package kr.or.ddit.paging;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleCondition implements Serializable{
	private String searchType;
	private String searchWord;
}
