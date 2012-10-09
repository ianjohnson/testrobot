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

public class EnvironmentTestXML {
  public static final String capabilityId = "capabilityId";
  public static final String capabilityDesc = "My test capability";
  public static final String capabilityClass = "org.mycompany.MyTestClass";
  public static final String capArgId = "capArgId1";
  public static final String capArgClasses = "java.lang.String";
  public static final String testId = "myTest";
  public static final String testArgId = "testArgId1";
  public static final String testArgClass = "java.lang.Integer";
  public static final String[] varNames = new String[] { "ENV_VAR_ONE",
      "ENV_VAR_TWO", "ENV_VAR_THREE" };
  public static final String[] varValues = new String[] { "ENV_VAR_VALUE_ONE",
      "ENV_VAR_VALUE_TWO", "ENV_VAR_VALUE_THREE" };
  public static final String[] existVarNames = new String[] { "JAVA_HOME",
      "PATH" };
  public static String noExistsProfileXML;
  public static String existsProfileXML;

  private static final String profileXMLTemplateHead = String.format(
      "<?xml version=\"1.0\"?>" + "<profile>" + "	<capability" + "		id=\"%s\""
          + "		description=\"%s\"" + "		class=\"%s\">" + "		<arguments>"
          + "			<argument" + "				id=\"%s\"" + "				kind=\"%s\"/>"
          + "		</arguments>" + "		<test" + "			id=\"%s\">" + "			<arguments>"
          + "				<argument" + "					id=\"%s\"" + "					kind=\"%s\"/>"
          + "			</arguments>" + "		</test>" + "		<environment>", capabilityId,
      capabilityDesc, capabilityClass, capArgId, capArgClasses, testId,
      testArgId, testArgClass);

  private static final String envVarProfileXMLTemplate = "			<variable"
      + "				name=\"%s\"" + "				value=\"%s\"/>";

  private static final String envVarExistProfileXMLTemplate = "			<exists"
      + "				name=\"%s\"/>";

  private static final String profileXMLTemplateTail = "		</environment>"
      + "	</capability>" + "</profile>";

  static {
    // Build some profile XML that contains environment variable
    // requirements for variable that do exist in the environment
    final StringBuffer existsXML = new StringBuffer();
    existsXML.append(profileXMLTemplateHead);
    for (String varName : existVarNames) {
      existsXML.append(String.format(envVarExistProfileXMLTemplate, varName));
    }
    existsXML.append(profileXMLTemplateTail);
    existsProfileXML = existsXML.toString();

    // Build some profile XML that contains environment variable
    // that are required but are not set in the environment.
    final StringBuffer noExistsXML = new StringBuffer();
    noExistsXML.append(profileXMLTemplateHead);
    for (int idx = 0; idx < varNames.length; idx++) {
      noExistsXML.append(String.format(envVarProfileXMLTemplate, varNames[idx],
          varValues[idx]));
    }
    for (String varName : varNames) {
      noExistsXML.append(String.format(envVarExistProfileXMLTemplate, varName));
    }
    noExistsXML.append(profileXMLTemplateTail);
    noExistsProfileXML = noExistsXML.toString();
  }
}
