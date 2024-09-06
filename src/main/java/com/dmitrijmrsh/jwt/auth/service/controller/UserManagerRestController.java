package com.dmitrijmrsh.jwt.auth.service.controller;

import com.dmitrijmrsh.jwt.auth.service.entity.User;
import com.dmitrijmrsh.jwt.auth.service.payload.UpdateUserPrivilegePayload;
import com.dmitrijmrsh.jwt.auth.service.payload.UpdateUserRolePayload;
import com.dmitrijmrsh.jwt.auth.service.service.UserManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "user_manager_controller")
@RestController
@RequestMapping("api/v1/manager")
@RequiredArgsConstructor
public class UserManagerRestController {

    private final UserManagerService userManagerService;

    @Operation(
            summary = "Получение данных пользователя по id",
            description =
                    "Данная ручка может быть вызвана админом (\"ROLE_ADMIN\") или менеджером (\"ROLE_MANAGER\"). " +
                    "В Authorization хэдере необходим JWT-токен. " +
                    "В url запроса указывается id пользователя",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные получены"),
            @ApiResponse(responseCode = "401", description = "Отправитель запроса не аутентифицирован / " +
                    "имеет недостаточно прав доступа"),
            @ApiResponse(responseCode = "500", description = "Недействительный JWT-токен")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/get/{id:\\d+}")
    public User getUserById(@PathVariable("id") Integer id) {
        return userManagerService.getUserById(id);
    }

    @Operation(
            summary = "Получение списка всех пользователей",
            description =
                    "Данная ручка может быть вызвана админом (\"ROLE_ADMIN\") или менеджером (\"ROLE_MANAGER\"). " +
                            "В Authorization хэдере необходим JWT-токен. " +
                            "В случае успеха возвращает список с данными действующих пользователей.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей получен"),
            @ApiResponse(responseCode = "401", description = "Отправитель не аутентифицирован / " +
                    "имеет недостаточно прав доступа"),
            @ApiResponse(responseCode = "500", description = "Недействительный JWT-токен")
    })

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/get/all")
    public List<User> getAllUsers(@AuthenticationPrincipal UserDetails userDetails) {
        return userManagerService.getAllUsers(userDetails);
    }

    @Operation(
            summary = "Обновление роли пользователя",
            description = "Данная ручка может быть вызвана только админом (\"ROLE_ADMIN\"). " +
                    "В Authorization хэдере необходим JWT-токен. " +
                    "В url запроса указывается id пользователя, в теле запроса - желаемая роль " +
                    "с префиксом ROLE_ из entity.enums",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Роль успешно обновлена"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат данных в теле запроса"),
            @ApiResponse(responseCode = "404", description = "Пользователь с данным id не найден"),
            @ApiResponse(responseCode = "401", description = "Отправитель не аутентифицирован / " +
                    "имеет недостаточно прав доступа"),
            @ApiResponse(responseCode = "500", description = "Недействительный JWT-токен")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update/role/{id:\\d+}")
    public ResponseEntity<?> updateUserRole(
            @PathVariable("id") Integer id,
            @Valid @RequestBody UpdateUserRolePayload updateUserRolePayload,
            BindingResult bindingResult
    ) throws BindException {

        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            }

            throw new BindException(bindingResult);
        }

        userManagerService.setRoleToUserById(id, updateUserRolePayload);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Обновление уровня привилегий пользователя",
            description =
                    "Данная ручка может быть вызвана админом (\"ROLE_ADMIN\") или менеджером (\"ROLE_MANAGER\"). " +
                            "В Authorization хэдере необходим JWT-токен. " +
                            "В url запроса указывается id пользователя, в теле запроса - желаемый уровень привелегий. " +
                            " с префиксом PRIVILEGE_ из entity.enums.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Привилегия успешно обновлена"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат данных в теле запроса"),
            @ApiResponse(responseCode = "404", description = "Пользователь с данным id не найден"),
            @ApiResponse(responseCode = "401", description = "Отправитель не аутентифицирован / " +
                    "имеет недостаточно прав доступа"),
            @ApiResponse(responseCode = "500", description = "Недействительный JWT-токен")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PatchMapping("/update/privilege/{id:\\d+}")
    public ResponseEntity<?> updateUserPrivilege(
            @PathVariable("id") Integer id,
            @Valid @RequestBody UpdateUserPrivilegePayload updateUserPrivilegePayload,
            @AuthenticationPrincipal UserDetails userDetails,
            BindingResult bindingResult
    ) throws BindException {

        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            }

            throw new BindException(bindingResult);
        }

        userManagerService.setPrivilegeToUserById(id, updateUserPrivilegePayload, userDetails);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Удаление пользователя по id",
            description =
                    "Данная ручка может быть вызвана админом (\"ROLE_ADMIN\") или менеджером (\"ROLE_MANAGER\"). " +
                    "В Authorization хэдере необходим JWT-токен." +
                    "В url указывается id удаляемого пользователя",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно удалён из основной таблицы"),
            @ApiResponse(responseCode = "401", description = "Отправитель запроса не аутентифицирован / " +
                    "имеет недостаточно прав доступа"),
            @ApiResponse(responseCode = "404", description = "Пользователь с данным id не найден"),
            @ApiResponse(responseCode = "500", description = "Недействительный JWT-токен")
    })
    @DeleteMapping("delete/{id:\\d+}")
    public ResponseEntity<?> deleteUserById(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        userManagerService.deleteUserById(id, userDetails);
        return ResponseEntity.noContent().build();
    }

}
