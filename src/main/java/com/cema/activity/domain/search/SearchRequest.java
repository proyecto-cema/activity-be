package com.cema.activity.domain.search;

import com.cema.activity.domain.Activity;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SearchRequest {

    private final int page;
    private final int size;
    private final Activity activity;
}
