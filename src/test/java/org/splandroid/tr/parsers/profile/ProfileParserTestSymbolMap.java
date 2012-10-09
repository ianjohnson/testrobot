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

import java.util.HashMap;
import java.util.Map;

import org.splandroid.tr.parsers.ISymbol;
import org.splandroid.tr.parsers.ISymbolMap;

class ProfileParserTestSymbolMap extends HashMap<String, ISymbol> implements
    ISymbolMap {

  private static final long serialVersionUID = Long.MAX_VALUE;

  public ProfileParserTestSymbolMap() {
  }

  public ProfileParserTestSymbolMap(int initialCapacity) {
    super(initialCapacity);
  }

  public ProfileParserTestSymbolMap(Map<String, ISymbol> m) {
    super(m);
  }

  public ProfileParserTestSymbolMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }
}
