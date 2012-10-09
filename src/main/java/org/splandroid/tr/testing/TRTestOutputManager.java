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
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;

public class TRTestOutputManager implements ITRTestOutputManager {
  private final File suiteResultDir;
  private String component;

  public TRTestOutputManager(String testResRootDirectory, Date timeNow) {
    final SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
    final String dateStamp = dateFormatter.format(timeNow);
    suiteResultDir = new File(testResRootDirectory, dateStamp);
  }

  public File createSuiteResultsDirectory() {
    if (suiteResultDir.exists()) {
      suiteResultDir.delete();
    }
    suiteResultDir.mkdirs();

    return suiteResultDir;
  }

  public File createComponentTestOutputDirectory(final String testId) {
    assert component != null : "Must set component";
    final File testOutputDir = new File(
        suiteResultDir,
        FilenameUtils.concat(component, testId));

    if (testOutputDir.exists()) {
      testOutputDir.delete();
    }
    testOutputDir.mkdirs();

    return testOutputDir;
  }

  public void setComponent(final String comp) {
    component = comp;
  }
}
