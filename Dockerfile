FROM openjdk:8-jre-alpine
RUN mkdir -p /opt/app
WORKDIR /opt/app

COPY comic-review-scraper-2.0-assembly.jar ./
RUN chmod +x ./comic-review-scraper-2.0-assembly.jar
ENTRYPOINT ["java","-jar","./comic-review-scraper-2.0-assembly.jar"]
CMD ["all"]
