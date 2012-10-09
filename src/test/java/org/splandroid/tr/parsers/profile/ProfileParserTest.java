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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.jmock.Expectations;
import org.splandroid.tr.MockeryLoggingTestCase;
import org.splandroid.tr.parsers.IArgumentSymbol;
import org.splandroid.tr.parsers.IEnvironmentSymbol;
import org.splandroid.tr.parsers.IParser;
import org.splandroid.tr.parsers.ISymbol;
import org.splandroid.tr.parsers.ISymbolMap;
import org.splandroid.tr.parsers.ITestSymbol;
import org.splandroid.tr.parsers.ParserException;
import org.splandroid.tr.parsers.profile.ArgumentSymbol;
import org.splandroid.tr.parsers.profile.CapabilitySymbol;
import org.splandroid.tr.parsers.profile.EnvironmentSymbol;
import org.splandroid.tr.parsers.profile.ProfileParser;
import org.splandroid.tr.parsers.profile.ProfileParserException;
import org.splandroid.tr.parsers.profile.TestSymbol;
import org.splandroid.tr.parsers.profile.xml.EnvironmentTestXML;
import org.splandroid.tr.parsers.profile.xml.InvalidXML;
import org.splandroid.tr.parsers.profile.xml.OneCapabilityOneTestXML;

public class ProfileParserTest extends MockeryLoggingTestCase {
  private IParser parser;

  public static List<IEnvironmentSymbol> getProfileEnvironment(
      String[] varNames, String[] varValues) {
    final int varNamesLen = varNames.length;
    assert varNamesLen == varNames.length;
    final List<IEnvironmentSymbol> env = new ArrayList<IEnvironmentSymbol>();
    for (int idx = 0; idx < varNamesLen; idx++) {
      final IEnvironmentSymbol envSym = new EnvironmentSymbol(varNames[idx],
          varValues[idx]);
      env.add(envSym);
    }

    return env;
  }

  public void setUp() {
    super.setUp();
    try {
      parser = new ProfileParser();
    } catch (ProfileParserException ex) {
      fail(ex.getMessage());
    }
  }

  public void tearDown() {
    super.tearDown();
    parser = null;
  }

  /**
   * Test the number of symbols in the symbol table is one since there is one
   * capability.
   */
  public void testSymbolCountWithOneCapability() {
    final ISymbolMap symbolTable = context.mock(ISymbolMap.class);
    final CapabilitySymbol capSym = new CapabilitySymbol(
        OneCapabilityOneTestXML.capabilityId,
        OneCapabilityOneTestXML.capabilityDesc,
        OneCapabilityOneTestXML.capabilityClass);

    context.checking(new Expectations() {
      {
        one(symbolTable).put(OneCapabilityOneTestXML.capabilityId, capSym);
      }
    });

    final byte[] xmlBytes = OneCapabilityOneTestXML.profileXML.getBytes();
    final ByteArrayInputStream stream = new ByteArrayInputStream(xmlBytes);
    try {
      parser.setSymbolTable(symbolTable).parse(stream);
    } catch (ParserException ex) {
      fail(ex.getMessage());
    }

    context.assertIsSatisfied();
  }

