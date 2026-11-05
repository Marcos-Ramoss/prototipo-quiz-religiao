package com.quiz.religiousquiz;

import com.quiz.religiousquiz.config.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReligiousQuizApplication {

    public static void main(String[] args) {
        DotenvLoader.load();
        SpringApplication.run(ReligiousQuizApplication.class, args);
    }
}
