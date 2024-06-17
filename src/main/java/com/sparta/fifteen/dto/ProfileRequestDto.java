package com.sparta.fifteen.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileRequestDto {
    @Size(min = 10, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    private String name;

    private String oneline;

    @Size(min = 10)
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_])*$")
    private String currentPassword;

    @Size(min = 10)
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_])*$")
    private String newPassword;

    @Size(min = 10)
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_])*$")
    private String checkNewPassword;

    public boolean isNewPasswordMatch() {

        return this.checkNewPassword.equals(this.newPassword);
    }

    public boolean isPasswordMatching() {

        return this.newPassword.equals(this.currentPassword);
    }
}
