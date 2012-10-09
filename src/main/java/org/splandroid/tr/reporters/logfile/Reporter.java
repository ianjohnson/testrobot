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
package org.splandroid.tr.reporters.logfile;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.log4j.Logger;
import org.splandroid.tr.events.IEventObserver;
import org.splandroid.tr.events.TRTestEvent;
import org.splandroid.tr.events.TRTestEventKind;

// TODO: Should this be in a org.splandroid.tr.testing.reporting package?
// And have the testing stuff automatically register a log file reporter.
public class Reporter implements IEventObserver {
  private static final String TEST = "Test";
  private static final String TEST_SUITE = TEST + " suite";
  private static final String TEST_CASE = TEST + " case";

  private static final String REPORT_FMT_START_FINI_HARNESS = "%s";
  private static final String REPORT_FMT_START_FINI_COMPONENT = "Component [%s]: %s";
  private static final String REPORT_FMT_TEST_SUITE_SETUP = "[%s]: "
      + TEST_SUITE + " [%s] configuration: %s";
  private static final String REPORT_FMT_TEST_SUITE = "[%s]: " + TEST_SUITE
      + " [%s]: %s";
  private static final String REPORT_FMT_TEST_CASE = "[%s]: " + TEST_CASE
      + " [%s]: %s";
  private static final String REPORT_FMT_TEST = "[%s]: " + TEST + " [%s]: %s";

  private static final String STARTING = "Starting...";
  private static final String FINISHED = "Finished";
  private static final String PASSED = "Passed";
  private static final String FAILED = "Failed";
  private static final String ERROR = "Error";

  private static final String lineSeparator = System
      .getProperty("line.separator");

  private static final Logger logger = Logger.getLogger(Reporter.class);

  private String component = null;

  public void setComponent(String component) {
    this.component = component;
  }

  public void update(TRTestEvent event) {
    final ImmutablePair<String, String> messages = getMessages(event);
    final String primaryMessage = messages.getLeft();
    final String debugMessage = messages.getRight();
    if (event.getThrowable() == null) {
      logger.info(primaryMessage);
    } else {
      logger.error(primaryMessage);
    }
    if (debugMessage.length() > 0) {
      logger.debug(debugMessage);
    }
  }

  private ImmutablePair<String, String> getMessages(TRTestEvent event) {
    assert event != null;

    final TRTestEventKind kind = event.getKind();
    final String id = event.getId();
    final String desc = event.getDescription();
    final String description = event.getDescription();
    final Throwable thr = event.getThrowable();
    final boolean isFailure = event.isFailure();
    final StringBuffer buf = new StringBuffer();
    final StringBuffer debugBuf = new StringBuffer();

    if ((kind != TRTestEventKind.STARTED) && (kind != TRTestEventKind.FINISHED)) {
      assert component != null : "Component must be set";
    }

    switch (kind) {
    case STARTED:
    case FINISHED:
      buf.append(String.format(REPORT_FMT_START_FINI_HARNESS, description));
      break;

    case COMPONENT_STARTED:
    case COMPONENT_FINISHED:
      final String componentStatus = (isFailure == true) ? FAILED
          : (kind == TRTestEventKind.COMPONENT_STARTED) ? STARTING : PASSED;
      buf.append(String.format(REPORT_FMT_START_FINI_COMPONENT, component,
          componentStatus));
      break;

    case PRE_TEST_SUITE_CONFIG:
    case POST_TEST_SUITE_CONFIG:
      String suiteConfigStatus = null;
      if (isFailure == true) {
        suiteConfigStatus = (thr == null) ? FAILED : ERROR;
      } else {
        suiteConfigStatus = (kind == TRTestEventKind.PRE_TEST_SUITE_CONFIG) ? STARTING
            : FINISHED;
      }
      buf.append(String.format(REPORT_FMT_TEST_SUITE_SETUP, component, id,
          suiteConfigStatus));
      break;

    case TEST_SUITE_STARTED:
    case TEST_SUITE_FINISHED:
      String testSuiteStatus = null;
      if (isFailure == true) {
        testSuiteStatus = (thr == null) ? FAILED : ERROR;
      } else {
        testSuiteStatus = (kind == TRTestEventKind.TEST_SUITE_STARTED) ? STARTING
            : FINISHED;
      }
      buf.append(String.format(REPORT_FMT_TEST_SUITE, component, id,
          testSuiteStatus));
      break;

    case TEST_CASE_STARTED:
      buf.append(String.format(REPORT_FMT_TEST_CASE, component, id, STARTING));
      debugBuf.append(String.format(REPORT_FMT_TEST_CASE, component, id, desc));
      break;

    case TEST_CASE_FINISHED:
      /*
       * case TEST_CASE_EXEC_SETUP: case TEST_CASE_EXEC:
       */
      String testCaseStatus = PASSED;
      if (isFailure == true) {
        testCaseStatus = (thr == null) ? FAILED : ERROR;
      }
      buf.append(String.format(REPORT_FMT_TEST_CASE, component, id,
          testCaseStatus));
      break;

    case TEST_STARTED:
      buf.append(String.format(REPORT_FMT_TEST, component, id, STARTING));
      debugBuf.append(String.format(REPORT_FMT_TEST, component, id,
          desc != null ? desc : "<<No description>>"));
      break;

    /*
     * case TEST_SETUP:
     */
    case TEST_FINISHED:
      String testStatus = PASSED;
      if (isFailure == true) {
        testStatus = (thr == null) ? FAILED : ERROR;
      }
      buf.append(String.format(REPORT_FMT_TEST, component, id, testStatus));
      if (description != null) {
        buf.append(": ").append(description);
      }
      break;

    default:
      buf.append(String.format("Unknown test event [%s]", kind.toString()));
    }

    if (thr != null) {
      final ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
      final PrintWriter pw = new PrintWriter(byteOutStream, true);
      thr.printStackTrace(pw);
      buf.append(lineSeparator).append(byteOutStream.toString());
    }

    return new ImmutablePair<String, String>(buf.toString(), debugBuf.toString());
  }
}
