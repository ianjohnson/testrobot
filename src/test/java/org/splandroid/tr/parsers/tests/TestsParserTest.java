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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.splandroid.tr.TRException;
import org.splandroid.tr.MockeryLoggingTestCase;
import org.splandroid.tr.parsers.IArgumentSymbol;
import org.splandroid.tr.parsers.ICapabilitySymbol;
import org.splandroid.tr.parsers.IEnvironmentSymbol;
import org.splandroid.tr.parsers.IParser;
import org.splandroid.tr.parsers.ISymbolMap;
import org.splandroid.tr.parsers.ITestArguments;
import org.splandroid.tr.parsers.ITestCaseDescriptor;
import org.splandroid.tr.parsers.ITestDescriptor;
import org.splandroid.tr.parsers.ITestSymbol;
import org.splandroid.tr.parsers.ParserException;
import org.splandroid.tr.parsers.profile.ProfileParserTest;
import org.splandroid.tr.parsers.tests.TestsParser;
import org.splandroid.tr.parsers.tests.xml.InvalidXML;
import org.splandroid.tr.parsers.tests.xml.OneCapabilityOneTestNoEnvironmentXML;
import org.splandroid.tr.parsers.tests.xml.OneCapabilityOneTestXML;

public class TestsParserTest extends MockeryLoggingTestCase {
  private IParser parser;
  private List<ITestCaseDescriptor> testCases;
  private ISymbolMap symbolTable;

  private static Map<String, String> getTestsEnvironment(String[] envVarNames,
      String[] envVarValues, String[] testEnvVarNames, String[] testEnvVarValues) {
    final int testVarLen = testEnvVarNames.length;
    assert testVarLen == testEnvVarValues.length;
    final Map<String, String> map = new HashMap<String, String>();
    final List<IEnvironmentSymbol> profileEnv = ProfileParserTest
        .getProfileEnvironment(envVarNames, envVarValues);
    for (IEnvironmentSymbol envSymbol : profileEnv) {
      map.put(envSymbol.getId(), envSymbol.getValue());
    }
    for (int idx = 0; idx < testVarLen; idx++) {
      final String name = testEnvVarNames[idx];
      final String value = testEnvVarValues[idx];
      map.put(name, value);
    }
    return map;
  }

  public void setUp() {
    super.setUp();
    testCases = new ArrayList<ITestCaseDescriptor>();
    try {
      parser = new TestsParser(testCases);
    } catch (TRException ex) {
      fail(ex.getMessage());
    }
    symbolTable = context.mock(ISymbolMap.class);
    parser.setSymbolTable(symbolTable);
  }

  public void tearDown() {
    super.tearDown();
    testCases = null;
    parser = null;
    symbolTable = null;
  }

