package com.trade.trade.domain.datatransferobjects;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String username;
    private String password;
}
