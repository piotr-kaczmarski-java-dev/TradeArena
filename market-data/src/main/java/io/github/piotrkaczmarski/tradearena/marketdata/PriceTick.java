package io.github.piotrkaczmarski.tradearena.marketdata;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;

import org.springframework.util.StringUtils;

public record PriceTick(
        String instrument,
        BigDecimal price,
        Instant timestamp,
        int schemaVersion) {

    private static final int CURRENT_SCHEMA_VERSION = 1;

    public PriceTick {
        Objects.requireNonNull(price, "price");
        if (price.scale() != 2) {
            try {
                price = price.setScale(2, RoundingMode.UNNECESSARY);
            } catch (ArithmeticException ex) {
                throw new IllegalArgumentException("price has incorrect scale: " + price, ex);
            }
        }

        if (price.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("price negative or zero: " + price);

        Objects.requireNonNull(instrument, "instrument");
        if (instrument.isBlank())
            throw new IllegalArgumentException("instrument blank");
        Objects.requireNonNull(timestamp, "timestamp");
    }

    public PriceTick(String instrument, BigDecimal price, Instant timestamp) {
        this(instrument, price, timestamp, CURRENT_SCHEMA_VERSION);
    }
}
