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
package org.splandroid.tr.testing.helpers.argument.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class will parse a string that contains one or more comma separated
 * strings. Escaped commas will be converted to commas.
 * 
 * <arg>[, <arg>]*
 */
public class ListParser implements IArgumentParser {
  private static final int GROUP_IDX_ARG = 1;
  private static final int GROUP_IDX_COMMA = 5;
  private static final String PARSER_REGEX = "((([^,\\\\])*(\\\\,|\\\\)*)+)(,|$)";
  private static final Pattern parserRegex = Pattern.compile(PARSER_REGEX);

  private final List<String> arguments;

  public ListParser(String commaSepArgs) {
    arguments = new ArrayList<String>();
    if (commaSepArgs == null || commaSepArgs.length() < 1) {
      return;
    }

    final Matcher matcher = parserRegex.matcher(commaSepArgs);
    while (matcher.find()) {
      final String arg = matcher.group(GROUP_IDX_ARG);
      final String comma = matcher.group(GROUP_IDX_COMMA);
      final String trimmedArg = arg.trim();
      final String unEscArg = trimmedArg.replace("\\,", ",");
      arguments.add(unEscArg);
      // If this is that last extracted argument then quit
      if ("".equals(comma)) {
        break;
      }
    }
  }

  public List<String> getArguments() {
    return arguments;
  }
}
