package com.next.genshinflow.application;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Schema(description = "페이지 요청")
public abstract class BasePageRequest {

    @Schema(defaultValue = "0", description = "요청 페이지 번호(시작값 : 0)")
    protected int page = 0;
    @Schema(defaultValue = "10", description = "페이지당 노출되는 항목의 수")
    protected int size = 10;

    public Pageable toPageable() {
        int pageNumber = Integer.max(page, 0);
        int pageSize = Integer.max(size, 1);
        return PageRequest.of(pageNumber, pageSize);
    }
}
