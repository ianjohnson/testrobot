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
package org.splandroid.tr.testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.splandroid.tr.ICalled;
import org.splandroid.tr.MockeryLoggingTestCase;
import org.splandroid.tr.parsers.ITestArguments;
import org.splandroid.tr.parsers.ITestDescriptor;
import org.splandroid.tr.reporting.IReportFacade;
import org.splandroid.tr.testing.TRTestCase;
import org.splandroid.tr.testing.TRTestFailed;
import org.splandroid.tr.testing.TRTestRunner;
import org.splandroid.tr.testing.annotations.Test;
import org.splandroid.tr.testing.annotations.TestExecuteSetter;
import org.splandroid.tr.testing.annotations.TestSetter;

class SetterValues {
  private Boolean valueBoolean;
  private Double valueDouble;
  private Float valueFloat;
  private Long valueLong;
  private Integer valueInteger;
  private String valueString;

  public SetterValues(Boolean valueBoolean, Double valueDouble,
      Float valueFloat, Long valueLong, Integer valueInteger, String valueString) {
    super();
    this.valueBoolean = valueBoolean;
    this.valueDouble = valueDouble;
    this.valueFloat = valueFloat;
    this.valueLong = valueLong;
    this.valueInteger = valueInteger;
    this.valueString = valueString;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((valueBoolean == null) ? 0 : valueBoolean.hashCode());
    result = prime * result
        + ((valueDouble == null) ? 0 : valueDouble.hashCode());
    result = prime * result
        + ((valueFloat == null) ? 0 : valueFloat.hashCode());
    result = prime * result
        + ((valueInteger == null) ? 0 : valueInteger.hashCode());
    result = prime * result + ((valueLong == null) ? 0 : valueLong.hashCode());
    result = prime * result
        + ((valueString == null) ? 0 : valueString.hashCode());
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
    final SetterValues other = (SetterValues )obj;
    if (valueBoolean == null) {
      if (other.valueBoolean != null)
        return false;
    } else if (!valueBoolean.equals(other.valueBoolean))
      return false;
    if (valueDouble == null) {
      if (other.valueDouble != null)
        return false;
    } else if (!valueDouble.equals(other.valueDouble))
      return false;
    if (valueFloat == null) {
      if (other.valueFloat != null)
        return false;
    } else if (!valueFloat.equals(other.valueFloat))
      return false;
    if (valueInteger == null) {
      if (other.valueInteger != null)
        return false;
    } else if (!valueInteger.equals(other.valueInteger))
      return false;
    if (valueLong == null) {
      if (other.valueLong != null)
        return false;
    } else if (!valueLong.equals(other.valueLong))
      return false;
    if (valueString == null) {
      if (other.valueString != null)
        return false;
    } else if (!valueString.equals(other.valueString))
      return false;
    return true;
  }
}

public class TRTestRunnerTest extends MockeryLoggingTestCase {
  private static final String METHOD_SET_BOOLEAN = "__set_boolean_";
  private static final String METHOD_SET_DOUBLE = "__set_double_";
  private static final String METHOD_SET_FLOAT = "__set_float_";
  private static final String METHOD_SET_LONG = "__set_long_";
  private static final String METHOD_SET_INTEGER = "__set_integer_";
  private static final String METHOD_SET_STRING = "__set_string_";
  private static final String METHOD_SETUP = "__setup_";
  private static final String METHOD_TEARDOWN = "__teardown_";
  private static final String METHOD_EXEC = "__execute_";
  private static final String METHOD_TEST_ONE = "__test_one_";
  private static final String METHOD_TEST_TWO = "__test_two_";
  private static final String METHOD_TEST_THREE = "__test_three_";

  private static final String booleanArgName = "boolean";
  private static final String doubleArgName = "double";
  private static final String floatArgName = "float";
  private static final String longArgName = "long";
  private static final String intArgName = "integer";
  private static final String stringArgName = "string";

