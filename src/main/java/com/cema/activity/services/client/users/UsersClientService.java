package com.cema.activity.services.client.users;

import com.cema.activity.domain.security.User;

public interface UsersClientService {

    User getUser(String userName);
}
