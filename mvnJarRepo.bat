
rem https://stackoverflow.com/questions/4955635/how-to-add-local-jar-files-to-a-maven-project
mvn install:install-file -Dfile=target\nals-1.0.0.jar -DgroupId=org.visualartsdna.code -DartifactId=nals -Dversion=1.0.0 -Dpackaging=jar -DgeneratePom=true
