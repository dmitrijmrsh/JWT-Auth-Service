package com.dmitrijmrsh.jwt.auth.service.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateUserRolePayload(

        @NotBlank(message = "{user.data.validation.errors.role.is.blank}")
        @Pattern(
                regexp = "ROLE_MANAGER|ROLE_USER",
                message = "{user.data.validation.errors.role.does.not.exist}"
        )
        String role
) {
}
