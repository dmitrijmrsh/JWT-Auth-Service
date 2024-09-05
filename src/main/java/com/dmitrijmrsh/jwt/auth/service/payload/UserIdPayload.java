package com.dmitrijmrsh.jwt.auth.service.payload;

import jakarta.validation.constraints.NotNull;

public record UserIdPayload(

        @NotNull(message = "{user.data.validation.errors.id.is.null}")
        Integer id
) {
}
