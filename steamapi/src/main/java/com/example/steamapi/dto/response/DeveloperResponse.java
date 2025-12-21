package com.example.steamapi.dto.response;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.Objects;

@Relation(collectionRelation = "developers", itemRelation = "developer")
public class DeveloperResponse extends RepresentationModel<DeveloperResponse> {
    private final Long id;
    private final String name;
    private final String geolocation;
    private final Integer gameCount;
    private final LocalDateTime registeredAt;

    public DeveloperResponse(Long id, String name, String geolocation, Integer gameCount, LocalDateTime registeredAt) {
        this.id = id;
        this.name = name;
        this.geolocation = geolocation;
        this.gameCount = gameCount;
        this.registeredAt = registeredAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGeolocation() {
        return geolocation;
    }

    public Integer getGameCount() {
        return gameCount;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DeveloperResponse that = (DeveloperResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name);
    }
}
