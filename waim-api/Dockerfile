# 1단계: 빌드 스테이지
FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /builder
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

## COPY PACKAGE
COPY waim-core waim-core
COPY waim-api waim-api
COPY waim-scheduler waim-scheduler

RUN chmod +x ./gradlew && ./gradlew :waim-api:bootJar --no-daemon
RUN java -Djarmode=layertools -jar waim-api/build/libs/*.jar extract

# 2단계: 최종 실행 스테이지
FROM eclipse-temurin:25-jre-alpine
WORKDIR /application

RUN apk add --no-cache curl

# waimuser(1900) 생성 및 보안 강화
RUN addgroup -S waimgroup && adduser -S waimuser -G waimgroup -u 1900
RUN chown waimuser:waimgroup /application

COPY --from=builder --chown=1900:1900 /builder/dependencies/ ./
COPY --from=builder --chown=1900:1900 /builder/spring-boot-loader/ ./
COPY --from=builder --chown=1900:1900 /builder/snapshot-dependencies/ ./
COPY --from=builder --chown=1900:1900 /builder/application/ ./

ENV JVM_OPTS="-XX:+UseZGC -XX:+UnlockExperimentalVMOptions"
USER 1900:1900
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS org.springframework.boot.loader.launch.JarLauncher"]