package com.example.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class Application {

    public static void main(final String[] args) {
        long init = System.currentTimeMillis();
        SpringApplication.run(Application.class, args);
        System.out.println(
            String.format(
                "\n*** --- Application Started | %s | Took: %s milliseconds --- ***\n",
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                (System.currentTimeMillis() - init)
            )
        );
    }
}