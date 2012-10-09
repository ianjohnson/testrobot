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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.splandroid.tr.commons.KillableProcess;
import org.splandroid.tr.commons.ProcessStatus;
import org.splandroid.tr.parsers.ITestArguments;
import org.splandroid.tr.parsers.ITestDescriptor;

public abstract class TRExecutableTestCase extends TRTestCase {
  private static Logger logger = Logger.getLogger(TRExecutableTestCase.class);
  private static int defaultPollDelayms = 500;

  public TRExecutableTestCase(String testId, String description,
      List<ITestDescriptor> tests, ITestArguments setUpInfo,
      Map<String, String> environment) {
    super(testId, description, tests, setUpInfo, environment);
  }

  protected static String[] splitLines(String lines) {
    final String[] splitLines = lines.split("(\n)|(\r\n)");
    return splitLines;
  }

  protected int runProcess(
      final String cmd,
      final File workingDir,
      final InputStream inStream,
      final OutputStream outStream,
      final OutputStream errStream,
      final String... options)
  throws Exception {
    logger.debug(String.format("Running command = [%s]...", cmd));

    final PumpStreamHandler streamHandler = new PumpStreamHandler(outStream,
        errStream, inStream);

    if (!workingDir.exists()) {
      fail(String.format("Working directory [%s] does not exist", workingDir));
    }
    logger.debug(
        String.format("Working directory is [%s]", workingDir.getPath()));

    final KillableProcess proc =
        new KillableProcess(cmd, workingDir, streamHandler);

    for (String option : options) {
      logger.debug(String.format("Adding command line option [%s]", option));
      proc.addCommandLine(option);
    }

    final Map<String, String> environment = getEnvironment();
    if (environment != null) {
      for (String envVarName : environment.keySet()) {
        final String value = environment.get(envVarName);
        logger.debug(String.format("Setting environment [%s] = [%s]",
            envVarName, value));
        proc.addEnvironmentVariable(envVarName, value);
      }
    } else {
      logger.debug("No environment to set");
    }

    logger.debug(String.format("Executing command = [%s]",
        proc.getCommandLine()));
    proc.start();
    while (proc.isRunning()) {
      synchronized (this) {
        try {
          wait(defaultPollDelayms);
        } catch (InterruptedException ex) {
          throw new InternalTestException(ex);
        }
      }
    }

    logger.debug(String.format("Execution standard output: [%s]",
        outStream.toString()));
    logger.debug(String.format("Execution standard error: [%s]",
        errStream.toString()));

    final ProcessStatus status = proc.getProcessStatus();
    final int exitStatus = status.getReturnValue();
    final Exception ex = status.getException();
    if (ex != null) {
      final String msg = String.format("Failed to run [%s]: %s", cmd,
          ex.getMessage());
      fail(msg);
    }

    return exitStatus;
  }
}
