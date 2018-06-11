package edu.sjsu.cmpe275;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SurveyApe {

    public static void main(String[] args) throws Throwable {
        SpringApplication.run(SurveyApe.class, args);
    }

}