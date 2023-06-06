FROM openjdk:8-jdk

COPY build/libs/education.jar education.jar

CMD ["java", "-jar", "education.jar"]
