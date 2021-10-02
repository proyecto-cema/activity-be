package com.cema.activity.controllers.helpers;

import com.cema.activity.domain.Activity;
import com.cema.activity.domain.search.SearchResponse;
import org.springframework.http.HttpHeaders;

public interface ActivityHelper {

    Activity register(Activity activity);

    void delete(String uuid, String type);

    Activity update(String uuid, Activity activity);

    Activity get(String uuid, String type, String cuig);

    SearchResponse search(Activity activity, int page, int size);

    HttpHeaders buildHeaders(SearchResponse searchResponse);
}
