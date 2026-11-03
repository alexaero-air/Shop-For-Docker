FROM openjdk:17-alpine
WORKDIR /app

# Копируем всё содержимое локальной папки src внутрь контейнера (в текущий WORKDIR)
COPY src/ .

# Компилируем файлы, которые теперь лежат прямо в /app внутри контейнера
RUN javac Main.java Product.java

EXPOSE 8080
CMD ["java", "Main"]
