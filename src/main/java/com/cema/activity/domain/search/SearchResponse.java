package com.cema.activity.domain.search;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SearchResponse<T> {

    List<T> activities;
    private final int totalElements;
    private final int totalPages;
    private final int currentPage;
}
