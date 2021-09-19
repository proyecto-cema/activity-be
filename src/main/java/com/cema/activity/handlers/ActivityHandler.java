package com.cema.activity.handlers;

import com.cema.activity.domain.Activity;

public interface ActivityHandler<IN, OU> {

    OU handle(IN activity);
}
