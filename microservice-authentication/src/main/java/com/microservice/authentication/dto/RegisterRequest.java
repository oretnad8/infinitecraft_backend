package com.microservice.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
    private String telefono;
    private String region;
    private String comuna;
    private String role;
}
