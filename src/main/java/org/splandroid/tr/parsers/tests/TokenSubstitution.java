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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.splandroid.tr.TRException;

/**
 * A string substitution service class. To construct you'll need a pattern to
 * match a token prefixed and suffixed by an anchor string. <br>
 * <br>
 * The warning is suppressed since Map is a generic but we do not know ahead of
 * time what the Map will "look" like.
 * 
 * @param <T>
 */
class TokenSubstitution<T extends Map<?, ?>> {
  private final String substitutionAnchor;
  private final Pattern substitutionPattern;
  private final List<T> maps;
  private final String kind;

  /**
   * @param pattern
   *          - A Java regular expression token pattern
   * @param anchor
   *          - Token prefix and suffix string
   * @param kindStr
   *          - A message prefix that will appear in error messages
   */
  public TokenSubstitution(String pattern, String anchor, String kindStr) {
    substitutionAnchor = anchor;
    substitutionPattern = Pattern.compile(String.format("%s%s%s", anchor,
        pattern, anchor));
    kind = kindStr;
    maps = new ArrayList<T>();
  }

  public synchronized String substitute(String value) throws TRException {
    final Matcher m = substitutionPattern.matcher(value);
    final StringBuffer result = new StringBuffer();
    int startIdx = 0;
    int oldStartIdx = 0;
    int endIdx = 0;
    while (m.find()) {
      final String token = m.group().replaceAll(substitutionAnchor, "");
      String replacement = null;
      for (T map : maps) {
        if (map != null) {
          final Object thing = map.get(token);
          if (thing != null) {
            replacement = thing.toString();
            break;
          }
        }
      }
      if (replacement == null) {
        throw new TRException(String.format("%s [%s] not found", kind, token));
      }
      startIdx = m.start();
      endIdx = m.end();
      if (startIdx > 0) {
        result.append(value.substring(oldStartIdx, startIdx));
        oldStartIdx = m.end();
      } else {
        oldStartIdx = endIdx;
      }

      result.append(replacement);
    }
    if (endIdx < value.length()) {
      result.append(value.substring(endIdx));
    }

    return result.toString();
  }

  public synchronized TokenSubstitution<T> addMap(T map) {
    maps.add(map);
    return this;
  }

  public synchronized void clearMaps() {
    maps.clear();
  }
}
