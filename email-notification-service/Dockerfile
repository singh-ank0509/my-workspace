#FROM openjdk:11
#RUN apt-get update && \
#    apt-get install -y tzdata && \
#    ln -sf /usr/share/zoneinfo/Asia/Kolkata /etc/localtime && \
#    echo "Asia/Kolkata" > /etc/timezone && \
#    dpkg-reconfigure -f noninteractive tzdata && \
#	apt-get install -y curl
 
#CMD ["bash"]
#WORKDIR /app
#COPY Email-Service-0.0.1-SNAPSHOT.jar /app/Email-Service-0.0.1-SNAPSHOT.jar
#EXPOSE 9090
#ENTRYPOINT ["java", "-jar", "Email-Service-0.0.1-SNAPSHOT.jar"]

FROM openjdk:11
RUN apt-get update && apt-get install -y tzdata
ENV TZ=Asia/Kolkata
RUN ln -snf /usr/share/zoneinfo/Asia/Kolkata /etc/localtime && echo "Asia/Kolkata" > /etc/timezone
EXPOSE 9090
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} Email-Service.jar
ENTRYPOINT ["java","-jar","/Email-Service.jar"]
