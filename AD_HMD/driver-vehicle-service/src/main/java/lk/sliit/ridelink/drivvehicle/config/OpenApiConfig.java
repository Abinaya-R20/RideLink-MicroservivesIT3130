package lk.sliit.ridelink.drivvehicle.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ridelinkOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("RideLink Driver & Vehicle Service API")
                .description("RESTful API for managing driver operational profiles, vehicles, availability, location, and eligible driver retrieval for the RideLink ride-sharing platform.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Member 2")
                    .email("member2@sliit.lk")
                )
            );
    }
}
