package com.cema.activity.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SearchResponse<T> {

    List<T> activities;
    private int totalElements;
    private int totalPages;
    private int currentPage;
}