  private static final String MSG_FAILURE = "test failed";
  private static final String MSG_ASSERT_FAILURE = "assert test failed";

  private IReportFacade reporter;
  private TRTestRunner testRunner;

  /**
   * An implemented test class with setter being both execution and test setter
   * methods. Calls to test methods are logged in an ICalled object. Set values
   * are checked along with any environment.
   */
  class TestClassMixedSetters extends TRTestCase {
    private ICalled called;
    private Map<String, SetterValues> targetValues;
    private Map<String, String> targetEnvironment;

    private Boolean valueBoolean;
    private Double valueDouble;
    private Float valueFloat;
    private Long valueLong;
    private Integer valueInteger;
    private String valueString;

    public TestClassMixedSetters(String testId, String description,
        List<ITestDescriptor> tests, ITestArguments setUpInfo,
        Map<String, String> environment, ICalled methodCalls) {
      super(testId, description, tests, setUpInfo, environment);
      called = methodCalls;
      targetValues = new HashMap<String, SetterValues>();
      targetEnvironment = null;
    }

    private void checkValues(String targetId) throws TRTestFailed {
      final SetterValues target = targetValues.get(targetId);
      if (targetValues == null) {
        fail(String.format("Invalid target ID [%s]", targetId));
      }
      final SetterValues currentValues = new SetterValues(valueBoolean,
          valueDouble, valueFloat, valueLong, valueInteger, valueString);
      assertTrue(target.equals(currentValues));

      final Map<String, String> environment = getEnvironment();
      if (environment != null) {
        targetEnvironment.equals(environment);
      }
    }

    @TestExecuteSetter
    @TestSetter
    public void setBoolean(Boolean b) {
      valueBoolean = b;
      called.called(METHOD_SET_BOOLEAN);
    }

    @TestExecuteSetter
    @TestSetter
    public void setDouble(Double d) {
      valueDouble = d;
      called.called(METHOD_SET_DOUBLE);
    }

    @TestExecuteSetter
    @TestSetter
    public void setFloat(Float f) {
      valueFloat = f;
      called.called(METHOD_SET_FLOAT);
    }

    @TestExecuteSetter
    @TestSetter
    public void setLong(Long l) {
      valueLong = l;
      called.called(METHOD_SET_LONG);
    }

    @TestExecuteSetter
    @TestSetter
    public void setInteger(Integer i) {
      valueInteger = i;
      called.called(METHOD_SET_INTEGER);
    }

    @TestExecuteSetter
    @TestSetter
    public void setString(String s) {
      valueString = s;
      called.called(METHOD_SET_STRING);
    }

    public void execute() {
      checkValues(METHOD_EXEC);
      called.called(METHOD_EXEC);
    }

    @Test
    public void testOne() {
      checkValues(METHOD_TEST_ONE);
      called.called(METHOD_TEST_ONE);
    }

    @Test
    public void testTwo() {
      checkValues(METHOD_TEST_TWO);
      called.called(METHOD_TEST_TWO);
    }

    public void setTargetValues(String methodId, boolean b, double d, float f,
        long l, int i, String s) {
      final SetterValues target = new SetterValues(new Boolean(b),
          new Double(d), new Float(f), new Long(l), new Integer(i), s);
      targetValues.put(methodId, target);
    }

    public void setTargetEnvironment(Map<String, String> target) {
      targetEnvironment = target;
    }
  }

  /**
   * An implemented test class that will fail in the execution phase.
   */
  class TestClassExecFailure extends TRTestCase {
    private boolean isAssert;

    public TestClassExecFailure(String testId, String description,
        List<ITestDescriptor> tests, ITestArguments setUpInfo,
        Map<String, String> environment, boolean isAssertFlag) {
      super(testId, description, tests, setUpInfo, environment);
      isAssert = isAssertFlag;
    }

    public void execute() {
      if (isAssert) {
        assertTrue(MSG_ASSERT_FAILURE, false);
      } else {
        fail(MSG_FAILURE);
      }
    }
  }

