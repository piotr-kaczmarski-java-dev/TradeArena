package io.github.piotrkaczmarski.tradearena.marketdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Simulates stock prices and publishes ticks to Kafka. */
@SpringBootApplication
public class MarketDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketDataApplication.class, args);
    }
}
