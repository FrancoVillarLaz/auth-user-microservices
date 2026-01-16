FROM eclipse-temurin:21-jdk

# Define el directorio de trabajo
WORKDIR /app

# Copia el único archivo JAR de target/ y lo renombra como 'app.jar' en el destino.
# El origen (target/*.jar) debe existir en tu máquina local donde ejecutas docker compose build.
COPY target/*.jar /app/app.jar

EXPOSE 8080

# Ejecuta el archivo 'app.jar'
ENTRYPOINT ["java", "-jar", "app.jar"]