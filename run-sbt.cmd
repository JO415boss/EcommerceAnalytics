@echo off
rem Lance sbt avec les fichiers Windows de Hadoop requis (voir README.md).
rem Exemples : run-sbt.cmd compile    |    run-sbt.cmd test    |    run-sbt.cmd assembly
if not defined HADOOP_HOME if exist "%USERPROFILE%\hadoop\bin\hadoop.dll" set "HADOOP_HOME=%USERPROFILE%\hadoop"
sbt %*