  /**
   * An implemented test class that will fail in the testing phase
   */
  class TestClassTestFailure extends TRTestCase {
    private ICalled testCalls;

    public TestClassTestFailure(String testId, String description,
        List<ITestDescriptor> tests, ITestArguments setUpInfo,
        Map<String, String> environment, ICalled calls) {
      super(testId, description, tests, setUpInfo, environment);
      testCalls = calls;
    }

    public void execute() {
    }

    @Test
    public void testOne() {
      testCalls.called(METHOD_TEST_ONE);
      assertTrue(MSG_ASSERT_FAILURE, false);
    }

    @Test
    public void testTwo() {
      testCalls.called(METHOD_TEST_TWO);
      fail(MSG_FAILURE);
    }

    @Test
    public void testThree() {
      testCalls.called(METHOD_TEST_THREE);
      assertTrue(true);
    }
  }

  /**
   * An implemented test class that uses setup and tear down
   */
  class TestClassTestSetupTearDown extends TRTestCase {
    private final ICalled testCalls;
    private final boolean executeFailure;

    public TestClassTestSetupTearDown(String testId, String description,
        List<ITestDescriptor> tests, ITestArguments setUpInfo,
        Map<String, String> environment, ICalled calls) {
      super(testId, description, tests, setUpInfo, environment);
      testCalls = calls;
      executeFailure = false;
    }

    public TestClassTestSetupTearDown(String testId, String description,
        List<ITestDescriptor> tests, ITestArguments setUpInfo,
        Map<String, String> environment, ICalled calls, boolean execFail) {
      super(testId, description, tests, setUpInfo, environment);
      testCalls = calls;
      executeFailure = execFail;
    }

    public void setUp() {
      testCalls.called(METHOD_SETUP);
    }

    public void tearDown() {
      testCalls.called(METHOD_TEARDOWN);
    }

    public void execute() throws Exception {
      if (executeFailure) {
        throw new Exception("TestClassTestSetupTearDown#execute failed");
      }
    }
  }

  public void setUp() {
    super.setUp();
    reporter = context.mock(IReportFacade.class, "reporter");
    testRunner = new TRTestRunner(reporter);
  }

  public void tearDown() {
    super.tearDown();
    testRunner = null;
    reporter = null;
  }