  public void testOneCapabilityWithOneTest() {
    // The capability
    final ICapabilitySymbol capSymbol = context.mock(ICapabilitySymbol.class,
        "Capability");
    final IArgumentSymbol capArg1 = context.mock(IArgumentSymbol.class,
        "Capability argument one");
    final IArgumentSymbol capArg2 = context.mock(IArgumentSymbol.class,
        "Capability argument two");
    final List<IArgumentSymbol> capArgs = new ArrayList<IArgumentSymbol>();
    capArgs.add(capArg1);
    capArgs.add(capArg2);

    // The test
    final ITestSymbol testSymbol = context.mock(ITestSymbol.class, "Test");
    final IArgumentSymbol testArg1 = context.mock(IArgumentSymbol.class,
        "Test argument one");
    final IArgumentSymbol testArg2 = context.mock(IArgumentSymbol.class,
        "Test argument two");
    final List<IArgumentSymbol> testArgs = new ArrayList<IArgumentSymbol>();
    testArgs.add(testArg1);
    testArgs.add(testArg2);

    // The test's environment
    final IEnvironmentSymbol envSymbolOne = context.mock(
        IEnvironmentSymbol.class, "Test environment one");
    final IEnvironmentSymbol envSymbolTwo = context.mock(
        IEnvironmentSymbol.class, "Test environment two");
    final List<IEnvironmentSymbol> testEnv = new ArrayList<IEnvironmentSymbol>();
    testEnv.add(envSymbolOne);
    testEnv.add(envSymbolTwo);

    context.checking(new Expectations() {
      {
        // Test environment one
        one(envSymbolOne).getId();
        will(returnValue(OneCapabilityOneTestXML.varNames[0]));

        one(envSymbolOne).getValue();
        will(returnValue(OneCapabilityOneTestXML.varValues[0]));

        // Test environment two
        one(envSymbolTwo).getId();
        will(returnValue(OneCapabilityOneTestXML.varNames[1]));

        one(envSymbolTwo).getValue();
        will(returnValue(OneCapabilityOneTestXML.varValues[1]));

        // Test argument one
        one(testArg1).getId();
        will(returnValue(OneCapabilityOneTestXML.argIds[0]));

        one(testArg1).getKindClass();
        will(returnValue(OneCapabilityOneTestXML.argClasses[0]));

        // Test argument two
        one(testArg2).getId();
        will(returnValue(OneCapabilityOneTestXML.argIds[1]));

        one(testArg2).getKindClass();
        will(returnValue(OneCapabilityOneTestXML.argClasses[1]));

        // Test symbol
        one(testSymbol).getId();
        will(returnValue(OneCapabilityOneTestXML.testId));

        one(testSymbol).getSymbolsInContext(IArgumentSymbol.CONTEXT);
        will(returnValue(testArgs));

        // Capability argument one
        one(capArg1).getId();
        will(returnValue(OneCapabilityOneTestXML.capArgId[0]));

        one(capArg1).getKindClass();
        will(returnValue(OneCapabilityOneTestXML.capArgClasses[0]));

        // Capability argument two
        one(capArg2).getId();
        will(returnValue(OneCapabilityOneTestXML.capArgId[1]));

        one(capArg2).getKindClass();
        will(returnValue(OneCapabilityOneTestXML.capArgClasses[1]));

        // Capability
        one(capSymbol).getId();
        will(returnValue(OneCapabilityOneTestXML.capabilityId));

        one(capSymbol).getKindClass();
        will(returnValue(OneCapabilityOneTestXML.capabilityClass));

        one(capSymbol).getSymbol(OneCapabilityOneTestXML.testId,
            ITestSymbol.CONTEXT);
        will(returnValue(testSymbol));

        one(capSymbol).getSymbolsInContext(IArgumentSymbol.CONTEXT);
        will(returnValue(capArgs));

        one(capSymbol).getSymbolsInContext(IEnvironmentSymbol.CONTEXT);
        will(returnValue(testEnv));

        // Symbol table
        one(symbolTable).get(OneCapabilityOneTestXML.capabilityId);
        will(returnValue(capSymbol));

        atLeast(1).of(symbolTable).containsKey(
            OneCapabilityOneTestXML.capabilityId);
        will(returnValue(true));

        atLeast(1).of(symbolTable).containsKey(OneCapabilityOneTestXML.testId);
        will(returnValue(false));
      }
    });

    final byte[] xmlBytes = OneCapabilityOneTestXML.testsXML.getBytes();
    final ByteArrayInputStream stream = new ByteArrayInputStream(xmlBytes);

    try {
      parser.parse(stream);
    } catch (ParserException ex) {
      logger.error(ex.getMessage());
      fail(ex.getMessage());
    }

    context.assertIsSatisfied();

    assertTrue(testCases.size() == 1);
    final ITestCaseDescriptor testCaseDesc = testCases.get(0);
    assertTrue(OneCapabilityOneTestXML.testCaseId.equals(testCaseDesc.getId()));
    assertTrue(OneCapabilityOneTestXML.testCaseDesc.equals(testCaseDesc
        .getDescription()));
    assertTrue(OneCapabilityOneTestXML.capabilityClass.equals(testCaseDesc
        .getClassName()));

    final ITestArguments setupInfo = testCaseDesc.getSetUpInfo();
    final int noSetupInfoEntries = OneCapabilityOneTestXML.capArgId.length;
    assertTrue(setupInfo.size() == noSetupInfoEntries);
    for (int idx = 0; idx < noSetupInfoEntries; idx++) {
      final Object value = setupInfo.get(OneCapabilityOneTestXML.capArgId[idx]);
      final Object target = OneCapabilityOneTestXML.testCapArgValueTarget[idx];
      assertTrue(target.equals(value.toString()));
    }

    final List<ITestDescriptor> tests = testCaseDesc.getTests();
    assertTrue(tests.size() == 1);
    final ITestDescriptor test = tests.get(0);
    assertTrue(OneCapabilityOneTestXML.testId.equals(test.getId()));
    final Map<String, Object> arguments = test.getArguments();
    for (int idx = 0; idx < OneCapabilityOneTestXML.argIds.length; idx++) {
      final String argName = OneCapabilityOneTestXML.argIds[idx];
      final Object argObject = arguments.get(argName);
      assertTrue(argObject != null);
      assertTrue(argObject.toString().equals(
          OneCapabilityOneTestXML.testArgValue[idx]));
    }

    final Map<String, String> env = testCaseDesc.getEnvironment();
    assertTrue(getTestsEnvironment(OneCapabilityOneTestXML.varNames,
        OneCapabilityOneTestXML.varValues, OneCapabilityOneTestXML.testEnvVar,
        OneCapabilityOneTestXML.testEnvValue).equals(env));
  }

