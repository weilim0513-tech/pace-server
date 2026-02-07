# Multi-stage Build for Optimized Image Size

# Stage 1: Build
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

# Gradle Wrapper 복사 (캐시 활용)
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# 의존성만 먼저 다운로드 (레이어 캐싱)
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true

# 소스 코드 복사 및 빌드
COPY src src
RUN ./gradlew bootJar -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:25-jre

WORKDIR /app

# 비 root 사용자 생성
RUN groupadd -r pace && useradd -r -g pace pace

# 빌드 결과물 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 소유권 변경
RUN chown pace:pace app.jar

# 사용자 전환
USER pace

# 헬스체크
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Virtual Threads 최적화 JVM 옵션
ENV JAVA_OPTS="-XX:+UseZGC \
               -XX:MaxRAMPercentage=75.0 \
               -Djava.security.egd=file:/dev/./urandom"

# 포트 노출
EXPOSE 8080

# 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
