package com.cema.activity.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SearchRequest {

    private int page;
    private int size;
    private Activity activity;
}
