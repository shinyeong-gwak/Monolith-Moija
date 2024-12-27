FROM openjdk:17-slim

RUN mkdir /app
RUN groupadd -r app && useradd -r -s /bin/false -g app app
WORKDIR /app
RUN mkdir temp
CMD ["./gradlew", "clean", "build"]
ADD /build/libs/Monolith-Moija-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8100
ENTRYPOINT ["java","-jar","/app/app.jar"]
