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
import java.io.IOException;
import java.io.InputStream;

import org.splandroid.tr.TRException;

public interface ITRTestInputProvider {
  public static final String testInfDirName = "TEST-INF";
  public static final String profileXMLFile = "profile.xml";
  public static final String testsXMLFile = "tests.xml";

  /**
   * Retrieves a input stream object for the profile XML file for a CHAINworks
   * component.
   * 
   * @param componentName
   * @return A File object representing the profile XML file for the component
   * @throws IOException
   */
  public InputStream getProfileFileStream() throws TRException;

  /**
   * Retrieves a input stream object for the tests XML file for a CHAINworks
   * component.
   * 
   * @param componentName
   * @return A File object representing the tests XML file for the component
   * @throws IOException
   */
  public InputStream getTestsFileStream() throws TRException;

  /**
   * Gets the root directory that contains the test input directories for the
   * component
   * 
   * @return Pathname of the component's root output directory
   */
  public File getComponentInputDirectory();

  /**
   * Set the current component name
   * 
   * @param component
   */
  public void setComponent(String component);
}