  /**
   * The profile contains only one capability. The test contains no environment.
   */
  public void testOneCapabilityWithOneTestWithNoEnviornment() {
    // The capability
    final ICapabilitySymbol capSymbol = context.mock(ICapabilitySymbol.class,
        "Capability");
    final IArgumentSymbol capArg1 = context.mock(IArgumentSymbol.class,
        "Capability argument one");
    final IArgumentSymbol capArg2 = context.mock(IArgumentSymbol.class,
        "Capability argument two");
    final List<IArgumentSymbol> capArgs = new ArrayList<IArgumentSymbol>();
    capArgs.add(capArg1);
    capArgs.add(capArg2);

    // The test
    final ITestSymbol testSymbol = context.mock(ITestSymbol.class, "Test");
    final IArgumentSymbol testArg1 = context.mock(IArgumentSymbol.class,
        "Test argument one");
    final IArgumentSymbol testArg2 = context.mock(IArgumentSymbol.class,
        "Test argument two");
    final List<IArgumentSymbol> testArgs = new ArrayList<IArgumentSymbol>();
    testArgs.add(testArg1);
    testArgs.add(testArg2);

    // The test's environment
    final IEnvironmentSymbol envSymbolOne = context.mock(
        IEnvironmentSymbol.class, "Test environment one");
    final IEnvironmentSymbol envSymbolTwo = context.mock(
        IEnvironmentSymbol.class, "Test environment two");
    final List<IEnvironmentSymbol> testEnv = new ArrayList<IEnvironmentSymbol>();
    testEnv.add(envSymbolOne);
    testEnv.add(envSymbolTwo);

    // Build a fake symbol table
    context.checking(new Expectations() {
      {
        // Test environment one
        one(envSymbolOne).getId();
        will(returnValue(OneCapabilityOneTestNoEnvironmentXML.varNames[0]));

        one(envSymbolOne).getValue();
        will(returnValue(OneCapabilityOneTestNoEnvironmentXML.varValues[0]));

        // Test environment two
        one(envSymbolTwo).getId();
        will(returnValue(OneCapabilityOneTestNoEnvironmentXML.varNames[1]));

        one(envSymbolTwo).getValue();
        will(returnValue(OneCapabilityOneTestNoEnvironmentXML.varValues[1]));

        // Test argument one
        one(testArg1).getId();
        will(returnValue(OneCapabilityOneTestNoEnvironmentXML.argIds[0]));

        one(testArg1).getKindClass();
        will(returnValue(OneCapabilityOneTestNoEnvironmentXML.argClasses[0]));

        // Test argument two
        one(testArg2).getId();
        will(returnValue(OneCapabilityOneTestNoEnvironmentXML.argIds[1]));

        one(testArg2).getKindClass();
        will(returnValue(OneCapabilityOneTestNoEnvironmentXML.argClasses[1]));

        // Test symbol
        one(testSymbol).getId();
        will(returnValue(OneCapabilityOneTestNoEnvironmentXML.testId));

        one(testSymbol).getSymbolsInContext(IArgumentSymbol.CONTEXT);
        will(returnValue(testArgs));

        // Capability argument one
        one(capArg1).getId();
        will(returnValue(OneCapabilityOneTestNoEnvironmentXML.capArgId[0]));

        one(capArg1).getKindClass();
        will(returnValue(OneCapabilityOneTestNoEnvironmentXML.capArgClasses[0]));

        // Capability argument two
        one(capArg2).getId();
        will(returnValue(OneCapabilityOneTestNoEnvironmentXML.capArgId[1]));

        one(capArg2).getKindClass();
        will(returnValue(OneCapabilityOneTestNoEnvironmentXML.capArgClasses[1]));

        // Capability
        one(capSymbol).getId();
        will(returnValue(OneCapabilityOneTestNoEnvironmentXML.capabilityId));

        one(capSymbol).getKindClass();
        will(returnValue(OneCapabilityOneTestNoEnvironmentXML.capabilityClass));

        one(capSymbol).getSymbol(OneCapabilityOneTestNoEnvironmentXML.testId,
            ITestSymbol.CONTEXT);
        will(returnValue(testSymbol));

        one(capSymbol).getSymbolsInContext(IArgumentSymbol.CONTEXT);
        will(returnValue(capArgs));

        one(capSymbol).getSymbolsInContext(IEnvironmentSymbol.CONTEXT);
        will(returnValue(testEnv));

        // Symbol table
        one(symbolTable).get(OneCapabilityOneTestNoEnvironmentXML.capabilityId);
        will(returnValue(capSymbol));

        atLeast(1).of(symbolTable).containsKey(
            OneCapabilityOneTestNoEnvironmentXML.capabilityId);
        will(returnValue(true));

        atLeast(1).of(symbolTable).containsKey(
            OneCapabilityOneTestNoEnvironmentXML.testId);
        will(returnValue(false));
      }
    });

    final String xml = OneCapabilityOneTestNoEnvironmentXML.testsXML;
    final byte[] xmlBytes = xml.getBytes();
    final ByteArrayInputStream stream = new ByteArrayInputStream(xmlBytes);

    try {
      parser.parse(stream);
    } catch (ParserException ex) {
      logger.error(ex.getMessage());
      fail(ex.getMessage());
    }

    context.assertIsSatisfied();

    assertTrue(testCases.size() == 1);
    final ITestCaseDescriptor testCaseDesc = testCases.get(0);
    assertTrue(OneCapabilityOneTestNoEnvironmentXML.testCaseId
        .equals(testCaseDesc.getId()));
    assertTrue(OneCapabilityOneTestNoEnvironmentXML.testCaseDesc
        .equals(testCaseDesc.getDescription()));
    assertTrue(OneCapabilityOneTestNoEnvironmentXML.capabilityClass
        .equals(testCaseDesc.getClassName()));

    final ITestArguments setupInfo = testCaseDesc.getSetUpInfo();
    final int noSetupInfoEntries = OneCapabilityOneTestNoEnvironmentXML.capArgId.length;
    assertTrue(setupInfo.size() == noSetupInfoEntries);
    for (int idx = 0; idx < noSetupInfoEntries; idx++) {
      final Object value = setupInfo
          .get(OneCapabilityOneTestNoEnvironmentXML.capArgId[idx]);
      final Object target = OneCapabilityOneTestNoEnvironmentXML.testCapArgValueTarget[idx];
      assertTrue(target.equals(value.toString()));
    }

    final List<ITestDescriptor> tests = testCaseDesc.getTests();
    assertTrue(tests.size() == 1);
    final ITestDescriptor test = tests.get(0);
    assertTrue(OneCapabilityOneTestNoEnvironmentXML.testId.equals(test.getId()));
    final Map<String, Object> arguments = test.getArguments();
    for (int idx = 0; idx < OneCapabilityOneTestNoEnvironmentXML.argIds.length; idx++) {
      final String argName = OneCapabilityOneTestNoEnvironmentXML.argIds[idx];
      final Object argObject = arguments.get(argName);
      assertTrue(argObject != null);
      assertTrue(argObject.toString().equals(
          OneCapabilityOneTestNoEnvironmentXML.testArgValue[idx]));
    }

    final Map<String, String> env = testCaseDesc.getEnvironment();
    assertTrue(OneCapabilityOneTestNoEnvironmentXML.getTestsEnvironment()
        .equals(env));
  }

  public void testInvalidXML() {
    final byte[] xmlBytes = InvalidXML.xml.getBytes();
    final ByteArrayInputStream stream = new ByteArrayInputStream(xmlBytes);

    try {
      parser.parse(stream);
    } catch (ParserException ex) {
      assertTrue(true);
      return;
    }

    assertTrue(false);
  }
}
