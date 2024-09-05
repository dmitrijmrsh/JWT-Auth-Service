package com.dmitrijmrsh.jwt.auth.service.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record GetUserInfoPayload(

        @NotNull(message = "{user.data.validation.errors.id.is.null}")
        Integer id,

        @NotBlank(message = "{user.data.validation.errors.first.name.is.blank}")
        String firstName,

        @NotBlank(message = "{user.data.validation.errors.last.name.is.blank}")
        String lastName,

        @NotBlank(message = "{user.data.validation.errors.email.is.blank}")
        @Pattern(
                regexp = "^[a-zA-Z0-9.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "{user.data.validation.errors.email.format.is.invalid}"
        )
        String email,

        @Pattern(
                regexp = "PRIVILEGE_STANDARD|PRIVILEGE_HIGH|PRIVILEGE_VIP",
                message = "{user.data.validation.errors.privilege.does.not.exist}"
        )
        String privilege
) {
}
