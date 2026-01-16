package com.proyect1.user.infra.in.web.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * DTO para la respuesta paginada genérica.
 * Contiene el contenido de la página actual y la información de paginación.
 *
 * @param <T> Tipo de los elementos contenidos en la página
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta paginada genérica")
public class PaginationResponse<T> {

    /**
     * Contenido de la página actual.
     * Lista de elementos del tipo T.
     */
    @Schema(description = "Contenido de la página actual")
    private List<T> content;


    /**
     * Información sobre la paginación.
     * Incluye detalles como número de página, tamaño, total de elementos, etc.
     */
    @Schema(description = "Información sobre la paginación")
    private PaginationInfo pagination;


    /**
     * DTO para la información de paginación.
     * Contiene detalles como número de página, tamaño, total de elementos, etc.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Detalles de la paginación")
    public static class PaginationInfo {

        /**
         * Número de la página actual.
         * Ejemplo: 0
         */
        @Schema(description = "Número de la página actual", example = "0")
        private int page;


        /**
         * Cantidad de elementos por página.
         * Ejemplo: 10
         */
        @Schema(description = "Cantidad de elementos por página", example = "10")
        private int size;


        /**
         * Cantidad total de elementos en todas las páginas.
         * Ejemplo: 125
         */
        @Schema(description = "Cantidad total de elementos en todas las páginas", example = "125")
        private long totalElements;


        /**
         * Cantidad total de páginas.
         * Ejemplo: 13
         */
        @Schema(description = "Cantidad total de páginas", example = "13")
        private int totalPages;


        /**
         * Indica si esta es la primera página.
         * Ejemplo: true
         */
        @Schema(description = "Indica si esta es la primera página", example = "true")
        private boolean first;


        /**
         * Indica si esta es la última página.
         * Ejemplo: false
         */
        @Schema(description = "Indica si esta es la última página", example = "false")
        private boolean last;


        /**
         * Indica si existe una página siguiente.
         * Ejemplo: true
         */
        @Schema(description = "Indica si existe una página siguiente", example = "true")
        private boolean hasNext;


        /**
         * Indica si existe una página anterior.
         * Ejemplo: false
         */
        @Schema(description = "Indica si existe una página anterior", example = "false")
        private boolean hasPrevious;

    }

    /**
     * Metodo estático para crear una instancia de PaginationResponseDTO a partir de un objeto Page.
     *
     * @param page Objeto Page que contiene la información de paginación y el contenido
     * @param <T>  Tipo de los elementos contenidos en la página
     * @return Una instancia de PaginationResponseDTO con el contenido y la información de paginación
     */
    public static <T> PaginationResponse<T> from(Page<T> page) {
        return PaginationResponse.<T>builder()
                .content(page.getContent())
                .pagination(PaginationInfo.builder()
                        .page(page.getNumber())
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .first(page.isFirst())
                        .last(page.isLast())
                        .hasNext(page.hasNext())
                        .hasPrevious(page.hasPrevious())
                        .build())
                .build();
    }

}

