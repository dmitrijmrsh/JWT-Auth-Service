package com.dmitrijmrsh.jwt.auth.service.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateUserPrivilegePayload(

        @NotBlank(message = "{user.data.validation.errors.privilege.is.blank}")
        @Pattern(
                regexp = "PRIVILEGE_STANDARD|PRIVILEGE_HIGH|PRIVILEGE_VIP",
                message = "{user.data.validation.errors.privilege.does.not.exist}"
        )
        String privilege
) {
}
