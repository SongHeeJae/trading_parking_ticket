package com.kuke.parkingticket.common.cache;


public class CacheKey {
    public static final int DEFAULT_EXPIRE_SEC = 60; // 1 minutes
    public static final String USER = "user";
    public static final int USER_EXPIRE_SEC = 60 * 5; // 5 minutes
    public static final String TICKET = "ticket";
    public static final String TICKETS = "tickets";
    public static final int TICKET_EXPIRE_SEC = 60 * 5; // 5 minutes
    public static final String TOKEN = "token";
}
