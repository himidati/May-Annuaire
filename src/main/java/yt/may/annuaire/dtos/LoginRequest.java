package yt.may.annuaire.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    // Getters
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}