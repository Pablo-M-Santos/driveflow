package com.pablo.driveflow_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "PageResponse", description = "Resposta paginada padronizada")
public class PageResponse<T> {

    @Schema(description = "Lista de dados")
    private List<T> data;

    @Schema(example = "0", description = "Número da página atual (começando em 0)")
    private Integer page;

    @Schema(example = "20", description = "Quantidade de itens por página")
    private Integer pageSize;

    @Schema(example = "1", description = "Total de páginas")
    private Integer totalPages;

    @Schema(example = "15", description = "Total de elementos")
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

