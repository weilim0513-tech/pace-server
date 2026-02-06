package com.pace.server.global.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

	private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(apiInfo())
			.servers(List.of(
				new Server().url("http://localhost:8080").description("Local"),
				new Server().url("https://api.pace-wake.com").description("Production")))
			.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
			.components(new Components()
				.addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme()));
	}

	private Info apiInfo() {
		return new Info()
			.title("PACE Wake API")
			.version("v1.0.0")
			.description("""
				PACE Wake 모닝 루틴 오토메이션 플랫폼 API
				
				## 인증 방식
				- JWT Bearer Token 사용
				- 로그인 후 발급받은 Access Token을 `Authorization: Bearer {token}` 헤더에 포함
				
				## 에러 응답
				모든 에러 응답은 `{ code, message }` 형식을 따름
				""")
			.contact(new Contact()
				.name("PACE Team")
				.email("support@pace.com"));
	}

	private SecurityScheme securityScheme() {
		return new SecurityScheme()
			.name(SECURITY_SCHEME_NAME)
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT")
			.description("JWT Access Token을 입력하세요");
	}
}
