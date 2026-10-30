package com.clara_santa_okinawa_associacao.associacao_okinawa_api.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Associação Okinawa")
                        .version("v1")
                        .description("Execute POST /auth/login e copie o campo accesToken da resposta. "
                                + "Clique em Authorize e cole somente o token, sem o prefixo Bearer. "
                                + "Os endpoints /usuarios exigem a permissão TELA_USUARIOS."))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Cole o JWT retornado pelo login, sem o prefixo Bearer.")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
