restful-token-login
========================

What is it?
-----------

This is a sample project using spring security with spring mvc and hibernate to server as a sample for writing a stateless web service backed by Spring security.
Sample can be deployabled as  Maven 3 project. During development  JBoss AS 7.1.1 was used. 

The example uses the `java:jboss/datasources/SpringQuickstartDS` database, configured and deployed by the application.

System requirements
-------------------

All you need to build this project is Java 6.0 (Java SDK 1.6) or better, Maven 3.0 or better.

The application this project produces is designed to be run on JBoss Enterprise Application Platform 6 or JBoss AS 7.1. 




Build and Deploy the Application
-------------------------

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type this command to build and deploy the archive:

        mvn clean package jboss-as:deploy

4. This will deploy `target/restful-token-login.war` to the running instance of the server.


Access the application 
---------------------
 
The application will be running at the following URL: <http://localhost:8080/restful-token-login/>.


Test application
--------------------

Application can be test via browser based rest clients like Simple REST Client from Chrome or command line tools like curl

Example by curl:

Login to service
Request: curl -XPOST -i -k -v --data "username=testuser&password=a12345" http://localhost:8080/restful-token-login/login -> Default login url
Response: 

HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
token: 6116fa82-50cc-436f-b03d-6418b9417a39
Content-Length: 0
Date: Sun, 29 Sep 2013 21:03:05 GMT

Get members
Request: curl -XGET -i -k -H 'token:6116fa82-50cc-436f-b03d-6418b9417a39' http://localhost:8080/restful-token-login/rest/members
Response:

HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sun, 29 Sep 2013 21:04:27 GMT

[{"id":0,"name":"testuser","password":"a12345","email":"john.smith@mailinator.com","phoneNumber":"2125551212"}]


Undeploy the Archive
--------------------

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. When you are finished testing, type this command to undeploy the archive:

        mvn jboss-as:undeploy



