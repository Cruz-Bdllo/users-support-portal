package com.code.supportportal.constant;


public class SecurityConstant {

    public static final int EXPIRATION_TIME = 5; //  Represent expiration in 5 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String PROPERTY_TOKEN = "Asde Delegacional";
    public static final String ASDE_ADMINISTRATOR = "User management portal";
    public static final String AUTHORITIES = "authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = {"/user/login", "/user/register", "/user/resetpassword", "/user/image/**"};
//    public static final String[] PUBLIC_URLS = {"**"};

} // end class for element constants
