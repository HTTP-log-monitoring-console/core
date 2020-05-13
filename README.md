# Application : HTTP log monitoring console program
                     
## Requirements 

Consume an actively written-to w3c-formatted HTTP access log (https://www.w3.org/Daemon/User/Config/Logging.html). It should default to reading /tmp/access.log and be overrideable
Example log lines:

```
127.0.0.1 - james [09/May/2018:16:00:39 +0000] "GET /report HTTP/1.0" 200 123
127.0.0.1 - jill [09/May/2018:16:00:41 +0000] "GET /api/user HTTP/1.0" 200 234
127.0.0.1 - frank [09/May/2018:16:00:42 +0000] "POST /api/user HTTP/1.0" 200 34
127.0.0.1 - mary [09/May/2018:16:00:42 +0000] "POST /api/user HTTP/1.0" 503 12
```
 
* Display stats every 10s about the traffic during those 10s: the sections of the web site with the most hits, as well as interesting summary statistics on the traffic as a whole. A section is defined as being what's before the second '/' in the resource section of the log line. For example, the section for "/pages/create" is "/pages"
* Make sure a user can keep the app running and monitor the log file continuously
* Whenever total traffic for the past 2 minutes exceeds a certain number on average, print or display a message saying that “High traffic generated an alert - hits = {value}, triggered at {time}”. The default threshold should be 10 requests per second, and should be overridable
* Whenever the total traffic drops again below that value on average for the past 2 minutes, print or display another message detailing when the alert recovered
* Write a test for the alerting logic
* Explain how you’d improve on this application design

## Proposed solution

Java based application using Java 9 runtime environment supporting console (tested on OSX & Windows OSes and Linux-compatible).

Maven is used for dependency control, Log4J2 is used for logging to console / file, Google's Lanterna library is used to create a console text-based GUI.

Reasons for selecting each different framework or library from a list of choices are given below in the 

### Usage

To launch the application please run the following files, depending on your OS :
* for OSX, runApplication.sh
* for Windows, runApplication.bat

Closing the console GUI is done either by closing the Terminal / Windows Prompt or by using the Ctrl+C key combination.

Available arguments for the application's launcher are displayed using the --h command :
```
./runApplication.sh --h
```

### Building the solution

Using Maven it is easy to build the application with the following command:
```
mvn clean install
```
The application's jar (core-1.0-snapshot.jar) is built in the /target folder of the project's root folder.

### Running tests

The following Maven command will run all JUnit tests available in the application : 
```
mvn test
```

### Generating random logs

An already existing 3rd party application (log-generator 1.0.2; https://pypi.org/project/log-generator/) does the log generation according to a configuration file (found in the source code).
The generator requires Python 3 & pip3 to install and run.

For easy use, I have included the source code of the generator in the external/log_generator folder and added scripts (generate_logs.sh/bat) to launch the log generation.

All rights to this log generator code belong to Peter Scopes (https://pypi.org/project/log-generator/). 

## Application structure & technical design

There are six main parts in the application, organized in two external modules and three internal ones.

External modules contain the file reading logic and the log parse: 
* the file reader logic ([code](src/main/java/com/filereader))
* the CLF log parser ([code](src/main/java/com/clfparser))

The body of the application contains four parts addressing the requirements:
* the traffic stats logic ([code](src/main/java/com/homework/monitoring/stats))
* the alerts logic ([code](src/main/java/com/homework/monitoring/alerts))
* general logic linking the main objects together ([code](src/main/java/com/homework/monitoring))
* the UI ([code](src/main/java/com/homework/ui)

A tool class is available in the [utils](src/main/java/com/utils/ConversionUtils.java) class.

The main class launching the application is the [StartApplication.java](src/main/java/com/homework/StartApplication.java) class.

## Technology / library / framework choices
### Maven
Maven is a dependency management system and build automation tool highly compatible with Java, using it for the project was easy as it integrated with multiple IDEs, including the IDEA Intellij IDE I used.

Using it allowed to easily update the different project dependencies and compile / run tests.

### Lanterna
This library is one of the better supported terminal GUI development frameworks according to different articles found on the Web (see References for links).
Using it was easy and required almost no specific ramp up.

## Possible improvements
To create a real-use application multiple things must be improved as a command line application is not very useful. Even if it was used only locally on a server it would require constant visual monitoring, which would quickly become tiring.

Possible logic changes:
* integrate output with a webserver (either directly or by publishing REST APIs)
* integrate an easy email server to send emails when alerts are raised
* have configurable alert levels, either by config file or by UI access
* add tracking for users / IPs emitting requests
* raise different alerts for activities such as spiders, heavily accessed resources, most often not found resources etc.

Technical improvements:
* connect to web server directly to be notified of traffic through open APIs (without going through disk access)
* export examined data either locally or to a web storage (S3 etc) so multiple servers' traffic could be aggregated in a general view of network traffic
* export UI events to a REST API so the UI could connect to multiple log monitoring applications (either aggregating results or by one-to-one connections).
* implement it as an OS service, launched at OS start and restarted automatically

## References
[Lanterna]
* http://mabe02.github.io/lanterna/apidocs/3.0/com/googlecode/lanterna/gui2/package-summary.html
* http://rememberjava.com/cli/2017/01/22/ncurses_terminal_libs.html
* https://medium.com/@giorgosbg/text-based-gui-with-lanterna-in-java-c8a754187fb1
* https://github.com/mabe02/lanterna

[CLF log format]
* https://en.wikipedia.org/wiki/Common_Log_Format
* Log generator : https://pypi.org/project/log-generator/

[General inspiration]
* https://goaccess.io/
