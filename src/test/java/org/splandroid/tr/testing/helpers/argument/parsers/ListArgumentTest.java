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
package org.splandroid.tr.testing.helpers.argument.parsers;

import java.util.List;

import org.splandroid.tr.testing.helpers.argument.parsers.ListParser;

import junit.framework.TestCase;

public class ListArgumentTest extends TestCase {
  private static final String ARG_ONE = "test_thing_one.dat";
  private static final String ARG_TWO =
      "c:\\Program Files\\Company\\BAI\\SoftwareElement.exe";
  private static final String ARG_THREE =
      "/opt/company/product/bin/softwareElement";

  private static final String ARG_FOUR = "\\,my fourth arg:`¬!\"£$%^&*()_+{}\\,[]:;@\'~#?/>.<|";
  private static final String ARG_FIVE = "blah blah blah\\,";
  private static final String ARG_SIX = "jnhgjknphg84g4 #;kIJ()U£U£TF\\,()£ldkfjlj90#";

  public void testNullArguments() {
    doTest("", "", ARG_ONE, "");
  }

  public void testOneArgumentWithNoEscapedCommas() {
    doTest(ARG_TWO);
  }

  public void testOneArgumentWithEscapedCommas() {
    doTest(ARG_FOUR);
  }

  public void testNoEscapedCommas() {
    doTest(ARG_ONE, ARG_TWO, ARG_THREE);
  }

  public void testEscapedCommas() {
    doTest(ARG_FOUR, ARG_FIVE, ARG_SIX);
  }

  private static void doTest(String... arguments) {
    final String args = createCommaSeparatedArguments(arguments);
    final ListParser parser = new ListParser(args);
    validateArgumentList(parser.getArguments(), arguments);
  }

  private static String createCommaSeparatedArguments(String... args) {
    final StringBuffer list = new StringBuffer();
    final int noArgs = args.length;

    for (int idx = 0; idx < noArgs; idx++) {
      list.append(args[idx]);
      if (idx < (noArgs - 1)) {
        list.append(", ");
      }
    }

    return list.toString();
  }

  private static void validateArgumentList(List<String> argList,
      String... expectedArgs) {
    final int argListLen = argList.size();
    assertTrue("Argument list length mismatch",
        argListLen == expectedArgs.length);

    for (int idx = 0; idx < argListLen; idx++) {
      final String unEsc = expectedArgs[idx].replace("\\,", ",");
      assertTrue(unEsc.equals(argList.get(idx)));
    }
  }
}
