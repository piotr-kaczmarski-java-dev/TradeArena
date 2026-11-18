package io.github.piotrkaczmarski.tradearena.marketdata;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class InstrumentPricesTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-11-16T10:00:00Z"), ZoneOffset.UTC);
    private static final double SIGMA_PER_SQRT_HOUR = 0.1225d;
    private static final Duration DT = Duration.ofSeconds(1);

    private static final Set<Instrument> INSTRUMENTS = Set.of(
            new Instrument("GAMS", 100.0d, SIGMA_PER_SQRT_HOUR, 0.0d),
            new Instrument("CMPS", 150.0d, SIGMA_PER_SQRT_HOUR, 0.0d),
            new Instrument("BOKS", 400.0d, SIGMA_PER_SQRT_HOUR, 0.0d));

    // --- determinism ---

    @Test
    void sameSeedProducesSameSequence() {
        InstrumentPrices first = newPrices(INSTRUMENTS, 42L);
        InstrumentPrices second = newPrices(INSTRUMENTS, 42L);

        List<List<PriceTick>> firstRun = tickTimes(first, 20);
        List<List<PriceTick>> secondRun = tickTimes(second, 20);

        assertEquals(firstRun, secondRun);
    }

    @Test
    void differentSeedsProduceDifferentSequences() {
        List<List<PriceTick>> seed1 = tickTimes(newPrices(INSTRUMENTS, 1L), 20);
        List<List<PriceTick>> seed2 = tickTimes(newPrices(INSTRUMENTS, 2L), 20);

        assertNotEquals(seed1, seed2);
    }

    /**
     * Stronger than "prices move at all": the realized per-tick volatility must
     * match sigma * sqrt(dt). If the shock were scaled by sigma * sqrt(dt) twice,
     * the realized volatility would be about 8x too small for these parameters and
     * this test would fail.
     *
     * With a fixed seed the result is deterministic, so the tolerance can't cause
     * flakiness. 2000 log returns give a standard error of about 1.6% on the
     * estimate, so 10% leaves room.
     */
    @Test
    void realizedVolatilityMatchesSigma() {
        InstrumentPrices prices = newPrices(Set.of(new Instrument("GAMS", 100.0d, SIGMA_PER_SQRT_HOUR, 0.0d)), 7L);
        int ticks = 2_000;

        double previous = 100.0d;
        double sum = 0.0d;
        double sumOfSquares = 0.0d;
        for (int i = 0; i < ticks; i++) {
            double current = prices.tick().get(0).price().doubleValue();
            double logReturn = Math.log(current / previous);
            sum += logReturn;
            sumOfSquares += logReturn * logReturn;
            previous = current;
        }
        double mean = sum / ticks;
        double realizedStdDev = Math.sqrt(sumOfSquares / ticks - mean * mean);

        double dtInHours = DT.toNanos() / (double) Duration.ofHours(1).toNanos();
        double expectedStdDev = SIGMA_PER_SQRT_HOUR * Math.sqrt(dtInHours); // ~0.00204
        assertEquals(expectedStdDev, realizedStdDev, expectedStdDev * 0.10d);
    }

    // --- shape of the output ---

    @Test
    void ticksAreOrderedByInstrumentRegardlessOfInputOrder() {
        Set<Instrument> reverseOrder = new LinkedHashSet<>(
                List.of(
                        new Instrument("GAMS", 100.0d, SIGMA_PER_SQRT_HOUR, 0.0d),
                        new Instrument("CMPS", 150.0d, SIGMA_PER_SQRT_HOUR, 0.0d),
                        new Instrument("BOKS", 400.0d, SIGMA_PER_SQRT_HOUR, 0.0d)));

        List<PriceTick> ticks = newPrices(reverseOrder, 42L).tick();

        assertEquals(List.of("BOKS", "CMPS", "GAMS"), instrumentsOf(ticks));
    }

    @Test
    void oneTickPerInstrument() {
        List<PriceTick> ticks = newPrices(INSTRUMENTS, 42L).tick();

        // Comparing sorted lists checks the count, the members and the absence of
        // duplicates in one assertion, independent of the order tick() returns.
        List<String> sorted = instrumentsOf(ticks).stream().sorted().toList();
        assertEquals(List.of("BOKS", "CMPS", "GAMS"), sorted);
    }

    @Test
    void tickPricesHaveScaleTwo() {
        InstrumentPrices prices = newPrices(INSTRUMENTS, 42L);

        List<Executable> checks = new ArrayList<>();
        for (List<PriceTick> ticks : tickTimes(prices, 10)) {
            for (PriceTick tick : ticks) {
                checks.add(() -> assertEquals(2, tick.price().scale(), () -> "scale of " + tick));
            }
        }
        assertAll(checks);
    }

    // --- constructor validation ---

    @Test
    void constructorRejectsDuplicateInstrument() {
        // Different starting prices make these two records unequal, so the Set keeps
        // both.
        Set<Instrument> duplicates = Set.of(
                new Instrument("GAMS", 100.0d, SIGMA_PER_SQRT_HOUR, 0.0d),
                new Instrument("GAMS", 101.0d, SIGMA_PER_SQRT_HOUR, 0.0d));

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> newPrices(duplicates, 42L));
        assertMessageContains(e, "duplicates");
    }

    /**
     * Decision: an empty set is invalid. A simulator with no instruments can only
     * come from a configuration mistake (for example, instruments failed to load),
     * so failing at startup beats silently publishing empty ticks forever.
     */
    @Test
    void constructorRejectsEmptyInstrumentSet() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> newPrices(Set.of(), 42L));
        assertMessageContains(e, "instruments");
    }

    @Test
    void acceptsInstrumentWithMinimumStartingPrice() {
        Set<Instrument> instruments = Set.of(new Instrument("GAMS", 0.01d, SIGMA_PER_SQRT_HOUR, 0.0d));

        assertDoesNotThrow(() -> newPrices(instruments, 42L));
    }

    @Test
    void constructorRejectsNullClock() {
        NullPointerException e = assertThrows(
                NullPointerException.class,
                () -> new InstrumentPrices(
                        INSTRUMENTS,
                        null,
                        new SplittableRandom(42L),
                        DT));
        assertMessageContains(e, "clock");
    }

    @Test
    void constructorRejectsNullGenerator() {
        NullPointerException e = assertThrows(
                NullPointerException.class,
                () -> new InstrumentPrices(
                        INSTRUMENTS,
                        FIXED_CLOCK,
                        null,
                        DT));
        assertMessageContains(e, "randomGenerator");
    }

    @ParameterizedTest(name = "dt = {0} s")
    @ValueSource(longs = { 0L, -1L })
    void constructorRejectsNonPositiveDt(long seconds) {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> new InstrumentPrices(
                        INSTRUMENTS,
                        FIXED_CLOCK,
                        new SplittableRandom(42L),
                        Duration.ofSeconds(seconds)));
        assertMessageContains(e, "dt");
    }

    @Test
    void constructorRejectsNullDt() {
        NullPointerException e = assertThrows(
                NullPointerException.class,
                () -> new InstrumentPrices(
                        INSTRUMENTS,
                        FIXED_CLOCK,
                        new SplittableRandom(42L),
                        null));
        assertMessageContains(e, "dt");
    }

    @Test
    void ticksPublishCurrentSchemaVersion() {
        InstrumentPrices newPrices = newPrices(INSTRUMENTS, 1L);
        List<PriceTick> tick = newPrices.tick();

        assertNotNull(tick);
        assertNotNull(tick.get(0));
        assertEquals(PriceTick.CURRENT_SCHEMA_VERSION, tick.get(0).schemaVersion());
    }

    // --- helpers ---

    private static InstrumentPrices newPrices(Set<Instrument> instruments, long seed) {
        return new InstrumentPrices(
                instruments,
                FIXED_CLOCK,
                new SplittableRandom(seed),
                DT);
    }

    private static List<List<PriceTick>> tickTimes(InstrumentPrices prices, int times) {
        List<List<PriceTick>> result = new ArrayList<>(times);
        for (int i = 0; i < times; i++) {
            result.add(prices.tick());
        }
        return result;
    }

    private static List<String> instrumentsOf(List<PriceTick> ticks) {
        return ticks.stream().map(PriceTick::instrument).toList();
    }

    private static void assertMessageContains(Throwable e, String expected) {
        String message = e.getMessage();
        assertTrue(
                message != null && message.contains(expected),
                () -> "expected message containing \"" + expected + "\" but was: " + message);
    }

}
