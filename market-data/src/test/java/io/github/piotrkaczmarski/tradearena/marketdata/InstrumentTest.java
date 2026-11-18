package io.github.piotrkaczmarski.tradearena.marketdata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class InstrumentTest {

    @ParameterizedTest
    @CsvSource({
            "'GAMS', 10.0",
            "'CMPS', 100.0",
            "'BOKS', 0.01",
            "'TOYS', 1.0",
            "'FUDS', 999.99"
    })
    public void correctParametersConstructCorrectInstrument(String symbol, double startingPrice) {
        Instrument instrument = new Instrument(symbol, startingPrice, 0.2d, 0.1d);
        assertEquals(symbol, instrument.symbol());
        assertEquals(startingPrice, instrument.startingPrice());
    }

    @ParameterizedTest
    @CsvSource({
            /*
             * ", 10.0",
             * "'', 10.0",
             * "'   ', 10.0",
             * "'GAMS', NaN",
             * "'GAMS', Infinity",
             * "'GAMS', -Infinity",
             * "'GAMS', -1.0"
             */
            ", 10.0, 0.2, 0.1",
            "'', 10.0, 0.2, 0.1",
            "'   ', 10.0, 0.2, 0.1",
            "'GAMS', NaN, 0.2, 0.1",
            "'GAMS', Infinity, 0.2, 0.1",
            "'GAMS', -Infinity, 0.2, 0.1",
            "'GAMS', -1.0, 0.2, 0.1",
            "'GAMS', 10.0, NaN, 0.1",
            "'GAMS', 10.0, Infinity, 0.1",
            "'GAMS', 10.0, -Infinity, 0.1",
            "'GAMS', 10.0, -0.1, 0.1",
            "'GAMS', 10.0, 0.2, NaN",
            "'GAMS', 10.0, 0.2, Infinity",
            "'GAMS', 10.0, 0.2, -Infinity"
    })
    public void constructorThrowsExceptionForInvalidParameters(
            String symbol,
            double startingPrice,
            double sigmaPerSqrtHour,
            double driftPerHour) {
        assertThrows(IllegalArgumentException.class,
                () -> new Instrument(symbol, startingPrice, sigmaPerSqrtHour, driftPerHour));
    }

    @ParameterizedTest
    @CsvSource({
            "'GAMS', 0.0",
            "'GAMS', 0.009",
            "'GAMS', -1.0"
    })
    public void constructorThrowsExceptionForStartingPriceBelowThreshold(String symbol, double startingPrice) {
        assertThrows(IllegalArgumentException.class, () -> new Instrument(symbol, startingPrice, 0.2d, 0.1d));
    }

}
