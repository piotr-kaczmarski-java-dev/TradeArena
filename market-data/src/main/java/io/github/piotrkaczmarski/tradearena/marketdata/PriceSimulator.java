package io.github.piotrkaczmarski.tradearena.marketdata;

import java.time.Duration;
import java.util.Objects;

public class PriceSimulator {

    private static final double NANOS_PER_HOUR = 3_600_000_000_000.0;

    /**
     * Applies one log-normal step to {@code price}:
     * {@code price * exp(sigmaPerSqrtHour * sqrt(dt in hours) * z)}.
     *
     * @param price            current price; must be finite and positive
     * @param sigmaPerSqrtHour volatility per square root of one hour; must be
     *                         finite and non-negative
     * @param dt               tick length; must be positive (zero is rejected as a
     *                         likely misconfiguration)
     * @param z                standard-normal shock; must be finite
     * @return the next price, always finite and positive
     * @throws IllegalArgumentException if any argument is out of range
     * @throws NullPointerException     if {@code dt} is null
     * @throws ArithmeticException      if the arguments are valid on their own but
     *                                  the step
     *                                  overflows to infinity or underflows to zero
     */
    public double nextPrice(
            double price,
            double sigmaPerSqrtHour,
            Duration dt,
            double z) {

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

        if (!Double.isFinite(z))
            throw new IllegalArgumentException("z must be a valid number");

        double dtHours = dt.toNanos() / NANOS_PER_HOUR;
        double next = price * Math.exp(sigmaPerSqrtHour * Math.sqrt(dtHours) * z);

        if (!Double.isFinite(next) || next <= 0)
            throw new ArithmeticException("price step out of range: price=" + price
                    + ", sigmaPerSqrtHour=" + sigmaPerSqrtHour + ", dt=" + dt + ", z=" + z);

        return next;
    }
}
