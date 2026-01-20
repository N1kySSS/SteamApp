package events;

import java.io.Serializable;

public record GameDeletedEvent(
        Long gameId,
        Boolean isFavourite
) implements Serializable {}