  /**
   * A test for the test class that contains an environment
   */
  public void testTestWithEnvironment() {
    final String id = "Test with no environment";
    final String desc = "A test to test the test class with no environment";

    final Boolean testOneBooleanArgValue = new Boolean(true);
    final Double testOneDoubleArgValue = new Double("3.141");
    final Float testOneFloatArgValue = new Float("2.718");
    final Long testOneLongArgValue = new Long("667");
    final Integer testOneIntArgValue = new Integer("42");
    final String testOneStringArgValue = new String(
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 "
            + "¬`!\"£$%^&*()_+-={[}]~#@'?/>.<,|\\'");

    final Boolean testTwoBooleanArgValue = new Boolean(false);
    final Double testTwoDoubleArgValue = new Double("-2.718");
    final Float testTwoFloatArgValue = new Float("-3.141");
    final Long testTwoLongArgValue = new Long("-42");
    final Integer testTwoIntArgValue = new Integer("-667");
    final String testTwoStringArgValue = new String("Some string or other");

    final Boolean execBooleanArgValue = new Boolean(true);
    final Double execDoubleArgValue = new Double("39.34763");
    final Float execFloatArgValue = new Float("-9.465");
    final Long execLongArgValue = new Long("-12");
    final Integer execIntArgValue = new Integer("-42");
    final String execStringArgValue = new String("execute string");

    final String testOneId = "one";
    final ITestArguments testOneArgs = context.mock(ITestArguments.class,
        "Test one arguments");
    final String testTwoId = "two";
    final ITestArguments testTwoArgs = context.mock(ITestArguments.class,
        "Test two arguments");

    final ITestDescriptor testOneDesc = context.mock(ITestDescriptor.class,
        "Test one descriptor");
    final ITestDescriptor testTwoDesc = context.mock(ITestDescriptor.class,
        "Test two descriptor");
    final List<ITestDescriptor> tests = new ArrayList<ITestDescriptor>();
    tests.add(testOneDesc);
    tests.add(testTwoDesc);

    final ITestArguments setUpInfo = context.mock(ITestArguments.class,
        "Setup info");

    final Set<String> argsKeySet = new HashSet<String>();
    argsKeySet.add(booleanArgName);
    argsKeySet.add(doubleArgName);
    argsKeySet.add(floatArgName);
    argsKeySet.add(longArgName);
    argsKeySet.add(intArgName);
    argsKeySet.add(stringArgName);

    final ICalled calls = context.mock(ICalled.class, "Test class calls");

    final Map<String, String> environment = new HashMap<String, String>();
    environment.put("ENV_VAR_ONE", "ENV_VALUE_ONE");
    environment.put("ENV_VAR_TWO", "ENV_VALUE_TWO");
    environment.put("ENV_VAR_THREE", "ENV_VALUE_THREE");

    context.checking(new Expectations() {
      {
        // Test one args
        one(testOneArgs).get(booleanArgName);
        will(returnValue(testOneBooleanArgValue));

        one(testOneArgs).get(doubleArgName);
        will(returnValue(testOneDoubleArgValue));

        one(testOneArgs).get(floatArgName);
        will(returnValue(testOneFloatArgValue));

        one(testOneArgs).get(longArgName);
        will(returnValue(testOneLongArgValue));

        one(testOneArgs).get(intArgName);
        will(returnValue(testOneIntArgValue));

        one(testOneArgs).get(stringArgName);
        will(returnValue(testOneStringArgValue));

        one(testOneArgs).keySet();
        will(returnValue(argsKeySet));

        // Test two args
        one(testTwoArgs).get(booleanArgName);
        will(returnValue(testTwoBooleanArgValue));

        one(testTwoArgs).get(doubleArgName);
        will(returnValue(testTwoDoubleArgValue));

        one(testTwoArgs).get(floatArgName);
        will(returnValue(testTwoFloatArgValue));

        one(testTwoArgs).get(longArgName);
        will(returnValue(testTwoLongArgValue));

        one(testTwoArgs).get(intArgName);
        will(returnValue(testTwoIntArgValue));

        one(testTwoArgs).get(stringArgName);
        will(returnValue(testTwoStringArgValue));

        one(testTwoArgs).keySet();
        will(returnValue(argsKeySet));

        // Execute set up info
        one(setUpInfo).get(booleanArgName);
        will(returnValue(execBooleanArgValue));

        one(setUpInfo).get(doubleArgName);
        will(returnValue(execDoubleArgValue));

        one(setUpInfo).get(floatArgName);
        will(returnValue(execFloatArgValue));

        one(setUpInfo).get(longArgName);
        will(returnValue(execLongArgValue));

        one(setUpInfo).get(intArgName);
        will(returnValue(execIntArgValue));

        one(setUpInfo).get(stringArgName);
        will(returnValue(execStringArgValue));

        one(setUpInfo).keySet();
        will(returnValue(argsKeySet));

        // Test one descriptor
        atLeast(1).of(testOneDesc).getId();
        will(returnValue(testOneId));

        one(testOneDesc).getArguments();
        will(returnValue(testOneArgs));

        // Test two descriptor
        atLeast(1).of(testTwoDesc).getId();
        will(returnValue(testTwoId));

        one(testTwoDesc).getArguments();
        will(returnValue(testTwoArgs));

        // Test class method calls
        exactly(3).of(calls).called(METHOD_SET_BOOLEAN);
        exactly(3).of(calls).called(METHOD_SET_DOUBLE);
        exactly(3).of(calls).called(METHOD_SET_FLOAT);
        exactly(3).of(calls).called(METHOD_SET_LONG);
        exactly(3).of(calls).called(METHOD_SET_INTEGER);
        exactly(3).of(calls).called(METHOD_SET_STRING);
        one(calls).called(METHOD_EXEC);
        one(calls).called(METHOD_TEST_ONE);
        one(calls).called(METHOD_TEST_TWO);

        // Reporter
        one(reporter).startingTestCase(id, desc);
        one(reporter).passedTestCase(id);
        one(reporter).startingTest(testOneId);
        one(reporter).passedTest(testOneId);
        one(reporter).startingTest(testTwoId);
        one(reporter).passedTest(testTwoId);
      }
    });

    final TestClassMixedSetters testCase = new TestClassMixedSetters(id, desc,
        tests, setUpInfo, environment, calls);
    testCase.setTargetValues(METHOD_TEST_ONE, testOneBooleanArgValue,
        testOneDoubleArgValue, testOneFloatArgValue, testOneLongArgValue,
        testOneIntArgValue, testOneStringArgValue);
    testCase.setTargetValues(METHOD_TEST_TWO, testTwoBooleanArgValue,
        testTwoDoubleArgValue, testTwoFloatArgValue, testTwoLongArgValue,
        testTwoIntArgValue, testTwoStringArgValue);
    testCase.setTargetValues(METHOD_EXEC, execBooleanArgValue,
        execDoubleArgValue, execFloatArgValue, execLongArgValue,
        execIntArgValue, execStringArgValue);
    testCase.setTargetEnvironment(environment);

    testRunner.setTestCase(testCase);
    final boolean passed = testRunner.run();
    assertTrue(passed == true);

    context.assertIsSatisfied();
  }

