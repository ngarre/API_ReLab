package com.natalia.relab.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String nickname;
    private String password;
}
