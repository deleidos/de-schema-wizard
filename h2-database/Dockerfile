FROM java:8
EXPOSE 9123
COPY schwiz-h2.jar /usr/local/h2/
WORKDIR /usr/local/h2/
CMD ["java", "-classpath", "schwiz-h2.jar", "com.deleidos.hd.h2.H2Database", "-tcpAllowOthers", "-tcpPort", "9123" ]
