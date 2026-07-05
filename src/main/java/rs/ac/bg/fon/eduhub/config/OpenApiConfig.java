package rs.ac.bg.fon.eduhub.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguracija OpenAPI/Swagger dokumentacije koja definiše JWT Bearer
 * autentifikacionu šemu, primenjenu globalno na sve endpointe. Ovo
 * omogućava korišćenje dugmeta "Authorize" u Swagger UI-ju za unos JWT
 * tokena i testiranje zaštićenih endpointa direktno iz dokumentacije.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Configuration
@OpenAPIDefinition(
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}