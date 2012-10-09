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
package org.splandroid.tr.events;

public enum TRTestEventKind {
  STARTED("Started"), FINISHED("Finished"),

  COMPONENT_STARTED("Component started"), COMPONENT_FINISHED(
      "Component finished"),

  PRE_TEST_SUITE_CONFIG("Pre-test suite configuration"), POST_TEST_SUITE_CONFIG(
      "Post-test suite configuration"),

  TEST_SUITE_STARTED("Test suite started"), TEST_SUITE_FINISHED(
      "Test suite finished"),

  TEST_CASE_STARTED("Test case started"), TEST_CASE_FINISHED(
      "Test case finished"),

  /*
   * TEST_CASE_EXEC_SETUP("Test case execute setup"),
   * TEST_CASE_EXEC("Test case execute"),
   * 
   * TEST_SETUP("Test setup"),
   */

  TEST_STARTED("Test started"), TEST_FINISHED("Test finished");

  private String name;

  TRTestEventKind(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
