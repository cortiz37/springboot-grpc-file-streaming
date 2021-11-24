FROM openjdk:15-alpine
ADD build/libs/pub-sub-connector.jar /app.jar
CMD java $JAVA_OPTS -jar app.jar $APP_OPTS
