package com.example.steamapi.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateGameRequest(
        @NotNull(message = "цена не может отсутствовать")
        @Max(value = 19999, message = "цена не может превышать 20к")
        @Min(value = 0, message = "цена не может быть меньше 0")
        Integer price,

        String imageUrl,
        String description
) {
}
