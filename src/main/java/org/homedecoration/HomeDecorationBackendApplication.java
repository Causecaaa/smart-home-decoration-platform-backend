package org.homedecoration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class HomeDecorationBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomeDecorationBackendApplication.class, args);
    }

}
