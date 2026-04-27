package pet.airbooking;

import org.springframework.boot.SpringApplication;

public class TestAirbookingApplication {

    public static void main(String[] args) {
        SpringApplication.from(AirbookingApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
