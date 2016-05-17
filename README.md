# IoT example project implementing an alarm


## Introduction

This project is using a JEE backend and an AngularJs frontend. It makes use of relayr Java API.
This API is leveraging RxJava. Details can be found on https://github.com/relayr/java-sdk.


## Prerequisites
* [git](https://git-scm.com/)
* [npm](https://www.npmjs.org)
* [nodejs](http://nodejs.org)
* [JDK](http://www.oracle.com/technetwork/java/javaee/downloads/index.html)
* [Maven](https://maven.apache.org)
* [bower](http://bower.io)
* [PhantomJs](http://phantomjs.org)
* [Application Server](http://http://wildfly.org/) or
* [Docker](https://www.docker.com/)


## Configuration of the relayr cloud
Configure your Wunderbar on the relayr cloud. Retrieve the bearer token identifiying you and the device id of the gyroskop of your wunderbar. The bearer token can be retrieved from the [developer dashboard](https://developer.relayr.io/dashboard/account/general).

Update the token and the device id in [alarm_manager.properties] (https://github.com/adesso-iot-workshop-2016/iot-alarm/blob/master/src/main/resources/alarm_manager.properties).

## Installation
```bash
mvn package
```
The build creates an alarm.war file in the target directory. You can either copy it to your deployment folder of the application server (i.e. <wildfly-install-dir>/standalone/deployments) or build and run a docker image.

## Docker instructions
```bash
docker build -t alarm .
docker run -p 80:8080 alarm
```

