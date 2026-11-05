# Etapa 1: Build da aplicação Maven
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Otimização de cache para dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Compilação do código fonte sem executar testes
COPY src ./src
RUN mvn clean package -DskipTests -B

# Etapa 2: Imagem leve de execução (JRE 17 Alpine)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia o artefato construído
COPY --from=builder /app/target/*.jar app.jar

# Configurações de ambiente para o Render (Free Tier com 512MB RAM)
ENV PORT=8080
ENV JAVA_TOOL_OPTIONS="-Xmx384m -Xms192m"

EXPOSE 8080

# Execução da aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]

