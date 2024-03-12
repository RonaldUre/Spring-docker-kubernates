FROM openjdk:11 as builder

WORKDIR /app/msvc-usuarios

COPY ./msvc-usuarios/.mvn ./.mvn
COPY ./msvc-usuarios/mvnw .
COPY ./msvc-usuarios/pom.xml .

RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/
#RUN ./mvnw dependency:go-offline

COPY ./msvc-usuarios/src ./src

RUN ./mvnw clean package -DskipTests


FROM openjdk:11

WORKDIR /app
RUN mkdir ./logs

ARG MSVC_NAME=msvc-usuarios

COPY --from=builder /app/${MSVC_NAME}/target/msvc-usuarios-0.0.1-SNAPSHOT.jar .

ARG PORT_APP=8001

ENV PORT ${PORT_APP}

EXPOSE ${PORT}

CMD sleep 30 && java -jar msvc-usuarios-0.0.1-SNAPSHOT.jar
#CMD ["java", "-jar", "msvc-usuarios-0.0.1-SNAPSHOT.jar"]
