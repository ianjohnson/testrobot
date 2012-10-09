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

import java.io.File;

public interface ITRTestOutputManager {
  public static final String testResDirName = "TEST-RES";
  public static final String dateFormat = "yyyy-MM-dd HH.mm.ss";

  /**
   * Create, if it does not exist, a directory that shall be used for recording
   * the results of the test suite.
   * 
   * @return the pathname of the suite's results directory.
   */
  public File createSuiteResultsDirectory();
  
  /**
   * Create, if it does not exist, a directory that can be used for the output
   * of tests.
   * 
   * @param testId
   *          - A test identifier
   * @return the pathname of the output directory
   */
  public File createComponentTestOutputDirectory(String testId);

  /**
   * Set the current component name
   * 
   * @param Component
   *          name
   */
  public void setComponent(String comp);
}
