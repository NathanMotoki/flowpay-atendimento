FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Copiar arquivos do Maven
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Baixar dependências
RUN ./mvnw dependency:go-offline

# Copiar código fonte
COPY src ./src

# Build da aplicação
RUN ./mvnw clean package -DskipTests

# Imagem final
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar JAR da etapa de build
COPY --from=build /app/target/*.jar app.jar

# Expor porta
EXPOSE 8080

# Executar aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]