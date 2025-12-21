package com.example.steamapi.api;

import com.example.steamapi.dto.request.CreateDeveloperRequest;
import com.example.steamapi.dto.response.DeveloperResponse;
import com.example.steamapi.dto.response.StatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "developers", description = "Api для работы с разработчиками")
@ApiResponses({
        @ApiResponse(
                responseCode = "400",
                description = "Ошибка валидации",
                content = {
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = StatusResponse.class)
                        )
                }
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Внутрення ошибка сервера",
                content = {
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = StatusResponse.class)
                        )
                }
        ),
})
public interface DeveloperApi {
    @Operation(summary = "Создать нового разработчика")
    @ApiResponse(responseCode = "200", description = "Разработчик успешно создан")
    @ApiResponse(responseCode = "400", description = "Невалидный запрос", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "409", description = "Разработчик с таким названием уже существует", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PostMapping(value = "/api/developers")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<DeveloperResponse>> createDeveloper(@Valid @RequestBody CreateDeveloperRequest request);

    @Operation(summary = "Получить всех разработчиков")
    @ApiResponse(responseCode = "200", description = "Разработчики успешно найдены")
    @ApiResponse(responseCode = "404", description = "Разработчики не найдены")
    @GetMapping(value = "/api/developers")
    CollectionModel<EntityModel<DeveloperResponse>> getAllDevelopers();

    @Operation(summary = "Получить конкретного разработчика по id")
    @ApiResponse(responseCode = "200", description = "Разработчик успешно найден")
    @ApiResponse(responseCode = "404", description = "Разработчик не найден")
    @GetMapping(value = "/api/developers/{id}")
    EntityModel<DeveloperResponse> getDeveloperById(@PathVariable Long id);

    @Operation(summary = "Удалить конкретного разработчика по id")
    @ApiResponse(responseCode = "200", description = "Разработчик успешно удален")
    @ApiResponse(responseCode = "404", description = "Разработчик не найден")
    @DeleteMapping(value = "/api/developers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteDeveloperById(@PathVariable Long id);
}
