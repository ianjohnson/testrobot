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
package org.splandroid.tr.parsers.tests;

import java.util.HashMap;
import java.util.Map;

import org.splandroid.tr.parsers.ITestArguments;

class TestArguments extends HashMap<String, Object> implements ITestArguments {
  private static final long serialVersionUID = Long.MAX_VALUE;

  private String id;

  public TestArguments(String id) {
    super();
    initialise(id);
  }

  public TestArguments(String id, int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
    initialise(id);
  }

  public TestArguments(String id, int initialCapacity) {
    super(initialCapacity);
    initialise(id);
  }

  public TestArguments(String id, Map<? extends String, ? extends Object> m) {
    super(m);
    initialise(id);
  }

  private void initialise(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    final StringBuffer repr = new StringBuffer();

    repr.append("[").append(id).append("]");
    repr.append(" {");
    final int noEntries = this.size() - 1;
    int count = 0;
    for (String key : this.keySet()) {
      repr.append("'").append(key).append("'").append(": ");
      repr.append(this.get(key).toString());
      if (count < noEntries) {
        repr.append(", ");
      }
      count++;
    }
    repr.append("}");

    return repr.toString();
  }
}
