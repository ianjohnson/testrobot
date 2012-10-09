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
package org.splandroid.tr.parsers.profile;

import java.util.Collection;

import org.splandroid.tr.parsers.ParserException;

public class ProfileParserException extends ParserException {
  private static final long serialVersionUID = Long.MAX_VALUE;
  private Collection<String> missingEnvironment = null;

  public ProfileParserException() {
    super();
  }

  public ProfileParserException(String message, Throwable cause) {
    super(message, cause);
  }

  public ProfileParserException(String message) {
    super(message);
  }

  public ProfileParserException(Throwable cause) {
    super(cause);
  }

  public ProfileParserException(Collection<String> missingEnv) {
    setMissingEnvironment(missingEnv);
  }

  public Collection<String> getMissingEnvironment() {
    return missingEnvironment;
  }

  @Override
  public String getMessage() {
    if (missingEnvironment == null) {
      return super.getMessage();
    }

    return getMissingEnvironmentMessage();
  }

  /**
   * Handles what should happen if the profile parser throws an exception.
   * 
   * @param ex
   *          - The exception thrown by the profile parser.
   * @return A string containing an error message derived from the exception.
   */
  private String getMissingEnvironmentMessage() {
    final StringBuffer msg = new StringBuffer();
    final int noMissingEnvs = missingEnvironment.size();

    if (noMissingEnvs > 0) {
      msg.append("Expected environment variables not set: ");
      int count = 0;
      for (String missingEnvVar : missingEnvironment) {
        msg.append(missingEnvVar);
        if (count < (noMissingEnvs - 1)) {
          msg.append(", ");
        }
        count++;
      }
    }

    return msg.toString();
  }

  private void setMissingEnvironment(Collection<String> missingEnv) {
    missingEnvironment = missingEnv;
  }
}
