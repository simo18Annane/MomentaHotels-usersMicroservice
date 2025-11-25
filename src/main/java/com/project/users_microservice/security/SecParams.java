package com.project.users_microservice.security;

public interface SecParams {

    public static final long EXP_TIME = 10*24*60*60*1000;
    public static final String SECRET = "******";
    public static final String PREFIX = "Bearer ";
    
}