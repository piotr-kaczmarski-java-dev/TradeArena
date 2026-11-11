package io.github.piotrkaczmarski.tradearena.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Single entry point for the UI: authentication and routing (Spring Cloud Gateway arrives in Phase 4). */
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
