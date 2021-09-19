package com.cema.activity.controllers.helpers;

import com.cema.activity.domain.Activity;
import com.cema.activity.domain.SearchResponse;
import org.springframework.data.domain.Page;

public interface ActivityHelper {

    void register(Activity activity);

    void delete(String uuid, String type);

    Activity update(String uuid, Activity activity);

    Activity get(String uuid, String type, String cuig);

    SearchResponse search(Activity activity, int page, int size);
}
