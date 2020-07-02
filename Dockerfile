FROM alpine/git as clone 
WORKDIR /app4
RUN git clone https://github.com/padmapriyapvld123/ProductService.git

FROM maven:3.5-jdk-8-alpine as build
WORKDIR /app4
COPY --from=0 /app4/ProductService /app4 
RUN mvn install

FROM openjdk:8-jre-alpine
WORKDIR /app4
COPY --from=1 /app4/target/productservice.jar /app4
EXPOSE 8083
ENTRYPOINT ["sh", "-c"]

CMD ["java -jar productservice.jar"]




