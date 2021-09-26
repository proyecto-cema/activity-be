package com.cema.activity.controllers.helpers.impl;

import com.cema.activity.constants.HandlerNames;
import com.cema.activity.controllers.helpers.ActivityHelper;
import com.cema.activity.domain.Activity;
import com.cema.activity.domain.SearchRequest;
import com.cema.activity.domain.SearchResponse;
import com.cema.activity.handlers.ActivityHandler;
import com.cema.activity.handlers.HandlerFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class ActivityHelperImpl implements ActivityHelper {

    private final HandlerFactory handlerFactory;

    public ActivityHelperImpl(HandlerFactory handlerFactory) {
        this.handlerFactory = handlerFactory;
    }

    @Override
    public Activity register(Activity activity) {
        String type = activity.getType().toLowerCase();
        activity.setId(null);
        log.info("Request to register new {} activity", type);

        ActivityHandler<Activity, Activity> activityHandler = handlerFactory.getHandler(type + HandlerNames.REGISTER_HANDLER);

        return activityHandler.handle(activity);
    }

    @Override
    public void delete(String uuid, String type) {
        log.info("Request to delete activity id {}", uuid);
        ActivityHandler<String, Activity> activityHandler = handlerFactory.getHandler(type + HandlerNames.DELETE_HANDLER);

        activityHandler.handle(uuid);
    }

    @Override
    public Activity update(String uuid, Activity activity) {
        activity.setId(UUID.fromString(uuid));
        String type = activity.getType().toLowerCase();
        log.info("Request to delete {} activity", type);

        ActivityHandler<Activity, Activity> activityHandler = handlerFactory.getHandler(type + HandlerNames.UPDATE_HANDLER);

        return activityHandler.handle(activity);
    }

    @Override
    public Activity get(String uuid, String type, String cuig) {
        log.info("Request to get activity id {}", uuid);
        Activity activity = new Activity();
        activity.setId(UUID.fromString(uuid));
        activity.setEstablishmentCuig(cuig);
        ActivityHandler<Activity, Activity> activityHandler = handlerFactory.getHandler(type + HandlerNames.GET_HANDLER);

        return activityHandler.handle(activity);
    }

    @Override
    public SearchResponse search(Activity activity, int page, int size) {
        log.info("Request to search activities");
        SearchRequest searchRequest = SearchRequest.builder()
                .activity(activity)
                .page(page)
                .size(size)
                .build();
        ActivityHandler<SearchRequest, SearchResponse> activityHandler = handlerFactory.getHandler(activity.getType() + HandlerNames.SEARCH_HANDLER);

        return activityHandler.handle(searchRequest);
    }
}
