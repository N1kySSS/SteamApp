package events;

import java.io.Serializable;

public record GameDiscountAddedEvent(
        Long gameId,
        Long gameFinalPrice,
        Long gamePercentDiscount
) implements Serializable {}
