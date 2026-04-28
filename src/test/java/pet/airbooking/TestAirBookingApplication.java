package pet.airbooking;

import org.springframework.boot.SpringApplication;

public class TestAirBookingApplication {

    public static void main(String[] args) {
        SpringApplication.from(AirbookingApplication::main).run(args);
    }

}
