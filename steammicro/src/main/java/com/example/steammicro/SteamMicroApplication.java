package com.example.steammicro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@SpringBootApplication(
        scanBasePackages = {"com.example.steammicro", "com.example.steamapi", "org.example.SteamEvents"},
        exclude = {DataSourceAutoConfiguration.class}
)
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class SteamMicroApplication {
    public static void main(String[] args) {
        SpringApplication.run(SteamMicroApplication.class, args);
    }
}
