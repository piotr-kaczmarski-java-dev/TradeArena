package io.github.piotrkaczmarski.tradearena.trading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Order API, order book per instrument and the matching engine. */
@SpringBootApplication
public class TradingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradingApplication.class, args);
    }
}
