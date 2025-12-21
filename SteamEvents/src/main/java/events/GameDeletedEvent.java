package events;

import java.io.Serializable;

public record GameDeletedEvent(
        Long gameId
) implements Serializable {}
