call mvn clean verify sonar:sonar -f ../pom.xml
timeout 5
start "" http://localhost:9000