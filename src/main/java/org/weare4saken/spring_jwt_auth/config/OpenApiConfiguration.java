package org.weare4saken.spring_jwt_auth.config;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;

@OpenAPIDefinition(
        info = @Info(
                version = "1.0",
                contact = @Contact(
                        name = "Ilya Kondratyuk",
                        url = "https://github.com/weare4saken",
                        email = "kondrashja91@gmail.ru"),
                license = @License(name = "MIT", url = "https://opensource.org/license/mit")
        )
)
@SecurityScheme(
        paramName = "authorization",
        name = "bearerAuth",
        in = SecuritySchemeIn.HEADER,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        ResolvedSchema errorSchema =
                ModelConverters.getInstance().resolveAsResolvedSchema(new AnnotatedType(ProblemDetail.class));

        ProblemDetail example = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Something went wrong");
        example.setInstance(URI.create("/api"));

        Content content =
                new Content().addMediaType("application/json", new MediaType().schema(errorSchema.schema)
                        .example(example));

        return openApi ->
                openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(
                        operation ->
                                operation.getResponses()
                                        .addApiResponse("400", new ApiResponse().description("Bad Request")
                                                .content(content))));
    }
}
