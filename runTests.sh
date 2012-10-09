#!/bin/bash
#
# Copyright Ian Johnson 2012
#
# This file is part of TestRobot.
#
# TestRobot is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# TestRobot is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with TestRobot.  If not, see <http://www.gnu.org/licenses/>.
#

if [ "${JAVA_HOME}" = "" ]; then
	echo "JAVA_HOME not set"
	exit 1
fi

if [ "${M2_REPO}" = "" ]; then
	M2_REPO="${HOME}/.m2/repository"
	if [ ! -d ${M2_REPO} ]; then
		echo "Maven repository does not exist in [${M2_REPO}]"
		exit 1
	fi
fi

if [ "${CLASSPATH}" = "" ]; then
	TEST_JAR="`pwd`/target/tr-1.0-SNAPSHOT-jar-with-dependencies.jar"
	LOG4J_JAR="${M2_REPO}/log4j/log4j/1.2.14/log4j-1.2.14.jar"
	ANT_JAR="${M2_REPO}/org/apache/ant/ant/1.7.0/ant-1.7.0.jar"
	APACHE_COMMONS_CLI_JAR="${M2_REPO}/commons-cli/commons-cli/20040117.000000/commons-cli-20040117.000000.jar"
	CLASSPATH="${TEST_JAR}:${LOG4J_JAR}:${ANT_JAR}:${APACHE_COMMONS_CLI_JAR}"
	export CLASSPATH
fi

MAIN_CLASS="org.splandroid.tr.Main"

JAVA_EXE="${JAVA_HOME}/bin/java"
exec "${JAVA_EXE}" -ea ${MAIN_CLASS} $@
