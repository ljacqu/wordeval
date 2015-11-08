call mvn clean verify sonar:sonar -f ../pom.xml
sleep 5
start "" http://localhost:9000