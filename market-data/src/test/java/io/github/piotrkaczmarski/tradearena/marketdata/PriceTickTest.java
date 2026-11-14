package io.github.piotrkaczmarski.tradearena.marketdata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

class PriceTickTest {

    private static final Instant FIXED = Instant.parse("2026-11-14T10:11:00Z");

    @Test
    void ticksWithSamePriceAndDifferentScaleAreEqual() {
        PriceTick tick1 = new PriceTick(
                "NWRB",
                new BigDecimal("100.0"),
                FIXED);

        PriceTick tick2 = new PriceTick(
                "NWRB",
                new BigDecimal("100.00"),
                FIXED);

        assertEquals(tick1, tick2);
    }

    @Test
    void rejectsNullInstrument() {
        Exception ex = assertThrows(NullPointerException.class, () -> new PriceTick(
                null,
                new BigDecimal("100.0"),
                FIXED));

        assertThat(ex).hasMessageContaining("instrument");
    }

    @Test
    void rejectsNullPrice() {
        Exception ex = assertThrows(NullPointerException.class, () -> new PriceTick(
                "NWRB",
                null,
                FIXED));

        assertThat(ex).hasMessageContaining("price");
    }

    @Test
    void rejectsNullTimestamp() {
        Exception ex = assertThrows(NullPointerException.class, () -> new PriceTick(
                "NWRB",
                new BigDecimal("100.0"),
                null));

        assertThat(ex).hasMessageContaining("timestamp");
    }

    @Test
    void rejectsZeroPrice() {
        assertThrows(IllegalArgumentException.class, () -> new PriceTick(
                "NWRB",
                new BigDecimal("0.00"),
                FIXED));
    }

    @Test
    void rejectsNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> new PriceTick(
                "NWRB",
                new BigDecimal("-0.01"),
                FIXED));
    }

    @Test
    void normalisesScaleZero() {
        PriceTick tick = new PriceTick(
                "NWRB",
                new BigDecimal("100"),
                FIXED);

        // Deliberately using assertEquals, it checks for scale so it will detect the
        // difference
        assertEquals(new BigDecimal("100.00"), tick.price());
    }

    @Test
    void acceptsExtraTrailingZeros() {
        PriceTick tick = new PriceTick(
                "NWRB",
                new BigDecimal("100.0000"),
                FIXED);

        assertEquals(new BigDecimal("100.00"), tick.price());
    }

    @Test
    void rejectsPriceWithMoreThanTwoDecimals() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> new PriceTick(
                "NWRB",
                new BigDecimal("100.001"),
                FIXED));

        assertThat(ex).hasMessageContaining("price");
    }

    @Test
    void rejectsBlankInstrument() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> new PriceTick(
                "   ",
                new BigDecimal("100.00"),
                FIXED));

        assertThat(ex).hasMessageContaining("instrument");
    }
}
