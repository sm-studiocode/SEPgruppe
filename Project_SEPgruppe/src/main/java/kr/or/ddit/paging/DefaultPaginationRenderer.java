package kr.or.ddit.paging;

import org.springframework.stereotype.Component;

@Component
public class DefaultPaginationRenderer implements PaginationRenderer {

    @Override
    public String renderPagination(PaginationInfo paging, String fnName) {
        int startPage = paging.getStartPage();
        int endPage = paging.getEndPage();
        int totalPage = paging.getTotalPage();
        int blockSize = paging.getBlockSize();
        int currentPage = paging.getCurrentPage();

        if (endPage > totalPage) {
            endPage = totalPage;
        }

        // Pagination HTML
        StringBuilder html = new StringBuilder();

        // Start of pagination container (Bootstrap style)
        html.append("<div class='card-body'>")
            .append("<div class='demo'>")
            .append("<ul class='pagination pg-primary'>");

        // Previous button (이전)
        if (startPage > blockSize) {
            html.append(
                String.format("<li class='page-item'><a class='page-link' href='javascript:void(0);' onclick='%s(%d);' aria-label='Previous'><span aria-hidden='true'>&laquo;</span></a></li>", fnName, startPage - blockSize)
            );
        } else {
            html.append("<li class='page-item disabled'><a class='page-link' href='#' aria-label='Previous'><span aria-hidden='true'>&laquo;</span></a></li>");
        }

        // Page number links
        for (int page = startPage; page <= endPage; page++) {
            if (page == currentPage) {
                html.append(String.format("<li class='page-item active'><a class='page-link' href='#'>%d</a></li>", page));
            } else {
                html.append(String.format("<li class='page-item'><a class='page-link' href='javascript:void(0);' onclick='%s(%d);'>%d</a></li>", fnName, page, page));
            }
        }

        // Next button (다음)
        if (endPage < totalPage) {
            html.append(
                String.format("<li class='page-item'><a class='page-link' href='javascript:void(0);' onclick='%s(%d);' aria-label='Next'><span aria-hidden='true'>&raquo;</span></a></li>", fnName, endPage + 1)
            );
        } else {
            html.append("<li class='page-item disabled'><a class='page-link' href='#' aria-label='Next'><span aria-hidden='true'>&raquo;</span></a></li>");
        }

        // End of pagination container
        html.append("</ul>")
            .append("</div>")
            .append("</div>");

        return html.toString();
    }
}
