package pl.tzason.complaint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ComplaintServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ComplaintServiceApplication.class, args);
    }
}