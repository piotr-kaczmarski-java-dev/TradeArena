package io.github.piotrkaczmarski.tradearena.marketdata;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

/**
 * Class responsible for storing and updating instrument prices.
 *
 * Not thread-safe, intended to be owned and called by a single component.
 */
public class InstrumentPrices {

    private static final PriceSimulator STEP = new PriceSimulator();
    private final Clock clock;
    private final RandomGenerator randomGenerator;

    private final TreeMap<String, Double> prices;
    private final double sigmaPerSqrtHour;
    private final Duration dt;

    public InstrumentPrices(
            Set<Instrument> instruments,
            Clock clock,
            RandomGenerator randomGenerator,
            double sigmaPerSqrtHour,
            Duration dt) {

        Objects.requireNonNull(instruments, "instruments must not be null");
        if (instruments.isEmpty()) {
            throw new IllegalArgumentException("instruments must not be empty");
        }
        Objects.requireNonNull(clock, "clock must not be null");
        Objects.requireNonNull(randomGenerator, "randomGenerator must not be null");
        if (!Double.isFinite(sigmaPerSqrtHour) || sigmaPerSqrtHour < 0) {
            throw new IllegalArgumentException("sigmaPerSqrtHour must be finite and non-negative");
        }
        Objects.requireNonNull(dt, "dt must not be null");
        if (!dt.isPositive()) {
            throw new IllegalArgumentException("dt must be positive");
        }
        instruments.forEach(i -> {
            Objects.requireNonNull(i, "instrument must not be null");
        });
        if (instruments.stream().map(Instrument::symbol).distinct().count() != instruments.size()) {
            throw new IllegalArgumentException("instruments must not contain duplicates");
        }

        this.clock = clock;
        this.randomGenerator = randomGenerator;
        this.sigmaPerSqrtHour = sigmaPerSqrtHour;
        this.prices = new TreeMap<>(instruments.stream()
                .collect(
                        Collectors.toMap(Instrument::symbol, Instrument::startingPrice)));
        this.dt = dt;
    }

    public List<PriceTick> tick() {
        for (String instrument : prices.keySet()) {
            double currentPrice = prices.get(instrument);
            double randomShock = randomGenerator.nextGaussian();

            double newPrice = STEP.nextPrice(
                    currentPrice,
                    sigmaPerSqrtHour,
                    dt,
                    randomShock);

            prices.put(instrument, newPrice);
        }

        Instant timestamp = clock.instant();

        return prices.entrySet().stream()
                .map(entry -> new PriceTick(
                        entry.getKey(),
                        BigDecimal.valueOf(entry.getValue()).setScale(2, RoundingMode.HALF_UP),
                        timestamp,
                        PriceTick.CURRENT_SCHEMA_VERSION))
                .collect(Collectors.toList());
    }
}
