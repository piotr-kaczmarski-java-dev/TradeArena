package io.github.piotrkaczmarski.tradearena.marketdata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class InstrumentTest {

    @ParameterizedTest
    @CsvSource({
            "'AAPL', 10.0",
            "'GOOG', 100.0",
            "'MSFT', 0.01",
            "'TSLA', 1.0",
            "'AMZN', 999.99"
    })
    public void correctParametersConstructCorrectInstrument(String symbol, double startingPrice) {
        Instrument instrument = new Instrument(symbol, startingPrice);
        assertEquals(symbol, instrument.symbol());
        assertEquals(startingPrice, instrument.startingPrice());
    }

    @ParameterizedTest
    @CsvSource({
            ", 10.0",
            "'', 10.0",
            "'   ', 10.0",
            "'AAPL', NaN",
            "'AAPL', Infinity",
            "'AAPL', -Infinity"
    })
    public void constructorThrowsExceptionForInvalidParameters(String symbol, double startingPrice) {
        assertThrows(IllegalArgumentException.class, () -> new Instrument(symbol, startingPrice));
    }

    @ParameterizedTest
    @CsvSource({
            "'AAPL', 0.0",
            "'AAPL', 0.009",
            "'AAPL', -1.0"
    })
    public void constructorThrowsExceptionForStartingPriceBelowThreshold(String symbol, double startingPrice) {
        assertThrows(IllegalArgumentException.class, () -> new Instrument(symbol, startingPrice));
    }

}
