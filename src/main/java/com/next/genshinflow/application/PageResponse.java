package com.next.genshinflow.application;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageResponse<T> {

    private List<T> content;
    @Schema(description = "비어있는지 여부", example = "false")
    private boolean empty;
    @Schema(description = "첫번째 페이지 여부", example = "false")
    private boolean first;
    @Schema(description = "마지막 페이지 여부", example = "false")
    private boolean last;
    @Schema(description = "페이지 번호", example = "1")
    private int number;
    @Schema(description = "해당 페이지의 항목 갯수", example = "10")
    private long numberOfElements;
    @Schema(description = "요청한 페이지 항목 갯수", example = "10")
    private int size;
    private long totalElements;
    private long totalPages;

    protected PageResponse() {
    }

    private PageResponse(List<T> content, long totalElements, long totalPages) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.empty = page.isEmpty();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.number = page.getNumber();
        this.numberOfElements = page.getNumberOfElements();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(page);
    }

    public static <T> PageResponse<T> of(List<T> content, long totalElements, long totalPages) {
        return new PageResponse<>(content, totalElements, totalPages);
    }
}