  /**
   * A test for the test class containing no environment
   */
  public void testTestWithNoEnvironment() {
    final String id = "Test with an environment";
    final String desc = "A test to test the test class with an environment";

    final Boolean testOneBooleanArgValue = new Boolean(false);
    final Double testOneDoubleArgValue = new Double("-16.384");
    final Float testOneFloatArgValue = new Float("-9.000005");
    final Long testOneLongArgValue = new Long("14");
    final Integer testOneIntArgValue = new Integer("-78");
    final String testOneStringArgValue = new String(
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 "
            + "¬`!\"£$%^&*()_+-={[}]~#@'?/>.<,|\\'");

    final Boolean testTwoBooleanArgValue = new Boolean(false);
    final Double testTwoDoubleArgValue = new Double("2.718");
    final Float testTwoFloatArgValue = new Float("3.141");
    final Long testTwoLongArgValue = new Long("42");
    final Integer testTwoIntArgValue = new Integer("667");
    final String testTwoStringArgValue = new String("Some string or other");

    final Boolean execBooleanArgValue = new Boolean(true);
    final Double execDoubleArgValue = new Double("39.34763");
    final Float execFloatArgValue = new Float("-9.465");
    final Long execLongArgValue = new Long("-12");
    final Integer execIntArgValue = new Integer("-42");
    final String execStringArgValue = new String("execute string");

    final String testOneId = "one";
    final ITestArguments testOneArgs = context.mock(ITestArguments.class,
        "Test one arguments");
    final String testTwoId = "two";
    final ITestArguments testTwoArgs = context.mock(ITestArguments.class,
        "Test two arguments");

    final ITestDescriptor testOneDesc = context.mock(ITestDescriptor.class,
        "Test one descriptor");
    final ITestDescriptor testTwoDesc = context.mock(ITestDescriptor.class,
        "Test two descriptor");
    final List<ITestDescriptor> tests = new ArrayList<ITestDescriptor>();
    tests.add(testOneDesc);
    tests.add(testTwoDesc);

    final ITestArguments setUpInfo = context.mock(ITestArguments.class,
        "Setup info");

    final Set<String> argsKeySet = new HashSet<String>();
    argsKeySet.add(booleanArgName);
    argsKeySet.add(doubleArgName);
    argsKeySet.add(floatArgName);
    argsKeySet.add(longArgName);
    argsKeySet.add(intArgName);
    argsKeySet.add(stringArgName);

    final ICalled calls = context.mock(ICalled.class, "Test class calls");

    context.checking(new Expectations() {
      {
        // Test one args
        one(testOneArgs).get(booleanArgName);
        will(returnValue(testOneBooleanArgValue));

        one(testOneArgs).get(doubleArgName);
        will(returnValue(testOneDoubleArgValue));

        one(testOneArgs).get(floatArgName);
        will(returnValue(testOneFloatArgValue));

        one(testOneArgs).get(longArgName);
        will(returnValue(testOneLongArgValue));

        one(testOneArgs).get(intArgName);
        will(returnValue(testOneIntArgValue));

        one(testOneArgs).get(stringArgName);
        will(returnValue(testOneStringArgValue));

        one(testOneArgs).keySet();
        will(returnValue(argsKeySet));

        // Test two args
        one(testTwoArgs).get(booleanArgName);
        will(returnValue(testTwoBooleanArgValue));

        one(testTwoArgs).get(doubleArgName);
        will(returnValue(testTwoDoubleArgValue));

        one(testTwoArgs).get(floatArgName);
        will(returnValue(testTwoFloatArgValue));

        one(testTwoArgs).get(longArgName);
        will(returnValue(testTwoLongArgValue));

        one(testTwoArgs).get(intArgName);
        will(returnValue(testTwoIntArgValue));

        one(testTwoArgs).get(stringArgName);
        will(returnValue(testTwoStringArgValue));

        one(testTwoArgs).keySet();
        will(returnValue(argsKeySet));

        // Execute set up info
        one(setUpInfo).get(booleanArgName);
        will(returnValue(execBooleanArgValue));

        one(setUpInfo).get(doubleArgName);
        will(returnValue(execDoubleArgValue));

        one(setUpInfo).get(floatArgName);
        will(returnValue(execFloatArgValue));

        one(setUpInfo).get(longArgName);
        will(returnValue(execLongArgValue));

        one(setUpInfo).get(intArgName);
        will(returnValue(execIntArgValue));

        one(setUpInfo).get(stringArgName);
        will(returnValue(execStringArgValue));

        one(setUpInfo).keySet();
        will(returnValue(argsKeySet));

        // Test one descriptor
        atLeast(1).of(testOneDesc).getId();
        will(returnValue(testOneId));

        one(testOneDesc).getArguments();
        will(returnValue(testOneArgs));

        // Test two descriptor
        atLeast(1).of(testTwoDesc).getId();
        will(returnValue(testTwoId));

        one(testTwoDesc).getArguments();
        will(returnValue(testTwoArgs));

        // Test class method calls
        exactly(3).of(calls).called(METHOD_SET_BOOLEAN);
        exactly(3).of(calls).called(METHOD_SET_DOUBLE);
        exactly(3).of(calls).called(METHOD_SET_FLOAT);
        exactly(3).of(calls).called(METHOD_SET_LONG);
        exactly(3).of(calls).called(METHOD_SET_INTEGER);
        exactly(3).of(calls).called(METHOD_SET_STRING);
        one(calls).called(METHOD_EXEC);
        one(calls).called(METHOD_TEST_ONE);
        one(calls).called(METHOD_TEST_TWO);

        // Reporter
        one(reporter).startingTestCase(id, desc);
        one(reporter).passedTestCase(id);
        one(reporter).startingTest(testOneId);
        one(reporter).passedTest(testOneId);
        one(reporter).startingTest(testTwoId);
        one(reporter).passedTest(testTwoId);
      }
    });

    final TestClassMixedSetters testCase = new TestClassMixedSetters(id, desc,
        tests, setUpInfo, null, calls);
    testCase.setTargetValues(METHOD_TEST_ONE, testOneBooleanArgValue,
        testOneDoubleArgValue, testOneFloatArgValue, testOneLongArgValue,
        testOneIntArgValue, testOneStringArgValue);
    testCase.setTargetValues(METHOD_TEST_TWO, testTwoBooleanArgValue,
        testTwoDoubleArgValue, testTwoFloatArgValue, testTwoLongArgValue,
        testTwoIntArgValue, testTwoStringArgValue);
    testCase.setTargetValues(METHOD_EXEC, execBooleanArgValue,
        execDoubleArgValue, execFloatArgValue, execLongArgValue,
        execIntArgValue, execStringArgValue);

    testRunner.setTestCase(testCase);
    final boolean passed = testRunner.run();
    assertTrue(passed == true);

    context.assertIsSatisfied();
  }

