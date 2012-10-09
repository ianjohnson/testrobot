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
package org.splandroid.tr.parsers.tests;

import java.lang.Boolean;
import java.lang.Double;
import java.lang.Float;
import java.lang.Long;
import java.lang.Integer;
import java.util.HashMap;
import java.util.Map;

import org.splandroid.tr.TRException;
import org.splandroid.tr.parsers.tests.TokenSubstitution;

import junit.framework.TestCase;

public class ArgumentSubstitutionTest extends TestCase {
  private static final String pattern = "\\w+";
  private static final String anchor = "%";
  private static final String kind = "Test argument";

  private static final String booleanArgName = "booleanArgName";
  private static final String doubleArgName = "doubleArgName";
  private static final String floatArgName = "floatArgName";
  private static final String longArgName = "longArgName";
  private static final String intArgName = "intArg";
  private static final String stringArgName = "stringArg";

  private static final String booleanArgValueStr = "true";
  private static final String doubleArgValueStr = "3.141";
  private static final String floatArgValueStr = "2.718";
  private static final String longArgValueStr = "667";
  private static final String intArgValueStr = "42";
  private static final String stringArgValueStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 "
      + "¬`!\"£$%^&*()_+-={[}]~#@'?/>.<,|\\'";

  private static final Boolean booleanArgValue = new Boolean(booleanArgValueStr);
  private static final Double doubleArgValue = new Double(doubleArgValueStr);
  private static final Float floatArgValue = new Float(floatArgValueStr);
  private static final Long longArgValue = new Long(longArgValueStr);
  private static final Integer intArgValue = new Integer(intArgValueStr);
  private static final String stringArgValue = new String(stringArgValueStr);

  private static final String[] argNames = new String[] { booleanArgName,
      doubleArgName, floatArgName, longArgName, intArgName, stringArgName };

  private static final Object[] argValues = new Object[] { booleanArgValue,
      doubleArgValue, floatArgValue, longArgValue, intArgValue, stringArgValue };

  private static final String noSubstitutionText = String.format(
      "no substitute %s%s at all ", anchor, anchor);

  private TokenSubstitution<Map<String, Object>> subs;
  private Map<String, Object> map;

  public void setUp() {
    subs = new TokenSubstitution<Map<String, Object>>(pattern, anchor, kind);
    map = new HashMap<String, Object>();
  }

  public void tearDown() {
    map = null;
    subs = null;
  }

  private void populateMap() {
    assertTrue(argNames.length == argValues.length);
    for (int idx = 0; idx < argNames.length; idx++) {
      map.put(argNames[idx], argValues[idx]);
    }
  }

  public void testNoArgumentSubstitutionDueToEmptyMap() {
    final String value = "Some text that will not be substituted";
    String replacement = null;
    try {
      replacement = subs.addMap(map).substitute(value);
    } catch (TRException ex) {
      fail(ex.getMessage());
    }

    assertTrue(value.equals(replacement));
  }

  public void testNoArgumentSubstitutionDueToNoSubstitution() {
    populateMap();
    final String target = "Some text that will not be substituted";
    String replacement = null;
    try {
      replacement = subs.addMap(map).substitute(target);
    } catch (TRException ex) {
      fail(ex.getMessage());
    }

    assertTrue(target.equals(replacement));
  }

  public void testArgumentSubstitutionWithPrefixAndSuffix() {
    populateMap();

    final StringBuffer argValue = new StringBuffer(noSubstitutionText);
    final StringBuffer targetValue = new StringBuffer(noSubstitutionText);
    for (int idx = 0; idx < argNames.length; idx++) {
      argValue.append(anchor).append(argNames[idx]).append(anchor);
      argValue.append(" ").append(noSubstitutionText);
      targetValue.append(argValues[idx].toString());
      targetValue.append(" ").append(noSubstitutionText);
    }
    final String target = targetValue.toString();

    String replacement = null;
    try {
      replacement = subs.addMap(map).substitute(argValue.toString());
    } catch (TRException ex) {
      fail(ex.getMessage());
    }

    assertTrue(replacement != null);
    assertTrue(target.equals(replacement));
  }

  public void testArgumentSubstitutionWithPrefixAndNoSuffix() {
    populateMap();

    final StringBuffer argValue = new StringBuffer(noSubstitutionText);
    final StringBuffer targetValue = new StringBuffer(noSubstitutionText);
    for (int idx = 0; idx < argNames.length - 1; idx++) {
      argValue.append(anchor).append(argNames[idx]).append(anchor);
      argValue.append(" ").append(noSubstitutionText);
      targetValue.append(argValues[idx].toString());
      targetValue.append(" ").append(noSubstitutionText);
    }
    argValue.append(" ").append(anchor).append(argNames[argNames.length - 1])
        .append(anchor);
    targetValue.append(" ").append(argValues[argNames.length - 1].toString());
    final String target = targetValue.toString();

    String replacement = null;
    try {
      replacement = subs.addMap(map).substitute(argValue.toString());
    } catch (TRException ex) {
      fail(ex.getMessage());
    }

    assertTrue(replacement != null);
    assertTrue(target.equals(replacement));
  }
}
