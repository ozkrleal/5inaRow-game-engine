# 5inaRow-Engine
it is part of microservice project for 5 in row game. 


## installation
1-installation of JDK : https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html#A1097257

2- installation of maven for mac , linux , windows , docker : https://maven.apache.org/install.html


### building the .jar file with maven
1st check if you correctly installed maven on your computer:
````
mvn -version
````

2nd build the mvn packages
````
mvn clean package
````

3rd generate the .jar file
````
java -jar target/engine-0.0.1-SNAPSHOT.jar
````


### running the service
````
mvn spring-boot:run
````