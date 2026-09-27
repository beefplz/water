# 1단계: 소스를 빌드해 실행 가능한 jar 생성
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# 의존성만 먼저 받아 두면 소스만 바뀌었을 때 이 레이어를 재사용
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew && ./gradlew dependencies --no-daemon > /dev/null

COPY src src
RUN ./gradlew bootJar --no-daemon

# 2단계: 실행에 필요한 JRE와 jar만 담은 이미지
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV TZ=Asia/Seoul

RUN useradd --system --uid 1001 app
COPY --from=build /app/build/libs/*.jar app.jar
USER app

EXPOSE 7355
ENTRYPOINT ["java", "-jar", "app.jar"]
