package com.cema.activity.handlers;

public interface ActivityHandler<IN, OU> {

    OU handle(IN activity);
}
