package io.github.piotrkaczmarski.tradearena.marketdata;

public record Instrument(String symbol, double startingPrice) {
    public Instrument {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("symbol must not be null or blank");
        }
        if (Double.isNaN(startingPrice) || Double.isInfinite(startingPrice)) {
            throw new IllegalArgumentException("startingPrice must be a valid number");
        }
        if (startingPrice < 0.01d) {
            throw new IllegalArgumentException("startingPrice must be greater or equal to 0.01");
        }
    }
}
