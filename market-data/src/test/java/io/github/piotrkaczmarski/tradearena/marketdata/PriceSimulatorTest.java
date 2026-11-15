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
    private static final Duration ONE_SECOND = Duration.ofSeconds(1);

    private final PriceSimulator ps = new PriceSimulator();

    // --- argument validation ---

    @ParameterizedTest(name = "price = {0}")
    @ValueSource(doubles = { Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 0.0d, -1.0d })
    void rejectsInvalidPrice(double price) {
        assertThatThrownBy(() -> ps.nextPrice(price, SIGMA_PER_SQRT_HOUR, ONE_SECOND, 0.5d))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("price");
    }

    @ParameterizedTest(name = "sigma = {0}")
    @ValueSource(doubles = { Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, -0.1225d })
    void rejectsInvalidSigma(double sigma) {
        assertThatThrownBy(() -> ps.nextPrice(PRICE, sigma, ONE_SECOND, 0.5d))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sigma");
    }

    @ParameterizedTest(name = "z = {0}")
    @ValueSource(doubles = { Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY })
    void rejectsInvalidZ(double z) {
        assertThatThrownBy(() -> ps.nextPrice(PRICE, SIGMA_PER_SQRT_HOUR, ONE_SECOND, z))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("z");
    }

    @ParameterizedTest(name = "dt = {0} s")
    @ValueSource(longs = { 0L, -1L })
    void rejectsNonPositiveDt(long seconds) {
        assertThatThrownBy(() -> ps.nextPrice(PRICE, SIGMA_PER_SQRT_HOUR, Duration.ofSeconds(seconds), 0.5d))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dt");
    }

    @Test
    void rejectsNullDt() {
        assertThatThrownBy(() -> ps.nextPrice(PRICE, SIGMA_PER_SQRT_HOUR, null, 0.5d))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("dt");
    }

    // --- result range (valid inputs whose product overflows or underflows) ---

    @Test
    void throwsWhenStepOverflowsToInfinity() {
        assertThatThrownBy(() -> ps.nextPrice(PRICE, 1e6d, ONE_SECOND, 10.0d))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void throwsWhenStepUnderflowsToZero() {
        assertThatThrownBy(() -> ps.nextPrice(PRICE, 1e6d, ONE_SECOND, -10.0d))
                .isInstanceOf(ArithmeticException.class);
    }

    // --- behaviour with realistic sigma and a 1 s tick ---

    @Test
    void staysPositiveWhenZIsVeryNegative() {
        double newPrice = ps.nextPrice(PRICE, SIGMA_PER_SQRT_HOUR, ONE_SECOND, -50.0d);

        assertThat(newPrice).isGreaterThan(0.0d);
    }

    @Test
    void returnsSamePriceWhenZIsZero() {
        double newPrice = ps.nextPrice(PRICE, SIGMA_PER_SQRT_HOUR, ONE_SECOND, 0.0d);

        assertThat(newPrice).isEqualTo(PRICE); // exp(0) is exactly 1.0, so exact comparison is safe
    }

    @Test
    void risesWhenZIsPositive() {
        double newPrice = ps.nextPrice(PRICE, SIGMA_PER_SQRT_HOUR, ONE_SECOND, 1.0d);

        assertThat(newPrice).isGreaterThan(PRICE);
    }

    @Test
    void fallsWhenZIsNegative() {
        double newPrice = ps.nextPrice(PRICE, SIGMA_PER_SQRT_HOUR, ONE_SECOND, -1.0d);

        assertThat(newPrice).isLessThan(PRICE);
    }

    @Test
    void oneSecondStepIsAbout0_00204() {
        double logStep = logStep(ONE_SECOND, 1.0d);

        assertThat(logStep).isCloseTo(0.00204d, within(0.00001d));
    }

    @Test
    void quadruplingDtDoublesLogStep() {
        double oneSecond = logStep(Duration.ofSeconds(1), 1.0d);
        double fourSeconds = logStep(Duration.ofSeconds(4), 1.0d);

        assertThat(fourSeconds / oneSecond).isCloseTo(2.0d, within(1e-9d)); // sqrt(4) = 2, not 4
    }

    @Test
    void subMillisecondDtStillMovesPrice() {
        double newPrice = ps.nextPrice(PRICE, SIGMA_PER_SQRT_HOUR, Duration.ofNanos(500_000), 1.0d);

        assertThat(newPrice).isGreaterThan(PRICE);
    }

    private double logStep(Duration dt, double z) {
        return Math.log(ps.nextPrice(PRICE, SIGMA_PER_SQRT_HOUR, dt, z) / PRICE);
    }

}
