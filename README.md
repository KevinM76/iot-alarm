# IoT example project implementing an alarm


## Introduction

This project is using a JEE backend and an AngularJs frontend.

check it out:

```bash
git clone https://github.com/adesso-iot-workshop-2016/iot-alarm.git
```

## Prerequisites
* [npm](https://www.npmjs.org)
* [nodejs](http://nodejs.org)
* [JDK](http://www.oracle.com/technetwork/java/javaee/downloads/index.html)
* [bower](http://bower.io)
* [PhantomJs](http://phantomjs.org)
* [Application Server](http://http://wildfly.org/) or
* [Docker](https://www.docker.com/)


## Configuration of the relayr cloud
Configure your Wunderbar on the relayr cloud. Retrieve the bearer token identifiying you and the device id of the gyroskop of your wunderbar. Update these in src/main/resources/alarm_manager.properties.

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

