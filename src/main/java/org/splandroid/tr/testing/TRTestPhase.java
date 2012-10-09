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
package org.splandroid.tr.testing;

public enum TRTestPhase {
  SETUP("Set up"), PRE_EXEC_SETUP("Pre-execute Setup"), EXECUTION("Execution"), TESTING(
      "Testing"), TEAR_DOWN("Tear down");

  private String name;

  TRTestPhase(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
