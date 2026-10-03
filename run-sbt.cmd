@echo off
rem Lance sbt avec les natives HADOOP_HOME requis sous Windows (voir README.md).
rem Exemples : run-sbt.cmd compile    |    run-sbt.cmd test    |    run-sbt.cmd assembly
if not defined HADOOP_HOME if exist "%USERPROFILE%\hadoop\bin\hadoop.dll" set "HADOOP_HOME=%USERPROFILE%\hadoop"
sbt %*
