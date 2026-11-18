package io.github.piotrkaczmarski.tradearena.marketdata;

public record Instrument(String symbol, double startingPrice, double sigmaPerSqrtHour, double driftPerHour) {
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
        if (!Double.isFinite(sigmaPerSqrtHour) || sigmaPerSqrtHour < 0) {
            throw new IllegalArgumentException("sigmaPerSqrtHour must be finite and non-negative");
        }
        if (!Double.isFinite(driftPerHour)) {
            throw new IllegalArgumentException("driftPerHour must be finite");
        }
    }
}
