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
package org.splandroid.tr.commons;

public class PlatformUtils {
  private static final boolean isWindows;

  // Work out which operating system we're running on
  // and ensure we pick the correct PATH environment
  // variable name
  static {
    final String os = System.getProperty("os.name").toLowerCase();
    if ((os.indexOf("windows") >= 0) || (os.indexOf("nt") >= 0)) {
      isWindows = true;
    } else {
      isWindows = false;
    }
  }

  public static boolean isWindows() {
    return isWindows;
  }
}
