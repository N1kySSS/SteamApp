package com.example.steamapi.dto.response;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.Objects;

@Relation(collectionRelation = "games", itemRelation = "game")
public class GameResponse extends RepresentationModel<GameResponse> {
    private final Long id;
    private final String title;
    private final Long developerId;
    private final String description;
    private final String imageUrl;
    private final Integer price;
    private final Boolean isFavourite;
    private final Float discount;
    private final LocalDateTime createdAt;

    public GameResponse(
            Long id,
            String title,
            Long developerId,
            String description,
            String imageUrl,
            Integer price,
            Boolean isFavourite,
            Float discount,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.title = title;
        this.developerId = developerId;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.isFavourite = isFavourite;
        this.discount = discount;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Long getDeveloperId() {
        return developerId;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getPrice() {
        return price;
    }

    public Boolean getFavourite() {
        return isFavourite;
    }

    public Float getDiscount() {
        return discount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GameResponse that = (GameResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(developerId, that.developerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, title, developerId);
    }
}
