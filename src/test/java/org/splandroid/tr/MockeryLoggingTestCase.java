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
package org.splandroid.tr;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class MockeryLoggingTestCase extends MockeryTestCase {
  protected Logger logger = null;

  static {
    BasicConfigurator.configure();
  }

  public MockeryLoggingTestCase() {
    super();
  }

  public MockeryLoggingTestCase(String name) {
    super(name);
  }

  public void setUp() {
    super.setUp();
    if (logger == null) {
      logger = Logger.getLogger(this.getClass());
    }
  }
}