  /**
   * Test both failure and assertion in the execution phase
   */
  public void testExecutionPhaseFailure() {
    final String id = "Test with no environment";
    final String desc = "A test to test the test class with no environment";

    final List<ITestDescriptor> emptyTests = new ArrayList<ITestDescriptor>();
    final ITestArguments setUpInfo = context.mock(ITestArguments.class,
        "Setup info");

    final boolean[] failureModes = { true, false };

    for (boolean isAssert : failureModes) {
      // Assert failure
      context.checking(new Expectations() {
        {
          one(setUpInfo).keySet();
          will(returnValue(new HashSet<String>()));

          // Reporter
          one(reporter).startingTestCase(id, desc);
          one(reporter).errorTestCase(with(any(String.class)),
              with(any(Throwable.class)));
        }
      });

      final TestClassExecFailure testCase = new TestClassExecFailure(id, desc,
          emptyTests, setUpInfo, null, isAssert);

      testRunner.setTestCase(testCase);
      final boolean passed = testRunner.run();
      assertTrue(passed == false);

      context.assertIsSatisfied();
    }
  }

  /**
   * Test both failure and assertion in the testing phase
   */
  public void testTestPhaseFailure() {
    final String id = "Test with no environment";
    final String desc = "A test to test the test class with no environment";

    final ITestArguments testArgs = context.mock(ITestArguments.class,
        "Test arguments");

    final String testZeroId = "zero-missing-test-method";
    final ITestDescriptor testZeroDesc = context.mock(ITestDescriptor.class,
        "Missing test zero method");

    final String testOneId = "one";
    final ITestDescriptor testOneDesc = context.mock(ITestDescriptor.class,
        "Test one descriptor");

    final String testTwoId = "two";
    final ITestDescriptor testTwoDesc = context.mock(ITestDescriptor.class,
        "Test two descriptor");

    final String testThreeId = "three";
    final ITestDescriptor testThreeDesc = context.mock(ITestDescriptor.class,
        "Test three descriptor");

    final List<ITestDescriptor> tests = new ArrayList<ITestDescriptor>();
    tests.add(testZeroDesc);
    tests.add(testOneDesc);
    tests.add(testTwoDesc);
    tests.add(testThreeDesc);

    final ITestArguments setUpInfo = context.mock(ITestArguments.class,
        "Setup info");

    final ICalled testCalls = context.mock(ICalled.class, "TestCalls");

    // Assert failure
    context.checking(new Expectations() {
      {
        // Test arguments
        exactly(tests.size()).of(testArgs).keySet();
        will(returnValue(new HashSet<String>()));

        // Test zero descriptor
        atLeast(1).of(testZeroDesc).getId();
        will(returnValue(testZeroId));

        one(testZeroDesc).getArguments();
        will(returnValue(testArgs));

        // Test one descriptor
        atLeast(1).of(testOneDesc).getId();
        will(returnValue(testOneId));

        one(testOneDesc).getArguments();
        will(returnValue(testArgs));

        // Test two descriptor
        atLeast(1).of(testTwoDesc).getId();
        will(returnValue(testTwoId));

        one(testTwoDesc).getArguments();
        will(returnValue(testArgs));

        // Test three descriptor
        atLeast(1).of(testThreeDesc).getId();
        will(returnValue(testThreeId));

        one(testThreeDesc).getArguments();
        will(returnValue(testArgs));

        // Setup info
        one(setUpInfo).keySet();
        will(returnValue(new HashSet<String>()));

        // Test calls
        one(testCalls).called(METHOD_TEST_ONE);
        one(testCalls).called(METHOD_TEST_TWO);
        one(testCalls).called(METHOD_TEST_THREE);

        // Reporter
        one(reporter).startingTestCase(id, desc);
        one(reporter).failedTestCase(id);

        one(reporter).startingTest(testZeroId);
        one(reporter).errorTest(with(any(String.class)),
            with(any(Throwable.class)));

        one(reporter).startingTest(testOneId);
        one(reporter).failedTest(testOneId, MSG_ASSERT_FAILURE);

        one(reporter).startingTest(testTwoId);
        one(reporter).failedTest(testTwoId, MSG_FAILURE);

        one(reporter).startingTest(testThreeId);
        one(reporter).passedTest(testThreeId);
      }
    });

    final TestClassTestFailure testCase = new TestClassTestFailure(id, desc,
        tests, setUpInfo, null, testCalls);

    testRunner.setTestCase(testCase);
    final boolean passed = testRunner.run();
    assertTrue(passed == false);

    context.assertIsSatisfied();
  }

