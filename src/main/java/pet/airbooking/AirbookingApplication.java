package pet.airbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AirbookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirbookingApplication.class, args);
    }

}
