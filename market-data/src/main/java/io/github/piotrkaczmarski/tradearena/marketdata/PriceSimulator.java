package io.github.piotrkaczmarski.tradearena.marketdata;

import java.time.Duration;
import java.util.Objects;

public class PriceSimulator {

    private static final double NANOS_PER_HOUR = 3_600_000_000_000.0;

    /**
     * Applies one log-normal step to {@code price}:
     * {@code price * exp((driftPerHour - sigmaPerSqrtHour^2 / 2) * dt in hours + sigmaPerSqrtHour * sqrt(dt in hours) * shock)}.
     *
     * @param price            current price; must be finite and positive
     * @param sigmaPerSqrtHour volatility per square root of one hour; must be
     *                         finite and non-negative
     * @param dt               tick length; must be positive (zero is rejected as a
     *                         likely misconfiguration)
     * @param shock            standard-normal shock; must be finite
     * @param driftPerHour     drift per one hour; must be finite
     * @return the next price, always finite and positive
     * @throws IllegalArgumentException if any argument is out of range
     * @throws NullPointerException     if {@code dt} is null
     * @throws ArithmeticException      if the arguments are valid on their own but
     *                                  the step overflows to infinity or underflows
     *                                  to zero
     */
    public double nextPrice(
            double price,
            double sigmaPerSqrtHour,
            Duration dt,
            double shock,
            double driftPerHour) {

        if (!Double.isFinite(price))
            throw new IllegalArgumentException("price must be a valid number");
        if (price <= 0)
            throw new IllegalArgumentException("price must be positive");

        if (!Double.isFinite(sigmaPerSqrtHour))
            throw new IllegalArgumentException("sigmaPerSqrtHour must be a valid number");
        if (sigmaPerSqrtHour < 0)
            throw new IllegalArgumentException("sigmaPerSqrtHour must be non-negative");

        Objects.requireNonNull(dt, "dt must not be null");
        if (dt.isZero() || dt.isNegative())
            throw new IllegalArgumentException("dt must be positive");

        if (!Double.isFinite(shock))
            throw new IllegalArgumentException("shock must be a valid number");

        if (!Double.isFinite(driftPerHour))
            throw new IllegalArgumentException("driftPerHour must be a valid number");

        double dtHours = dt.toNanos() / NANOS_PER_HOUR;

        // Each step moves the price by a steady trend (drift) plus a random shock.
        // Random shocks on their own would make prices creep upward on average, because
        // a rise always gains more than an equally sized fall loses. The small
        // subtraction in `drift` cancels that, so drift alone decides whether prices
        // grow on average. With drift = 0, the average price stays flat, but a typical
        // single price slowly sinks, while a few big winners keep the average up.
        // That's a trade-off between the average and the median.
        double next = price * Math.exp(
                ((driftPerHour - (sigmaPerSqrtHour * sigmaPerSqrtHour) / 2d) * dtHours)
                        +
                        sigmaPerSqrtHour * Math.sqrt(dtHours) * shock);

        if (!Double.isFinite(next) || next <= 0)
            throw new ArithmeticException(
                    "price step out of range: price=" + price + ", sigmaPerSqrtHour=" + sigmaPerSqrtHour + ", dt=" + dt
                            + ", shock=" + shock + ", driftPerHour=" + driftPerHour + ", next=" + next);

        return next;
    }
}
