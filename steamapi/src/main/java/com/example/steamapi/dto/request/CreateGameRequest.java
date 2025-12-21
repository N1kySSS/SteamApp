package com.example.steamapi.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateGameRequest(
        @NotBlank(message = "название не может быть пустым") String title,
        @NotNull(message = "разработчик не может отсутствовать") Long developerId,

        @NotNull(message = "цена не может отсутствовать")
        @Max(value = 19999, message = "цена не может превышать 20к")
        @Min(value = 0, message = "цена не может быть меньше 0")
        Integer price,

        String imageUrl,
        String description
) {}
