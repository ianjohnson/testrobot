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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.splandroid.tr.TRException;

public class TRTestInputProvider implements ITRTestInputProvider {
  private final String rootDir;

  private String componentName;

  public TRTestInputProvider(String testInfRootDirectory) {
    rootDir = testInfRootDirectory;
    componentName = null;
  }

  /**
   * Build a java.io.FileInputStream object for a file specified by it's file
   * name.
   * 
   * @param fileName
   * @return The java.io.FileInputStream object
   * @throws TRException
   */
  private static InputStream getFileStream(final File file)
  throws TRException {
    if (file.exists() == false) {
      throw new TRException(new IOException(String.format(
          "File [%s] does not exist", file.getAbsolutePath())));
    }
    FileInputStream stream = null;
    try {
      stream = new FileInputStream(file);
    } catch (FileNotFoundException notFoundEx) {
      throw new TRException(notFoundEx);
    } catch (SecurityException secEx) {
      throw new TRException(secEx);
    }

    return stream;
  }

  public File getComponentInputDirectory() {
    return new File(rootDir, componentName);
  }

  public void setComponent(String component) {
    componentName = component;
  }

  public InputStream getProfileFileStream() throws TRException {
    final File file = new File(rootDir, FilenameUtils.concat(componentName, profileXMLFile));

    return getFileStream(file);
  }

  public InputStream getTestsFileStream() throws TRException {
    final File file = new File(rootDir, FilenameUtils.concat(componentName, testsXMLFile));

    return getFileStream(file);
  }
}
