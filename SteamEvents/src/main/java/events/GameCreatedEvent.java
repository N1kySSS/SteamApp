package events;

import java.io.Serializable;

public record GameCreatedEvent (
        Long gameId,
        String title,
        String developerName
) implements Serializable {}
