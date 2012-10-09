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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.splandroid.tr.parsers.ISymbol;
import org.splandroid.tr.parsers.ParserException;

class Symbol implements ISymbol {
  private final String id;

  private final Map<String, ISymbol> symbols = new HashMap<String, ISymbol>();
  private final Map<String, List<ISymbol>> contextMap = new HashMap<String, List<ISymbol>>();

  public Symbol(String symbolId) {
    id = symbolId;
  }

  public String getId() {
    return id;
  }

  private String getKey(String id, String context) {
    return context + "." + id;
  }

  private void addSymbol(String key, ISymbol symbol) throws ParserException {
    if (symbols.containsKey(key)) {
      throw new ParserException(String.format(
          "Symbol with key [%s] already exists", key));
    }
    symbols.put(key, symbol);
  }

  private void addSymbolToContext(String context, ISymbol symbol) {
    if (contextMap.containsKey(context)) {
      final List<ISymbol> contextList = contextMap.get(context);
      contextList.add(symbol);
    } else {
      final List<ISymbol> contextList = new ArrayList<ISymbol>();
      contextList.add(symbol);
      contextMap.put(context, contextList);
    }
  }

  public void addSymbol(ISymbol symbol, String context) throws ParserException {
    final String key = getKey(symbol.getId(), context);
    addSymbol(key, symbol);
    addSymbolToContext(context, symbol);
  }

  public ISymbol getSymbol(String id, String context) {
    final String key = getKey(id, context);
    return symbols.get(key);
  }

  public List<ISymbol> getSymbolsInContext(String context) {
    final List<ISymbol> contextSymbols = contextMap.get(context);
    if (contextSymbols == null) {
      return new ArrayList<ISymbol>();
    }
    return contextSymbols;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final Symbol other = (Symbol )obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }
}
