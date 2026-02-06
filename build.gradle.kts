plugins {
	java
	id("org.springframework.boot") version "4.0.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.pace"
version = "0.0.1-SNAPSHOT"
description = "PACE_WAKE"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

// Spring Cloud & AI 버전 관리 (BOM)
extra["springCloudVersion"] = "2025.1.1"
extra["springAiVersion"] = "1.1.2"

dependencies {
    // Web & Validation
    // Boot 4.0부터 Virtual Threads가 기본 내장됨 (server.tomcat.threads.max 무의미)
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Security & Auth (SSO/JWT)
    // 카카오/구글 로그인, JWT 토큰 발급 및 검증
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    // JWT 라이브러리 (jjwt)
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")

    // Data Persistence (DB & Cache)
    // MySQL 저장(회원/알람), Redis 캐싱(리캡 데이터/Refresh Token)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    runtimeOnly("com.mysql:mysql-connector-j") // MySQL 드라이버

    // Batch Processing (대용량 처리)
    // 새벽 4시 모닝 리캡 데이터 프리페칭
    implementation("org.springframework.boot:spring-boot-starter-batch")
    testImplementation("org.springframework.batch:spring-batch-test")

    // AI & External API (외부 연동)
    // 뉴스 3줄 요약 (Spring AI), 외부 API 장애 대응 (Resilience4j)

    // Spring AI (OpenAI 연동용)
    implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter")

    // Circuit Breaker (기상청 API 죽었을 때 Fallback 처리용)
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")

    // Utilities & Monitoring (운영)
    // 롬복, 스웨거 문서화, 헬스 체크
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Swagger UI (API 명세서 자동 생성)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")

    // Actuator (AWS ALB 헬스체크 및 메트릭 수집)
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// BOM (Bill of Materials) import
dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
