FROM openjdk:15-alpine
ADD build/libs/springboot-grpc-file-streaming.jar /app.jar
CMD java $JAVA_OPTS -jar app.jar $APP_OPTS
