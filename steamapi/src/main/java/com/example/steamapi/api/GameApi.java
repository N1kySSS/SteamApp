package com.example.steamapi.api;

import com.example.steamapi.dto.request.CreateGameRequest;
import com.example.steamapi.dto.request.UpdateGameRequest;
import com.example.steamapi.dto.response.GameResponse;
import com.example.steamapi.dto.response.StatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "games", description = "Api для работы с играми")
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
public interface GameApi {
    @Operation(summary = "Создать новую игру")
    @ApiResponse(responseCode = "200", description = "Игра успешно создана")
    @ApiResponse(responseCode = "400", description = "Невалидный запрос", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "409", description = "Игра с таким названием уже существует", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PostMapping(value = "/api/games")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<GameResponse>> createGame(@Valid @RequestBody CreateGameRequest request);

    @Operation(summary = "Добавить игру в избранное")
    @ApiResponse(responseCode = "200", description = "Игра успешно создана")
    @ApiResponse(responseCode = "404", description = "Игра не найдена")
    @PostMapping(value = "/api/games/{id}")
    EntityModel<GameResponse> makeGameFavourite(@PathVariable Long id);

    @Operation(summary = "Получить конкретную игру по id")
    @ApiResponse(responseCode = "200", description = "Игра успешно найдена")
    @ApiResponse(responseCode = "404", description = "Игра не найдена")
    @GetMapping(value = "/api/games/{id}")
    EntityModel<GameResponse> getGameById(@PathVariable Long id);

    @Operation(summary = "Получить все игры")
    @ApiResponse(responseCode = "200", description = "Игры успешно найдены")
    @ApiResponse(responseCode = "404", description = "Игры не найдены")
    @GetMapping(value = "/api/games")
    CollectionModel<EntityModel<GameResponse>> getAllGames();

    @Operation(summary = "Получить все игры с пагинацией и фильтрацией")
    @ApiResponse(responseCode = "200", description = "Игры успешно найдены")
    @ApiResponse(responseCode = "404", description = "Игры не найдены")
    @GetMapping(value = "/api/games/paged")
    PagedModel<EntityModel<GameResponse>> getAllGamesPagination(
            @Parameter(description = "Фильтр по ID разработчика") @RequestParam(required = false) Long developerId,
            @Parameter(description = "Номер страницы (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size
    );

    @Operation(summary = "Получить все избранные игры")
    @ApiResponse(responseCode = "200", description = "Избранные игра успешно найдены")
    @ApiResponse(responseCode = "404", description = "Избранные игры не найдены")
    @GetMapping(value = "/api/games/favourites")
    CollectionModel<EntityModel<GameResponse>> getFavouriteGames();

    @Operation(summary = "Обновить игру по ID")
    @ApiResponse(responseCode = "200", description = "Игра успешно обновлена")
    @ApiResponse(responseCode = "404", description = "Игра не найдена", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "409", description = "Игра с таким названием уже существует", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PutMapping("/api/games/{id}")
    EntityModel<GameResponse> updateGame(@PathVariable Long id, @Valid @RequestBody UpdateGameRequest request);

    @Operation(summary = "Удалить конкретную игру по id")
    @ApiResponse(responseCode = "200", description = "Игра успешно удалена")
    @ApiResponse(responseCode = "404", description = "Игра не найдена")
    @DeleteMapping(value = "/api/games/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteGameById(@PathVariable Long id);
}
