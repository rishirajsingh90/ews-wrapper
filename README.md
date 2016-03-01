## Gently Tell Folks Out (of meeting rooms) Backend

## Getting Started
* Open `application.properties`
* Modify `exchange.username` and `exchange.password` to be production credentials. Until we receive an outlook service account,
we have to make do by locally modifying this prop file and testing
* Add a new file under `src/main/resources/application.properties` with meeting room names/emails from your Microsoft Exchange directory. A sample template is shown below:

```
exchange:
  url: https://outlook.office365.com/EWS/Exchange.asmx
  username: username@company.com
  password: password
  assist:
    subject: URGENT: Assistance Needed
    content: A guest requires your assistance.
  timezone: America/Chicago

floors:
  - floor: FIFTY_ONE
    meetingRooms:
      - email: room1@company.com
        name: room1
      - email: room2@company.com
        name: room2
      - email: room3@company.com
        name: room3
  - floor: FIFTY_THREE
    meetingRooms:
      - email: room1@company.com
        name: room1
      - email: room2@company.com
        name: room2
      - email: room3@company.com
        name: room3
```

### IntelliJ
These are steps to run/debug the project via an IDE like IntelliJ:
 * Import Project
 * **Go to project structure and update the Java SDK to 1.8.**
 * Select the project's pom.xml
 * Right click on the `Application.java`
 * Run `Application.main()`

### CLI
These are steps to run the project via a command line interface:
 ```
 * mvn clean package
 * java -jar target/ems-wrapper-0.1.0.jar
 ```
* Point your browser to http://localhost:8080/rest/meetingRoom/lookup/{roomEmailAddress} to retrieve the schedule for a specific meeting room
* Point your browser to http://localhost:8080/rest/meetingRoom/availability/{floorNumber} to retrieve a list of all available meeting rooms

### Sample request/responses
http://localhost:8080/rest/meetingRoom/lookup/chibronzeville@slalom.com

```
[{"email":"Mark Tomaszewski ","startDate":"2016-01-26T08:45","endDate":"2016-01-26T10:00"},{"email":"Ben Edmiston ","startDate":"2016-01-26T10:00","endDate":"2016-01-26T11:00"},{"email":"Nihara Vankayala ","startDate":"2016-01-26T11:00","endDate":"2016-01-26T11:30"},{"email":"Nigel Caine ","startDate":"2016-01-26T11:30","endDate":"2016-01-26T12:30"},{"email":"Jim Trahanas ","startDate":"2016-01-26T13:00","endDate":"2016-01-26T15:00"},{"email":"Jennifer Bierman ","startDate":"2016-01-26T15:30","endDate":"2016-01-26T17:00"}]
```

http://localhost:8080/rest/meetingRoom/availability/FIFTY_THREE

```
["Green line","Orange Line","Red Line","Washington"]
```

http://localhost:8080/rest/meetingRoom/availability/FIFTY_ONE?startTime=2016-01-27T16:30

```
["Bucktown","Goose Island","Purple Line","Ravenswood","Wrigleyville"]
```

## Expected Responses
* This web application now returns a list of meeting room statuses for the current day.
* If you are not seeing any responses, or see an error response returned, try the following:
    1. If you receive a 401, ensure that you have updated your credentials on `application.properties`
    2. If you see an empty response, make sure you've correctly entered a meeting room email address as a path parameter and you're testing
against a day that is _not_ a weekend ;)