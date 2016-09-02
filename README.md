# Android HTTP Server

Small but powerful multithread web server written completely in Java SE and then ported to Android.

The server implements most of the HTTP 1.1 specification and ses its own specification of Servlets for handling dynamic pages.
Servlets support cookies, sessions, file uploads and anything else to build a common web application.

It can be used as a standalone web server for static content or as a remote application back-office engine that can be accessed from web.

## Key features

* Small footprint, requires no external libraries
* Handles HTTP requests in separate threads
* Supports dynamic pages via Servlets (own specification)
* Implements Servlet Pool for memory optimisation and resource reuse
* Support for GET, POST, HEAD methods
* Supports KEEP-ALIVE connections
* Full support for mime types (uses Apache mime.type)
* Supports buffered file upload (multipart requests)
* Exposes compact API for handling sessions

## Building application

```
gradlew -b app/build.gradle build
```

## Sample code

Hello World servlet

```java
package example;

import ro.polak.webserver.servlet.HTTPRequest;
import ro.polak.webserver.servlet.HTTPResponse;
import ro.polak.webserver.servlet.Servlet;

public class HelloWorld extends Servlet {

    @Override
    public void service(HTTPRequest request, HTTPResponse response) {
        response.getPrintWriter().print("Hello World!");
    }
}
```

## Screens

![Admin main activity](screens/main.png)
![HTTP back-office login](screens/admin-login.png)
![HTTP back-office menu](screens/admin-menu.png)

![HTTP back-office drive access](screens/admin-drive-access.png)
![HTTP back-office server statistics](screens/admin-server-statistics.png)
![HTTP back-office SMS inbox](screens/admin-sms-inbox.png)

### 500 error page trace
![Servlet error 500](screens/servlet-error-500.png)

## Sample script to send SMS using wget

If you want to send a real SMS please remove "&test=1" from the POST params.

```bash
SERVER_IP=192.168.1.1; SERVER_PORT=8080; echo "Phone number:"; read TO; echo "Message:"; read MESSAGE; wget -qO- --post-data "to=$TO&message=$MESSAGE&test=1" http://$SERVER_IP:$SERVER_PORT/api/SmsSend.dhtml
```