  /**
   * Test symbol table is correct for one capability with one test and some
   * arguments and an environment.
   */
  public void testSymbolsWithOneCapabilityOneTest() {
    final ISymbolMap symbolTable = new ProfileParserTestSymbolMap();
    final byte[] xmlBytes = OneCapabilityOneTestXML.profileXML.getBytes();
    final ByteArrayInputStream stream = new ByteArrayInputStream(xmlBytes);
    try {
      parser.setSymbolTable(symbolTable).parse(stream);
    } catch (ParserException ex) {
      fail(ex.getMessage());
    }

    final CapabilitySymbol capSym = (CapabilitySymbol )symbolTable
        .get(OneCapabilityOneTestXML.capabilityId);
    assertEquals(new CapabilitySymbol(OneCapabilityOneTestXML.capabilityId,
        OneCapabilityOneTestXML.capabilityDesc,
        OneCapabilityOneTestXML.capabilityClass), capSym);

    final List<ArgumentSymbol> targetCapArgs = new ArrayList<ArgumentSymbol>();
    for (int idx = 0; idx < OneCapabilityOneTestXML.capArgId.length; idx++) {
      final ArgumentSymbol argSym = new ArgumentSymbol(
          OneCapabilityOneTestXML.capArgId[idx],
          OneCapabilityOneTestXML.capArgClasses[idx]);
      targetCapArgs.add(argSym);
    }
    final List<ISymbol> capArgs = capSym
        .getSymbolsInContext(IArgumentSymbol.CONTEXT);
    assertEquals(targetCapArgs, capArgs);

    final TestSymbol testSym = (TestSymbol )capSym.getSymbol(
        OneCapabilityOneTestXML.testId, ITestSymbol.CONTEXT);
    assertEquals(new TestSymbol(OneCapabilityOneTestXML.testId), testSym);

    final List<IEnvironmentSymbol> targetEnv = getProfileEnvironment(
        OneCapabilityOneTestXML.varNames, OneCapabilityOneTestXML.varValues);
    final List<ISymbol> envs = capSym
        .getSymbolsInContext(IEnvironmentSymbol.CONTEXT);
    assertEquals(targetEnv, envs);

    final List<ArgumentSymbol> targetArgs = new ArrayList<ArgumentSymbol>();
    for (int idx = 0; idx < OneCapabilityOneTestXML.argIds.length; idx++) {
      final ArgumentSymbol argSym = new ArgumentSymbol(
          OneCapabilityOneTestXML.argIds[idx],
          OneCapabilityOneTestXML.argClasses[idx]);
      targetArgs.add(argSym);
    }
    final List<ISymbol> args = testSym
        .getSymbolsInContext(IArgumentSymbol.CONTEXT);
    assertEquals(targetArgs, args);
  }

  public void testEnvironmentVariableExists() {
    final ISymbolMap symbolTable = context.mock(ISymbolMap.class);
    final CapabilitySymbol capSym = new CapabilitySymbol(
        EnvironmentTestXML.capabilityId, EnvironmentTestXML.capabilityDesc,
        EnvironmentTestXML.capabilityClass);

    context.checking(new Expectations() {
      {
        one(symbolTable).put(EnvironmentTestXML.capabilityId, capSym);
      }
    });

    final byte[] xmlBytes = EnvironmentTestXML.existsProfileXML.getBytes();
    final ByteArrayInputStream stream = new ByteArrayInputStream(xmlBytes);
    try {
      parser.setSymbolTable(symbolTable).parse(stream);
    } catch (ParserException ex) {
      assertTrue(false);
    }

    context.assertIsSatisfied();
  }

  public void testEnvironmentVariablesNotExist() {
    final ISymbolMap symbolTable = context.mock(ISymbolMap.class);
    final CapabilitySymbol capSym = new CapabilitySymbol(
        EnvironmentTestXML.capabilityId, EnvironmentTestXML.capabilityDesc,
        EnvironmentTestXML.capabilityClass);

    context.checking(new Expectations() {
      {
        one(symbolTable).put(EnvironmentTestXML.capabilityId, capSym);
      }
    });

    final byte[] xmlBytes = EnvironmentTestXML.noExistsProfileXML.getBytes();
    final ByteArrayInputStream stream = new ByteArrayInputStream(xmlBytes);
    try {
      parser.setSymbolTable(symbolTable).parse(stream);
    } catch (ParserException ex) {
      if (ex instanceof ProfileParserException) {
        final Collection<String> missing = ((ProfileParserException )ex)
            .getMissingEnvironment();
        assertTrue(missing.size() == EnvironmentTestXML.varNames.length);
        final Collection<String> expectedMissing = new HashSet<String>();
        for (int idx = 0; idx < missing.size(); idx++) {
          expectedMissing.add(EnvironmentTestXML.varNames[idx]);
        }
        assertTrue(expectedMissing.containsAll(missing));
        context.assertIsSatisfied();
        return;
      }
    }

    assertTrue(false);
  }

  public void testInvalidXML() {
    final ISymbolMap symbolTable = context.mock(ISymbolMap.class);
    final byte[] xmlBytes = InvalidXML.xml.getBytes();
    final ByteArrayInputStream stream = new ByteArrayInputStream(xmlBytes);
    try {
      parser.setSymbolTable(symbolTable).parse(stream);
    } catch (ParserException ex) {
      assertTrue(true);
      return;
    }

    assertTrue(false);
  }
}
