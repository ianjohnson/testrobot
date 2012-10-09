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

/**
 * Describes the returning status of the spawned process. The stored return
 * value is initialised to ProcessStatus#invalidReturnValue. This will not be
 * returned from a java.lang.Process#waitFor() method. If the exception is null
 * and the return value is ProcessStatus#invalidReturnValue the process never
 * ran.
 */
public class ProcessStatus {
  public static final int invalidReturnValue = Integer.MIN_VALUE;

  private int returnValue = invalidReturnValue;
  private Exception exception = null;

  public ProcessStatus() {
  }

  public int getReturnValue() {
    return returnValue;
  }

  public Exception getException() {
    return exception;
  }

  public void setReturnValue(int returnValue) {
    if (this.returnValue == invalidReturnValue) {
      this.returnValue = new Integer(returnValue);
    }
  }

  public void setException(Exception exception) {
    if (this.exception == null) {
      this.exception = exception;
    }
  }
}
