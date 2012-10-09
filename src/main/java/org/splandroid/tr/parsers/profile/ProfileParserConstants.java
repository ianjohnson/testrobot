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

import org.splandroid.tr.parsers.ParserConstants;

class ProfileParserConstants {
  public static final String TAG_CAPABILITY = "capability";
  public static final String ATTR_CAPABILITY_ID = ParserConstants.ATTR_ID;
  public static final String ATTR_CAPABILITY_DESC = "description";
  public static final String ATTR_CAPABILITY_CLASS = ParserConstants.ATTR_CLASS;

  public static final String TAG_TEST = "test";
  public static final String ATTR_TEST_ID = ParserConstants.ATTR_ID;

  public static final String TAG_ARGUMENTS = "arguments";

  public static final String TAG_ARGUMENT = "argument";
  public static final String ATTR_ARGUMENT_ID = ParserConstants.ATTR_ID;
  public static final String ATTR_ARGUMENT_KIND = "kind";

  public static final String TAG_ENVIRONMENT = "environment";
  public static final String TAG_VARIABLE = "variable";
  public static final String TAG_EXISTS = "exists";
  public static final String ATTR_VARIABLE_NAME = "name";
  public static final String ATTR_VARIABLE_VALUE = "value";
}
