
#!/bin/bash

mvn clean package

java -jar target/engine-0.0.1-SNAPSHOT.jar
