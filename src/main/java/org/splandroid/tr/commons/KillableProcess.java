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
/*
 * 
 */
package org.splandroid.tr.commons;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;

/**
 * A wrapper around java.lang.Process that spawns processes in a separate
 * thread. The spawned processes can be killed.
 */
public class KillableProcess {

  private Thread thread = null;
  private Process process = null;
  private ProcessStatus procStatus = null;
  private Semaphore finishSema = null;
  private boolean killRequested = false;
  private ExecuteStreamHandler streamHandler = null;
  private List<String> cmdLine = null;
  private File workingDir = null;
  private List<ImmutablePair<String, String>> envVars = null;

  public KillableProcess(String execCmd, File workingDir,
      ExecuteStreamHandler streamHandler) {
    // Build a process status object for this process
    procStatus = new ProcessStatus();

    // A semaphore to notify when the thread has finished
    finishSema = new Semaphore(1);

    // An empty command line
    cmdLine = new ArrayList<String>();
    this.addCommandLine(execCmd);

    // Environment variables to add to the default environment
    envVars = new ArrayList<ImmutablePair<String, String>>();

    // Working directory
    this.workingDir = workingDir;

    // Stream handler
    this.streamHandler = streamHandler;
  }

  /**
   * Add a single component to the command line. Each component will be space
   * delimited on the command line.
   * 
   * @param cmdLineComp
   */
  final public void addCommandLine(String cmdLineComp) {
    if (cmdLineComp != null && cmdLineComp.length() > 0) {
      cmdLine.add(cmdLineComp);
    }
  }

  /**
   * Add a list of command line components to the command line. Each component
   * in the list shall be space delimited on the executed command line.
   * 
   * @param cmdLineComps
   */
  final public void addCommandLine(List<String> cmdLineComps) {
    for (String cmdLineComp : cmdLineComps) {
      cmdLine.add(cmdLineComp);
    }
  }

  /**
   * Set an environment variable in the executable's environment.
   * 
   * @param envVarName
   * @param value
   */
  final public void addEnvironmentVariable(String envVarName, String value) {
    final ImmutablePair<String, String> nameValuePair =
        new ImmutablePair<String, String>(envVarName, value);
    envVars.add(nameValuePair);
  }

  /**
   * Start the thread that will spawn the defined process as long as the process
   * is not running.
   */
  final public synchronized void start() {
    if (this.isRunning() == false) {
      // Generate a unique-ish name for the thread
      final int hash = cmdLine.hashCode();
      final String thrName = String.format("KillableProcess-%d", hash);

      // Build a thread that'll run the process
      thread = new Thread(thrName) {
        public void run() {
          // Launch the process and wait for completion
          try {
            final ProcessBuilder pb = new ProcessBuilder(cmdLine);
            // Add the passed in environment
            Map<String, String> procEnv = pb.environment();
            for (ImmutablePair<String, String> p : envVars) {
              procEnv.put(p.getLeft(), p.getRight());
            }

            // Add working directory
            pb.directory(workingDir);
            // Start the process
            process = pb.start();
            if (process != null) {
              // Re-direct the streams
              try {
                streamHandler.setProcessInputStream(process.getOutputStream());
                streamHandler.setProcessOutputStream(process.getInputStream());
                streamHandler.setProcessErrorStream(process.getErrorStream());
              } catch (Exception e) {
                process.destroy();
                throw e;
              }
              streamHandler.start();
              final int retValue = process.waitFor();
              streamHandler.stop();
              procStatus.setReturnValue(retValue);
            }
          } catch (Exception ex) {
            procStatus.setException(ex);
          } finally {
            finishSema.release();
          }
        }
      };
      thread.setDaemon(true);
      finishSema.acquireUninterruptibly();
      thread.start();
      killRequested = false;
    }
  }

  /**
   * Kill the process if the process has not received a kill request and it is
   * running.
   */
  final public synchronized void kill() {
    if (killRequested == false && this.isRunning() == true) {
      process.destroy();
      killRequested = true;
    }
  }

  /**
   * Is the process still running?
   * 
   * @return boolean
   */
  final public boolean isRunning() {
    return (finishSema.availablePermits() == 0);
  }

  /**
   * Get the process return status
   * 
   * @return ProcessStatus
   */
  final public ProcessStatus getProcessStatus() {
    return procStatus;
  }

  /**
   * Returns a copy of the string representation of the command line
   * 
   * @return String The command line
   */
  final public String getCommandLine() {
    final StringBuffer strBuf = new StringBuffer();
    final int cmdLineLen = cmdLine.size();

    for (int idx = 0; idx < cmdLineLen; idx++) {
      strBuf.append(cmdLine.get(idx));
      if (idx < (cmdLineLen - 1)) {
        strBuf.append(" ");
      }
    }

    return strBuf.toString();
  }
}
