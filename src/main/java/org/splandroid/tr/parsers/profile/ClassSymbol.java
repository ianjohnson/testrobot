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

import org.splandroid.tr.parsers.IClassSymbol;

class ClassSymbol extends Symbol implements IClassSymbol {

  private final String kindClass;

  public ClassSymbol(String symbolId, String kindClassName) {
    super(symbolId);
    kindClass = kindClassName;
  }

  public String getKindClass() {
    return kindClass;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((kindClass == null) ? 0 : kindClass.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    final ClassSymbol other = (ClassSymbol )obj;
    if (kindClass == null) {
      if (other.kindClass != null)
        return false;
    } else if (!kindClass.equals(other.kindClass))
      return false;
    return true;
  }
}
