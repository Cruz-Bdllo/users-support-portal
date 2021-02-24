package com.code.supportportal.constant;

/*
* This allow that one user have only one role, in that role have all permissions necessary.
* */
public class Authority {
    public static final String[] USER_AUTHORITIES = { "user:read" };
    public static final String[] HR_AUTHORITIES = { "user:read", "user:update" };
    public static final String[] MANAGEMENT_AUTHORITIES = { "user:read", "user:update" };
    public static final String[] ADMIN_AUTHORITIES = { "user:read", "user:update", "user:create" };
    public static final String[] SUPER_USER_AUTHORITIES = { "user:read", "user:update", "user:create", "user:delete" };
}
