package com.pablo.driveflow_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "PageResponse", description = "Standardized paginated response")
public class PageResponse<T> {

    @Schema(description = "List of items")
    private List<T> data;

    @Schema(example = "0", description = "Current page number (starting at 0)")
    private Integer page;

    @Schema(example = "20", description = "Number of items per page")
    private Integer pageSize;

    @Schema(example = "1", description = "Total number of pages")
    private Integer totalPages;

    @Schema(example = "15", description = "Total number of elements")
    private Long totalElements;

    public static <T> PageResponse<T> of(List<T> data, int page, int pageSize, int totalPages, long totalElements) {
        return PageResponse.<T>builder()
                .data(data)
                .page(page)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build();
    }
}

