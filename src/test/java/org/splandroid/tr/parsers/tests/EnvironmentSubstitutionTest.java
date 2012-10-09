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

import java.util.HashMap;
import java.util.Map;

import org.splandroid.tr.TRException;
import org.splandroid.tr.parsers.tests.TokenSubstitution;

import junit.framework.TestCase;

public class EnvironmentSubstitutionTest extends TestCase {
  private static final String pattern = "\\w+";
  private static final String anchor = "$";
  private static final String anchorForPattern = "\\" + anchor;
  private static final String kind = "Test environment variable";

  private static final String VAR_ONE = "ENV_VAR_ONE";
  private static final String VAR_TWO = "ENV_VAR_TWO";
  private static final String VALUE_ONE = "/some/directory/file/name";
  private static final String VALUE_TWO = "nothing_in_particular";

  private TokenSubstitution<Map<String, String>> subs;
  private Map<String, String> env;

  public void setUp() {
    subs = new TokenSubstitution<Map<String, String>>(pattern,
        anchorForPattern, kind);
    env = new HashMap<String, String>();
  }

  public void tearDown() {
    env = null;
    subs = null;
  }

  private void populateMap() {
    env.put(VAR_ONE, VALUE_ONE);
    env.put(VAR_TWO, VALUE_TWO);
  }

  public void testSimpleSubstitution() {
    populateMap();
    final String HOME = "HOME";
    final String stuff = String.format(" some text %s%s ", anchor, anchor);
    final String value = String.format(
        "%1$s%2$s%1$s%4$s%1$s%3$s%1$s%4$s%1$s%5$s%1$s", anchor, VAR_ONE,
        VAR_TWO, stuff, HOME);
    final String target = String.format("%s%s%s%s%s", VALUE_ONE, stuff,
        VALUE_TWO, stuff, System.getenv(HOME));
    String replacement = null;
    try {
      replacement = subs.addMap(env).addMap(System.getenv()).substitute(value);
    } catch (TRException ex) {
      fail(ex.getMessage());
    }
    assertTrue(replacement != null);
    assertTrue(target.equals(replacement));
  }
}
