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
package org.splandroid.tr.parsers.profile.xml;

public class OneCapabilityOneTestXML {
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

  public static final String profileXML = "<?xml version=\"1.0\"?>"
      + "<profile>" + "	<capability" + "		id=\""
      + capabilityId
      + "\""
      + "		description=\""
      + capabilityDesc
      + "\""
      + "		class=\""
      + capabilityClass
      + "\">"
      + "		<arguments>"
      + "			<argument"
      + "				id=\""
      + capArgId[0]
      + "\""
      + "				kind=\""
      + capArgClasses[0]
      + "\"/>"
      + "			<argument"
      + "				id=\""
      + capArgId[1]
      + "\""
      + "				kind=\""
      + capArgClasses[1]
      + "\"/>"
      + "		</arguments>"
      + "		<test"
      + "			id=\""
      + testId
      + "\">"
      + "			<arguments>"
      + "				<argument"
      + "					id=\""
      + argIds[0]
      + "\""
      + "					kind=\""
      + argClasses[0]
      + "\"/>"
      + "				<argument"
      + "					id=\""
      + argIds[1]
      + "\""
      + "					kind=\""
      + argClasses[1]
      + "\"/>"
      + "			</arguments>"
      + "		</test>"
      + "		<environment>"
      + "			<variable"
      + "				name=\""
      + varNames[0]
      + "\""
      + "				value=\""
      + varValues[0]
      + "\"/>"
      + "			<variable"
      + "				name=\""
      + varNames[1]
      + "\""
      + "				value=\""
      + varValues[1]
      + "\"/>"
      + "		</environment>"
      + "	</capability>"
      + "</profile>";
}
