FROM aim-hub-cn-shanghai.cr.volces.com/basic/amazoncorretto:21
COPY target/mall-*.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]