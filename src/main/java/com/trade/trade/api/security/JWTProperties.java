package com.trade.trade.api.security;

public class JWTProperties {
    public static final String SECRET = "hahathisissecret";
    public static final int EXPIRATION_TIME = 864_000_000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}
