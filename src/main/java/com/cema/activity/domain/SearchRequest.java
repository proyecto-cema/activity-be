package com.cema.activity.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SearchRequest {

    private final int page;
    private final int size;
    private final Activity activity;
}
