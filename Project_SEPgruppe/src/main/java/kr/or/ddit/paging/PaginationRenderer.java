package kr.or.ddit.paging;

/**
 *  {@link PaginationInfo} 내의 프로퍼티를 이용해
 *   페이지 네비게이션 링크를 동적으로 생성.
 */
public interface PaginationRenderer {
	public default String renderPagination(PaginationInfo paging) {
		return renderPagination(paging, "fnPaging");
	}
	public String renderPagination(PaginationInfo paging, String fnName);
}
