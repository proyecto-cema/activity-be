package com.cema.activity.services.notification.impl;

import com.cema.activity.constants.Activities;
import com.cema.activity.domain.Activity;
import com.cema.activity.domain.security.User;
import com.cema.activity.services.client.users.UsersClientService;
import com.cema.activity.services.database.DatabaseService;
import com.cema.activity.services.notification.NotificationService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TextNotificationService implements NotificationService {

    private final String accountSid;
    private final String authSecret;
    private final String authUser;
    private final String sender;
    private final String countryCode;
    private final DatabaseService databaseService;
    private final UsersClientService usersClientService;
    private final Map<String, String> activityNames;

    public TextNotificationService(@Value("${notification.text.sid}") String accountSid,
                                   @Value("${notification.text.secret}") String authSecret,
                                   @Value("${notification.text.user}") String authUser,
                                   @Value("${notification.text.sender}") String sender,
                                   @Value("${notification.text.country-code}") String countryCode,
                                   DatabaseService databaseService, UsersClientService usersClientService) {
        this.accountSid = accountSid;
        this.authSecret = authSecret;
        this.sender = sender;
        this.countryCode = countryCode;
        this.databaseService = databaseService;
        this.usersClientService = usersClientService;
        this.activityNames = new HashMap<>();
        this.authUser = authUser;
        activityNames.put(Activities.INOCULATION_TYPE, Activities.INOCULATION_NAME);
        activityNames.put(Activities.WEIGHING_TYPE, Activities.WEIGHING_NAME);
        activityNames.put(Activities.ULTRASOUND_TYPE, Activities.ULTRASOUND_NAME);
        activityNames.put(Activities.MOVEMENT_TYPE, Activities.MOVEMENT_NAME);
        activityNames.put(Activities.FEEDING_TYPE, Activities.FEEDING_NAME);
    }

    //@Scheduled(cron = "0 15 10 * * ?", zone = "America/Buenos_Aires")
    @Override
    public void notifyAllUsers() {
        List<Activity> activityList = databaseService.getAllUsersToNotifyToday()
                .stream().filter(activity -> StringUtils.hasText(activity.getWorkerUserName())).collect(Collectors.toList());
        log.info("Found {} activities for today", activityList.size());
        for (Activity activity : activityList) {
            try {
                User toNotify = usersClientService.getUser(activity.getWorkerUserName());
                String phone = toNotify.getPhone();
                String name = toNotify.getName();
                String activityType = activity.getType();
                activityType = activityNames.get(activityType.toLowerCase(Locale.ROOT));
                log.info("Notifying user {} with phone {} for activityType {}", name, phone, activityType);

                String body = String.format("El usuario %s tiene una actividad de %s programada para hoy.", name, activityType);
                log.info("Sending message: {}", body);
                sendNotification(body, phone);
            } catch (Exception e) {
                log.error("unable to send notification to user {} due to error", activity.getWorkerUserName(), e);
            }
        }
    }

    @Override
    public void sendNotification(String body, String destination) {
        Twilio.init(authUser, authSecret, accountSid);
        if (!destination.startsWith(countryCode)) {
            destination = countryCode + destination;
        }

        Message message = Message.creator(new PhoneNumber(destination),
                new PhoneNumber(sender),
                body).create();

        log.info("Sending message {}", message.getSid());
    }
}
