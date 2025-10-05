package io.aurasage.document.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

   @Value("${spring.application.version}")
   private String appVersion;

   @Bean
   public OpenAPI customOpenAPI() {
      return new OpenAPI()
            .info(new Info()
                  .title("AuraSage Document Service API")
                  .version(appVersion)
                  .description("Document management service for AuraSage platform"))
            .servers(List.of(
                  new Server()
                        .url("http://localhost:9090/documents/v1")
                        .description("API Gateway"),
                  new Server()
                        .url("http://localhost:8081")
                        .description("Direct Access")));
   }
}
