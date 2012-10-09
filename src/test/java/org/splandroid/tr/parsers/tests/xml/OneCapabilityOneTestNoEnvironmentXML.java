/*
 * Copyright Ian Johnson 2012
 *
 * This file is part of TestRobot.
 *
 * TestRobot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TestRobot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TestRobot.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.splandroid.tr.parsers.tests.xml;

import java.util.HashMap;
import java.util.Map;

public class OneCapabilityOneTestNoEnvironmentXML {
  public static final String capabilityId = "myCapability";
  public static final String capabilityDesc = "My test capability";
  public static final String capabilityClass = "org.mycompany.MyTestClass";
  public static final String[] capArgId = new String[] { "capArgId1",
      "capArgId2" };
  public static final String[] capArgClasses = new String[] {
      "java.lang.String", "java.lang.Integer" };
  public static final String testId = "myTest";
  public static final String[] argIds = new String[] { "argId1", "argId2" };
  public static final String[] argClasses = new String[] { "java.lang.String",
      "java.lang.Integer" };
  public static final String[] varNames = new String[] { "envVar1", "envVar2" };
  public static final String[] varValues = new String[] { "envValue1",
      "envValue2" };

  public static final String padding = " blah blah blah %% $$ blah blah ";
  public static final String testCaseId = "myTestCase";
  public static final String testCaseDesc = "My test case description";
  public static final String testCapArgValue[] = new String[] {
      "capArgValue1 " + padding + "$" + varNames[1] + "$" + padding + "$"
          + varNames[0] + "$", "667" };
  public static final String testCapArgValueTarget[] = new String[] {
      "capArgValue1 " + padding + varValues[1] + padding + varValues[0],
      testCapArgValue[1] };

  public static final String testArgValue[] = new String[] { "testArgValue1",
      "27" };

  public static final String testsXML = "<?xml version=\"1.0\"?>\n"
      + "<tests>\n" + "	<testcase \n" + "		id=\""
      + testCaseId
      + "\"\n"
      + "		description=\""
      + testCaseDesc
      + "\">\n"
      + "		<"
      + capabilityId
      + " \n"
      + "			"
      + capArgId[0]
      + "=\""
      + testCapArgValue[0]
      + "\"\n"
      + "			"
      + capArgId[1]
      + "=\""
      + testCapArgValue[1]
      + "\">\n"
      + "			<"
      + testId
      + "\n"
      + "				"
      + argIds[0]
      + "=\""
      + testArgValue[0]
      + "\"\n"
      + "				"
      + argIds[1]
      + "=\""
      + testArgValue[1]
      + "\">\n"
      + "			</"
      + testId
      + ">\n"
      + "		</"
      + capabilityId + ">\n" + "	</testcase>\n" + "</tests>\n";

  public static Map<String, String> getTestsEnvironment() {
    final Map<String, String> map = new HashMap<String, String>();
    for (int idx = 0; idx < varNames.length; idx++) {
      final String name = varNames[idx];
      final String value = varValues[idx];
      map.put(name, value);
    }
    return map;
  }
}
