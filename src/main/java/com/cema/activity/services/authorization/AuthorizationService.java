package com.cema.activity.services.authorization;

public interface AuthorizationService {

    String getCurrentUserCuig();

    boolean isOnTheSameEstablishment(String cuig);

    boolean isAdmin();
}