  public void testSetupAndTeardown() {
    final String id = "Test setup and tear down";
    final String desc = "A test using setup and tear down";

    final List<ITestDescriptor> tests = new ArrayList<ITestDescriptor>();

    final ITestArguments setUpInfo = context.mock(ITestArguments.class,
        "Setup info");

    final ICalled testCalls = context.mock(ICalled.class, "TestCalls");

    // Assert failure
    context.checking(new Expectations() {
      {
        // Setup info
        one(setUpInfo).keySet();
        will(returnValue(new HashSet<String>()));

        // Test calls
        one(testCalls).called(METHOD_SETUP);
        one(testCalls).called(METHOD_TEARDOWN);

        // Reporter
        one(reporter).startingTestCase(id, desc);
        one(reporter).passedTestCase(id);
      }
    });

    final TestClassTestSetupTearDown testCase = new TestClassTestSetupTearDown(
        id, desc, tests, setUpInfo, null, testCalls);

    testRunner.setTestCase(testCase);
    final boolean passed = testRunner.run();
    assertTrue(passed == true);

    context.assertIsSatisfied();
  }

  /**
   * Tests that even if the execute phase fails the tear down method shall still
   * be called.
   */
  public void testSetupAndTeardownWithExecFailure() {
    final String id = "Test setup and tear down with execute phase failure";
    final String desc = "A test using setup and tear down with execute phase failure";

    final List<ITestDescriptor> tests = new ArrayList<ITestDescriptor>();

    final ITestArguments setUpInfo = context.mock(ITestArguments.class,
        "Setup info");

    final ICalled testCalls = context.mock(ICalled.class, "TestCalls");

    // Assert failure
    context.checking(new Expectations() {
      {
        // Setup info
        one(setUpInfo).keySet();
        will(returnValue(new HashSet<String>()));

        // Test calls
        one(testCalls).called(METHOD_SETUP);
        one(testCalls).called(METHOD_TEARDOWN);

        // Reporter
        one(reporter).startingTestCase(id, desc);
        one(reporter).errorTestCase(with(any(String.class)),
            with(any(Throwable.class)));
      }
    });

    final TestClassTestSetupTearDown testCase = new TestClassTestSetupTearDown(
        id, desc, tests, setUpInfo, null, testCalls, true);

    testRunner.setTestCase(testCase);
    final boolean passed = testRunner.run();
    assertTrue(passed == false);

    context.assertIsSatisfied();
  }
}
