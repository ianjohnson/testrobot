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
package org.splandroid.tr.extensions.execdemo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.splandroid.tr.parsers.ITestArguments;
import org.splandroid.tr.parsers.ITestDescriptor;
import org.splandroid.tr.testing.TRExecutableTestCase;
import org.splandroid.tr.testing.annotations.Test;
import org.splandroid.tr.testing.annotations.TestExecuteSetter;
import org.splandroid.tr.testing.annotations.TestSetter;

public class Demo extends TRExecutableTestCase {
  private static final Logger logger = Logger.getLogger(Demo.class);

  private String executable;
  private String script;
  private String scriptArgument;
  private String output;
  private String expectedOutput;

  public Demo(String testId, String description, List<ITestDescriptor> tests,
      ITestArguments setUpInfo, Map<String, String> environment) {
    super(testId, description, tests, setUpInfo, environment);
  }

  @TestExecuteSetter
  public void setExecutable(String executable) {
    this.executable = executable;
  }

  @TestExecuteSetter
  public void setScript(String script) {
    logger.debug(String.format("Input directory: [%s]", getInputDirectory()));
    this.script = new File(getInputDirectory(), script).getAbsolutePath();
  }

  @TestExecuteSetter
  public void setScriptArgument(String arg) {
    this.scriptArgument = arg;
  }

  public void execute() throws Exception {
    logger.debug(String
        .format("Running execute for test case [%s]...", getId()));
    logger.debug(String.format("Working directory: [%s]",
        System.getProperty("user.dir")));
    output = null;
    final OutputStream outStream = new ByteArrayOutputStream();
    final OutputStream errStream = new ByteArrayOutputStream();
    final int exitStatus = runProcess(executable, getOutputDirectory(),
        System.in, outStream, errStream, script, scriptArgument);
    assertTrue(
        String.format("%s failed with exit code %d", executable, exitStatus),
        exitStatus == 0);
    output = splitLines(outStream.toString())[0];
  }

  @TestSetter
  public void setExpectedOutput(String value) {
    expectedOutput = value;
  }

  @Test
  public void testOutput() {
    assertTrue(expectedOutput.equals(output));
  }
}
