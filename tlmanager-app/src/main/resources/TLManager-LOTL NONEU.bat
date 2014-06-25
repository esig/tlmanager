@echo off

set JAVA_HOME=..\java

%JAVA_HOME%\bin\java -Dtlmanager.common.mode="LOTL" -Dtlmanager.common.territory="NONEU" -jar ..\tl-manager.jar