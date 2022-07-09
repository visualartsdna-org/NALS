#!/bin/sh
#
mvn install:install-file -Dfile=target/nals-1.0.0.jar -DgroupId=org.visualartsdna -DartifactId=nals -Dversion=1.0.0 -Dpackaging=jar -DgeneratePom=true
