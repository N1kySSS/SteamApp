package com.example.steamapi.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateDeveloperRequest(
        @NotBlank(message = "имя не может быть пустым") String name,
        @NotBlank(message = "геолокация не может отсутствовать") String geolocation
) {}
