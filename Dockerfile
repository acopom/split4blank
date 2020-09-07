FROM maven:3-jdk-8-alpine

LABEL maintainer  "Atsuko Yamaguchi <https://researchmap.jp/atsuko>"

COPY Split4Blank.jar /app/Split4Blank.jar

WORKDIR /data

ENTRYPOINT ["java","-jar","/app/Split4Blank.jar"]