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

@SuppressWarnings(value = { "serial" })
final class InternalTestException extends Exception {
  private TRTestPhase phase = null;

  public InternalTestException() {
  }

  public InternalTestException(String message) {
    super(message);
  }

  public InternalTestException(Throwable cause) {
    super(cause);
  }

  public InternalTestException(String message, Throwable cause) {
    super(message, cause);
  }

  public TRTestPhase getPhase() {
    return phase;
  }

  public void setPhase(TRTestPhase phase) {
    this.phase = phase;
  }

  @Override
  public String getMessage() {
    final String msg = String.format("%s phase expection: %s", phase,
        super.getMessage());
    return msg;
  }
}
