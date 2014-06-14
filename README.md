# Redch SOS

This is an sligthly modifed version of the [52°North SOS 4.0.](0https://wiki.52north.org/bin/view/SensorWeb/SensorObservationServiceIVDocumentation) which implements the
  [OGC Sensor Observation Service (SOS)](http://www.opengeospatial.org/standards/sos) standard version 2.0.0. It aims to adapt 52ºNorth SOS implementation to fullfill the needs of the Redch project.

## Installation


### Database

You must create a Postgres PostGIS database named `sos`. To do so, connect to postgres and create the database. Then, connect to it and add the PostGIS extension.

    $ psql -h localhost

    =# CREATE DATABASE sos;
    =# \connect sos;
    =# CREATE EXTENSION postgis;

    # Quit from the DB
    =# \q

### AMQP Service extension

This SOS implementation has been extended to suit the requirements of the Redch project. A RabbitMQ client wrapper has been added as extension.

Contained in the [amqp-service](https://github.com/sauloperez/sos/tree/master/src/extensions/amqp-service) submodule of the `extensions` module, it comes with an example properties file which can be found in the [config folder](https://github.com/sauloperez/sos/tree/master/config). These properties are the following:

    # URL of the RabbitMQ server
    redch.amqp.host=192.168.0.20

    # Name of the exchange to where the observations should be published
    redch.amqp.exchange=observations

An updated copy of this file must be stored in the same folder named as `redch.properties`.

**Note:** Make sure you compile the `amqp-service` submodule whenever you change it before compiling the whole SOS. You can do so with the `mvn install` command.

#### Integration

This service has been integrated with the SOS by using a subclass of the corresponding request handler. As every observation received must be published into the queue, the [ObservationsPostRequestHandler](https://github.com/sauloperez/sos/blob/master/src/bindings/rest/code/src/main/java/org/n52/sos/binding/rest/resources/observations/ObservationsPostRequestHandler.java) with [RedchObservationsPostRequestHandler](https://github.com/sauloperez/sos/blob/mastero/src/bindings/rest/code/src/main/java/org/n52/sos/binding/rest/resources/observations/RedchObservationsPostRequestHandler.java) of the rest binding, which connects to the AMQP Service.

## Usage

### Development

First of all, deploy the Java Webapp with the command `mvn clean tomcat:deploy` or `mvn clean tomcat:undeploy tomcat:deploy` if it already exists. Then configure it using the webapp. Just browse to `<tomcat_url>/webapp` and follow the wizard's steps.

Redch SOS talks to RabbitMQ and Postgresql. So besides setting them up and running, make sure the `redch.amqp.host` property of the `redch.properties` file points to the right the RabbitMQ server. As for the DB, make sure the field `Host`  under **Datasource settings** section of the SOS administrative backend points to the right host.


### Production

In production the deployment process is automatized using [Capistrano](http://capistranorb.com/). To deploy just type the following command from your machine:

	cap production deploy

This essentially runs commands on the remote server through SSH. Once the process is finished, point your browser to `<production_server>/webapp` and fill up the wizard fields like in development.



## Testing

The AMQPService extensions as well as the SOS itself come with unit tests made with JUnit. It is recommended to run them from your IDE of choice. Most of them have JUnit plugins available.

The tests can be found in the `src/test` folder of every module.