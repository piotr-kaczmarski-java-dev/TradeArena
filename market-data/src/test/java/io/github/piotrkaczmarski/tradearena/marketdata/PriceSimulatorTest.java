package io.github.piotrkaczmarski.tradearena.marketdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PriceSimulatorTest {

    private static final double PRICE = 100.0d;
    private static final double SIGMA_PER_SQRT_HOUR = 0.1225d;
    private static final double DRIFT_CANCELLING_ITO_CORRECTION = (SIGMA_PER_SQRT_HOUR * SIGMA_PER_SQRT_HOUR) / 2d;
    private static final Duration ONE_SECOND = Duration.ofSeconds(1);

    private final PriceSimulator ps = new PriceSimulator();

    // --- argument validation ---

    @ParameterizedTest(name = "price = {0}")
    @ValueSource(doubles = { Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 0.0d, -1.0d })
    void rejectsInvalidPrice(double price) {
        assertThatThrownBy(
                () -> ps.nextPrice(price, SIGMA_PER_SQRT_HOUR, ONE_SECOND, 0.5d, DRIFT_CANCELLING_ITO_CORRECTION))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("price");
    }

    @ParameterizedTest(name = "sigma = {0}")
    @ValueSource(doubles = { Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, -0.1225d })
    void rejectsInvalidSigma(double sigma) {
        assertThatThrownBy(() -> ps.nextPrice(PRICE, sigma, ONE_SECOND, 0.5d, DRIFT_CANCELLING_ITO_CORRECTION))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sigma");
    }

    @ParameterizedTest(name = "z = {0}")
    @ValueSource(doubles = { Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY })
    void rejectsInvalidShock(double shock) {
        assertThatThrownBy(
                () -> ps.nextPrice(PRICE, SIGMA_PER_SQRT_HOUR, ONE_SECOND, shock, DRIFT_CANCELLING_ITO_CORRECTION))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("shock");
    }

    @ParameterizedTest(name = "dt = {0} s")
    @ValueSource(longs = { 0L, -1L })
    void rejectsNonPositiveDt(long seconds) {
        assertThatThrownBy(
                () -> ps.nextPrice(
                        PRICE,
                        SIGMA_PER_SQRT_HOUR,
                        Duration.ofSeconds(seconds),
                        0.5d,
                        DRIFT_CANCELLING_ITO_CORRECTION))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dt");
    }

    @Test
    void rejectsNullDt() {
        assertThatThrownBy(() -> ps.nextPrice(PRICE, SIGMA_PER_SQRT_HOUR, null, 0.5d, DRIFT_CANCELLING_ITO_CORRECTION))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("dt");
    }

    // --- result range (valid inputs whose product overflows or underflows) ---

    @Test
    void throwsWhenStepOverflowsToInfinity() {
        assertThatThrownBy(() -> ps.nextPrice(PRICE, 1e6d, ONE_SECOND, 10.0d, DRIFT_CANCELLING_ITO_CORRECTION))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void throwsWhenStepUnderflowsToZero() {
        assertThatThrownBy(() -> ps.nextPrice(PRICE, 1e6d, ONE_SECOND, -10.0d, DRIFT_CANCELLING_ITO_CORRECTION))
                .isInstanceOf(ArithmeticException.class);
    }

    // --- behaviour with realistic sigma and a 1 s tick ---

    @Test
    void staysPositiveWhenShockIsVeryNegative() {
        double newPrice = ps.nextPrice(PRICE, SIGMA_PER_SQRT_HOUR, ONE_SECOND, -50.0d, DRIFT_CANCELLING_ITO_CORRECTION);

        assertThat(newPrice).isGreaterThan(0.0d);
    }

    @Test
    void returnsSamePriceWhenShockIsZero() {
        double newPrice = ps.nextPrice(PRICE, SIGMA_PER_SQRT_HOUR, ONE_SECOND, 0.0d, DRIFT_CANCELLING_ITO_CORRECTION);

        assertThat(newPrice).isEqualTo(PRICE); // exp(0) is exactly 1.0, so exact comparison is safe
    }

    @Test
    void risesWhenShockIsPositive() {
        double newPrice = ps.nextPrice(PRICE, SIGMA_PER_SQRT_HOUR, ONE_SECOND, 1.0d, DRIFT_CANCELLING_ITO_CORRECTION);

        assertThat(newPrice).isGreaterThan(PRICE);
    }

    @Test
    void fallsWhenShockIsNegative() {
        double newPrice = ps.nextPrice(PRICE, SIGMA_PER_SQRT_HOUR, ONE_SECOND, -1.0d, DRIFT_CANCELLING_ITO_CORRECTION);

        assertThat(newPrice).isLessThan(PRICE);
    }

    @Test
    void oneSecondStepIsAbout0_00204() {
        double logStep = logStep(ONE_SECOND, 1.0d);

        assertThat(logStep).isCloseTo(0.00204, within(0.00001d));
    }

    @Test
    void quadruplingDtDoublesLogStep() {
        double oneSecond = logStep(Duration.ofSeconds(1), 1.0d);
        double fourSeconds = logStep(Duration.ofSeconds(4), 1.0d);

        assertThat(fourSeconds / oneSecond).isCloseTo(2.0d, within(1e-9d)); // sqrt(4) = 2, not 4
    }

    @Test
    void subMillisecondDtStillMovesPrice() {
        double newPrice = ps.nextPrice(
                PRICE,
                SIGMA_PER_SQRT_HOUR,
                Duration.ofNanos(500_000),
                1.0d,
                DRIFT_CANCELLING_ITO_CORRECTION);

        assertThat(newPrice).isGreaterThan(PRICE);
    }

    @Test
    void driftAloneGrowsPriceAtRateMu() {
        double sigma = 0.0d;
        double driftPerHour = Math.log(2d);
        Duration dt = Duration.ofHours(1);

        double next = ps.nextPrice(PRICE, sigma, dt, 1.0d, driftPerHour);

        assertThat(next).isCloseTo(PRICE * 2, within(1e-9d));
    }

    @Test
    void negativeDriftAloneShrinksPriceAtRateMu() {
        double sigma = 0.0d;
        double driftPerHour = -Math.log(2d);
        Duration dt = Duration.ofHours(1);

        double next = ps.nextPrice(PRICE, sigma, dt, 1.0d, driftPerHour);

        assertThat(next).isCloseTo(PRICE * 1d / 2d, within(1e-9d));
    }

    private double logStep(Duration dt, double shock) {
        return Math.log(ps.nextPrice(PRICE, SIGMA_PER_SQRT_HOUR, dt, shock, DRIFT_CANCELLING_ITO_CORRECTION) / PRICE);
    }

}
