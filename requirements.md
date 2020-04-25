# core
https://www.w3.org/Daemon/User/Config/Logging.html
https://app.greenhouse.io/tests/b639fcefdf3777ee2d949923ba06ef4f

Datadog | Engineering Team Lead
ASSIGNMENT:
Hi Sergiu,

As mentioned by Alison, we would like for you to complete a Take Home Project.

We ask that you try to complete this within one week, however, we understand other calendar constraints can arise, so please let me know when we can possibly anticipate a submission from you. We’ve found quality submissions take between 4-8 hours.

Take a look at the prompt below and feel free to email Alison or myself, Helene, with any questions. Thanks again.

Best,

Helene

 

***********************************

HTTP log monitoring console program

***********************************

 

At Datadog, we value working on real solutions to real problems, and as such we think the best way to understand your capabilities is to give you the opportunity to solve a problem similar to the ones we solve on a daily basis. As the next step in our process, we ask that you write a simple console program that monitors HTTP traffic on your machine. Treat this as an opportunity to show us how you would write something you would be proud to put your name on. 

Consume an actively written-to w3c-formatted HTTP access log (https://www.w3.org/Daemon/User/Config/Logging.html). It should default to reading /tmp/access.log and be overrideable
Example log lines:

127.0.0.1 - james [09/May/2018:16:00:39 +0000] "GET /report HTTP/1.0" 200 123

127.0.0.1 - jill [09/May/2018:16:00:41 +0000] "GET /api/user HTTP/1.0" 200 234

127.0.0.1 - frank [09/May/2018:16:00:42 +0000] "POST /api/user HTTP/1.0" 200 34

127.0.0.1 - mary [09/May/2018:16:00:42 +0000] "POST /api/user HTTP/1.0" 503 12

 

Display stats every 10s about the traffic during those 10s: the sections of the web site with the most hits, as well as interesting summary statistics on the traffic as a whole. A section is defined as being what's before the second '/' in the resource section of the log line. For example, the section for "/pages/create" is "/pages"
Make sure a user can keep the app running and monitor the log file continuously
Whenever total traffic for the past 2 minutes exceeds a certain number on average, print or display a message saying that “High traffic generated an alert - hits = {value}, triggered at {time}”. The default threshold should be 10 requests per second, and should be overridable
Whenever the total traffic drops again below that value on average for the past 2 minutes, print or display another message detailing when the alert recovered
Write a test for the alerting logic
Explain how you’d improve on this application design