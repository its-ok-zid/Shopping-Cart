package com.cts.common.api;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages, boolean hasNext) {
    public static <S, T> PageResponse<T> from(Page<S> source, Function<S, T> mapper) {
        return new PageResponse<>(source.map(mapper).getContent(), source.getNumber(), source.getSize(),
                source.getTotalElements(), source.getTotalPages(), source.hasNext());
    }
}
