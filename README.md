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

This SOS implementation has been extended to properly suit the requirements of the Redch project use case. A new extension has been added in order to provide a RabbitMQ client. It is a wrapper of the official RabbitMQ client.

Contained in the `amqp-service` project submodule, wihtin the `extensions` module, it comes with an example properties file which can be found in `src/main/resources` folder. These properties are the following:

    # URL of the RabbitMQ server
    redch.amqp.host=192.168.0.20

    # Name of the exchange to where the observations should be published
    redch.amqp.exchange=observations

An updated copy of this file must be stored in the $HOME folder of the user responsible for the execution of the Java web application, with the name of `redch.properties`

#### Integration

This service has been integrated with the SOS by using a subclass of the corresponding request handler. As we are interested to push every observation received to a queue, we have extend `ObservationsPostRequestHandler` in `RedchObservationsPostRequestHandler` of the rest binding.


## Usage

First of all, deploy the Java Webapp with the command `mvn clean tomcat:deploy` or `mvn clean tomcat:undeploy tomcat:deploy` if it already exists. Then configure it using the webapp. Just browse to <tomcat_url>/webapp and follow the wizard steps.

Redch SOS talks to RabbitMQ and Postgresql. So besides setting them up and running, make sure the `redch.amqp.host` property of the `redch.properties` file points to the right the RabbitMQ server. As for the DB, make sure the field `Host` of the SOS webapp **Datasource settings** section points to the righ host.
