package com.quiz.religiousquiz;

import com.quiz.religiousquiz.config.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReligiousQuizApplication {

    public static void main(String[] args) {
        DotenvLoader.load();
        SpringApplication.run(ReligiousQuizApplication.class, args);
        System.out.println("Swagger-local: http://localhost:8080/swagger-ui/index.html");
        System.out.println("Swagger-produção: https://prototipo-quiz-religiao.onrender.com/swagger-ui.html");
    }
}
