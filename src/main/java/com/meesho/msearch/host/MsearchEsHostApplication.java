package com.meesho.msearch.host;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.meesho.msearch.host", "com.meesho.msearch.es"})
public class MsearchEsHostApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsearchEsHostApplication.class, args);
    }
}
