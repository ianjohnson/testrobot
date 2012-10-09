REM
REM Copyright Ian Johnson 2012
REM
REM This file is part of TestRobot.
REM
REM TestRobot is free software: you can redistribute it and/or modify
REM it under the terms of the GNU General Public License as published by
REM the Free Software Foundation, either version 3 of the License, or
REM (at your option) any later version.
REM
REM TestRobot is distributed in the hope that it will be useful,
REM but WITHOUT ANY WARRANTY; without even the implied warranty of
REM MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
REM GNU General Public License for more details.
REM
REM You should have received a copy of the GNU General Public License
REM along with TestRobot.  If not, see <http://www.gnu.org/licenses/>.
REM
@echo off
if "%JAVA_HOME%" == "" goto java_home_error

if [%M2_REPO%] == [] goto set_m2_repo
goto run_tests
:set_m2_repo
set M2_REPO=%USERPROFILE%\.m2\repository
if exists "%M2_REPO%" goto run_tests
goto m2_repo_error

:run_tests
echo %M2_REPO%
"%JAVA_HOME%\bin\java" -cp "%CLASSPATH%;.\target\tr-1.0-SNAPSHOT-jar-with-dependencies.jar";"%M2_REPO%\log4j\log4j\1.2.14\log4j-1.2.14.jar";"%M2_REPO%\org\apache\ant\ant\1.7.0\ant-1.7.0.jar";"%M2_REPO\commons-cli\commons-cli\20040117.000000\commons-cli-20040117.000000.jar" org.splandroid.tr.Main %1 %2 %3 %4 %5 %6 %7 %8 %9

goto end

:java_home_error
echo "JAVA_HOME not set"
goto end

:m2_repo_error
echo "Maven repository does not exist"
goto end

:end
