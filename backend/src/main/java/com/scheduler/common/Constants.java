package com.scheduler.common;

import java.util.concurrent.TimeUnit;

public class Constants {
    public static final long DEFAULT_LOCK_TIMEOUT = 5000;
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;
    
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_RUNNING = "RUNNING";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";
}
