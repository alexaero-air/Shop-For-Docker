FROM openjdk:17-alpine
WORKDIR /app
COPY *.java .
RUN javac Main.java Product.java
EXPOSE 8080
CMD ["java", "Main"]
