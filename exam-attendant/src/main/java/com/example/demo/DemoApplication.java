package com.example.demo;

import com.example.demo.model.Exam;
import com.example.demo.repository.ExamRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    // Seed initial data if exams are empty so the dropdown has options
    @Bean
    public CommandLineRunner dataLoader(ExamRepository examRepo) {
        return args -> {
            if (examRepo.count() == 0) {
                Exam e1 = new Exam();
                e1.setName("Math 101");
                e1.setDuration(90);
                e1.setDescription("Basic Mathematics");
                examRepo.save(e1);

                Exam e2 = new Exam();
                e2.setName("Physics 202");
                e2.setDuration(120);
                e2.setDescription("Advanced Physics");
                examRepo.save(e2);
            }
        };
    }
}
