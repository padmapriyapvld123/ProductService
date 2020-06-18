From openjdk:8
copy ./target/productservice.jar productservice.jar
CMD ["java","-jar","productservice.jar"]

