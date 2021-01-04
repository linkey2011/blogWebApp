FROM java:8
VOLUME /tmp
ADD CSerBlog.jar /test.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/test.jar"]