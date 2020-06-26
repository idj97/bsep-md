# BSEP-MM / BSEP-MD


## Demo:
- https://www.youtube.com/watch?v=v7c1azRPKJc


## Overview:

University project, a military monitoring system consisted of the following subsystems:

- Backend
  - PublicKeyInfrastructure - spring boot app for creating, manipulating and distributing certificates.
  - SiemAgent - spring boot app, simulates a program used for reading OS logs and sending them to the Siem Center.
  - SiemCenter - spring boot app, used to gather, filter and process logs, and trigger alarms occasionally.
  - SiemCenterKjar - dependency for storing drools rules for alarm triggering.
  - Simulator - python project, stores logs in a special folder in order to simulate healthy app state, and malicious state.
  
- Frontend
  - PKI-client - angular app that communicates with the PublicKeyInfrastructure. An administrator creates, views and distributes certificates.
  - Siem-center-client - angular app that communicates with the SiemCenter. The operator views reports, logs and alarms, while the admin also creates rules to trigger alarms.
  
- External services:
  - Keycloak - user authentication and authorization.
  
  
## Getting started:
- Download and start keycloak server.
- Navigate to localhost:8080, configure PKI and SiemCenter realms defined in application.properties.
- Start PublicKeyInfrastructure front end and back end servers.
- Download server certificates, and distribute them to the SiemCenter and SiemAgent truststore and keystore to establish https.
- SiemCenterKjar - perform maven clean, install and package.
- SiemCenter - perfrom maven clean and install and run front end and back end server.
- SiemAgent - start the server.
 

## Guides:
The pages listed below contain information that helped us develop the application:
- https://sites.google.com/site/ddmwsst/digital-certificates
- https://www.sslshopper.com/what-is-a-csr-certificate-signing-request.
- https://www.npmjs.com/package/keycloak-angular





