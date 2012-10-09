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
package org.splandroid.tr.extensions.simpledemo;

import java.util.List;
import java.util.Map;

import org.splandroid.tr.parsers.ITestArguments;
import org.splandroid.tr.parsers.ITestDescriptor;
import org.splandroid.tr.testing.TRTestCase;
import org.splandroid.tr.testing.annotations.Test;
import org.splandroid.tr.testing.annotations.TestExecuteSetter;
import org.splandroid.tr.testing.annotations.TestSetter;

public class Demo extends TRTestCase {

  private Boolean booleanValue;
  private Double doubleValue;
  private Float floatValue;
  private Long longValue;
  private Integer integerValue;
  private String stringValue;
  private IntegerPair pairValue;

  private Boolean targetBooleanValue;
  private Double targetDoubleValue;
  private Float targetFloatValue;
  private Long targetLongValue;
  private Integer targetIntegerValue;
  private String targetStringValue;
  private IntegerPair targetPairValue;

  public Demo(String testId, String description, List<ITestDescriptor> tests,
      ITestArguments setUpInfo, Map<String, String> environment) {
    super(testId, description, tests, setUpInfo, environment);
  }

  @TestExecuteSetter
  public void setBoolean(Boolean value) {
    booleanValue = value;
  }

  @TestExecuteSetter
  public void setDouble(Double value) {
    doubleValue = value;
  }

  @TestExecuteSetter
  public void setFloat(Float value) {
    floatValue = value;
  }

  @TestExecuteSetter
  public void setLong(Long value) {
    longValue = value;
  }

  @TestExecuteSetter
  public void setInteger(Integer value) {
    integerValue = value;
  }

  @TestExecuteSetter
  public void setString(String value) {
    stringValue = value;
  }

  @TestExecuteSetter
  public void setCustom(IntegerPair value) {
    pairValue = value;
  }

  public void execute() throws Exception {
    booleanValue = new Boolean(!booleanValue.booleanValue());
    doubleValue = new Double(-doubleValue.doubleValue());
    floatValue = new Float(-floatValue.floatValue());
    longValue = new Long(-longValue.longValue());
    integerValue = new Integer(-integerValue.intValue());
    stringValue = reverse(stringValue);
    pairValue = new IntegerPair(pairValue.getB(), pairValue.getA());
  }

  @TestSetter
  public void setTargetBoolean(Boolean value) {
    targetBooleanValue = value;
  }

  @TestSetter
  public void setTargetDouble(Double value) {
    targetDoubleValue = value;
  }

  @TestSetter
  public void setTargetFloat(Float value) {
    targetFloatValue = value;
  }

  @TestSetter
  public void setTargetLong(Long value) {
    targetLongValue = value;
  }

  @TestSetter
  public void setTargetInteger(Integer value) {
    targetIntegerValue = value;
  }

  @TestSetter
  public void setTargetString(String value) {
    targetStringValue = value;
  }

  @TestSetter
  public void setTargetCustom(IntegerPair value) {
    targetPairValue = value;
  }

  @Test
  public void testCheckValues() {
    assertTrue("Boolean", targetBooleanValue.equals(booleanValue));
    assertTrue("Double", targetDoubleValue.equals(doubleValue));
    assertTrue("Float", targetFloatValue.equals(floatValue));
    assertTrue("Long", targetLongValue.equals(longValue));
    assertTrue("Integer", targetIntegerValue.equals(integerValue));
    assertTrue("String", targetStringValue.equals(stringValue));
    assertTrue("Custom", targetPairValue.equals(pairValue));
  }

  private static String reverse(String source) {
    int i, len = source.length();
    StringBuffer dest = new StringBuffer(len);

    for (i = (len - 1); i >= 0; i--)
      dest.append(source.charAt(i));
    return dest.toString();
  }
}
