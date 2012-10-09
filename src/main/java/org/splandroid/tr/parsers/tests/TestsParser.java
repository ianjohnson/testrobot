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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.splandroid.tr.TRException;
import org.splandroid.tr.parsers.IArgumentSymbol;
import org.splandroid.tr.parsers.ICapabilitySymbol;
import org.splandroid.tr.parsers.IEnvironmentSymbol;
import org.splandroid.tr.parsers.IParser;
import org.splandroid.tr.parsers.ISymbol;
import org.splandroid.tr.parsers.ISymbolMap;
import org.splandroid.tr.parsers.ITestArguments;
import org.splandroid.tr.parsers.ITestCaseDescriptor;
import org.splandroid.tr.parsers.ITestDescriptor;
import org.splandroid.tr.parsers.ITestSymbol;
import org.splandroid.tr.parsers.ParserException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

class TestsHandler extends DefaultHandler {
  private static Logger logger = Logger.getLogger(TestsHandler.class);

  private static final String substitutionPattern = "\\w+";
  private static final String argumentAnchor = "%";
  private static final String argumentKind = "Argument";
  private static final String environmentAnchor = "\\$";
  private static final String environmentKind = "Environment variable";

  private final ISymbolMap symbols;
  private final List<ITestCaseDescriptor> testCases;
  private final TokenSubstitution<Map<String, Object>> argumentsSubstitution;
  private final TokenSubstitution<Map<String, String>> environmentSubstitution;

  private Locator locator = null;

  // Parser state
  private String currentTestCaseId = null;
  private String currentTestCaseDesc = null;
  private String currentTestId = null;
  private List<ITestDescriptor> currentTests = null;
  private ITestArguments currentSetupInfo = null;
  private ITestArguments currentTestArguments = null;
  private Map<String, String> currentTestEnvironment = null;
  private ISymbol currentCapability = null;
  private ISymbol currentTestSymbol = null;

  public TestsHandler(ISymbolMap symbolTable,
      List<ITestCaseDescriptor> testCases) {
    super();
    this.symbols = symbolTable;
    this.testCases = testCases;
    this.argumentsSubstitution = new TokenSubstitution<Map<String, Object>>(
        substitutionPattern, argumentAnchor, argumentKind);
    this.environmentSubstitution = new TokenSubstitution<Map<String, String>>(
        substitutionPattern, environmentAnchor, environmentKind);
  }

  private static Class<?> loadClass(String klassName) throws TRException {
    Class<?> klass;
    try {
      klass = Class.forName(klassName);
    } catch (ClassNotFoundException notFoundEx) {
      throw new TRException(String.format("Symbol class [%s] not found: %s",
          klassName, notFoundEx.getMessage()));
    }

    return klass;
  }

  private String getLocation() {
    return String.format("at line %d and column %d", locator.getLineNumber(),
        locator.getColumnNumber());
  }

  /**
   * Load arguments into a java.util.Map for a given symbol.
   * 
   * @param aSymbol
   *          - The symbol to load arguments for
   * @param arguments
   *          - A map to store the argument ID/value pairs
   * @param tagName
   *          - Current tag name
   * @param attrs
   *          - Attributes for the current tag
   * @param locator
   *          - SAX parser location
   * @throws SAXException
   */
  private void loadArguments(ISymbol aSymbol, ITestArguments arguments,
      String tagName, Attributes attrs, Locator locator) throws SAXException {
    final List<ISymbol> symbolArguments = aSymbol
        .getSymbolsInContext(IArgumentSymbol.CONTEXT);

    final int expectedArgCount = symbolArguments.size();
    final int tagArgCount = attrs.getLength();
    if (tagArgCount != expectedArgCount) {
      throw new SAXParseException(String.format(
          "Argument count mismatch in tag [%s], "
              + "expected %d and got %d arguments, "
              + "at line %d and column %d", tagName, expectedArgCount,
          tagArgCount, locator.getLineNumber(), locator.getColumnNumber()),
          locator);
    }

    argumentsSubstitution.clearMaps();
    argumentsSubstitution.addMap(arguments);

    for (ISymbol symbol : symbolArguments) {
      final IArgumentSymbol argSymbol = (IArgumentSymbol )symbol;
      final String argId = argSymbol.getId();
      final String klassName = argSymbol.getKindClass();

      logger.debug(String.format("Reading argument [%s] of kind [%s]", argId,
          klassName));

      Class<?> klass;
      try {
        klass = loadClass(klassName);
      } catch (TRException ex) {
        throw new SAXParseException(String.format(
            "Error loading class [%s] for attribute " + "[%s] in tag [%s]",
            klassName, argId, tagName), locator, ex);
      }

      // Get argument's value and do value substitution
      String argValue = attrs.getValue(argId);
      if (argValue == null) {
        throw new SAXParseException(String.format(
            "Argument [%s] not set at line %d and column %d", argId,
            locator.getLineNumber(), locator.getColumnNumber()), locator);
      }
      try {
        argValue = argumentsSubstitution.substitute(argValue);
      } catch (TRException ex) {
        throw new SAXParseException(String.format(
            "Failed to substitute argument value at "
                + "attribute [%s] in tag [%s]", argId, tagName), locator, ex);
      }
      if (argValue == null) {
        throw new SAXParseException(String.format(
            "Attribute [%s] in tag [%s] not found", argId, tagName), locator);
      }

      // Build argument object and initialise with its value
      Constructor<?> klassConstructor;
      try {
        klassConstructor = klass.getConstructor(String.class);
      } catch (NoSuchMethodException noSuchMethodEx) {
        throw new SAXParseException(String.format(
            "Expected constructor for class [%s] does "
                + "not exist for attribute [%s] in tag [%s]", klassName, argId,
            tagName), locator, noSuchMethodEx);
      } catch (SecurityException secEx) {
        throw new SAXParseException(String.format(
            "Constructor for class [%s] does "
                + "not allow execution for attribute [%s] in tag [%s]",
            klassName, argId, tagName), locator, secEx);
      }
      Object argValueObj;
      try {
        argValueObj = klassConstructor.newInstance(argValue);
      } catch (Exception ex) {
        throw new SAXParseException(String.format(
            "Could not construct [%s] object "
                + "for attribute [%s] in tag [%s] with value [%s]", klassName,
            argId, tagName, argValue), locator, ex);
      }

      logger.debug(String.format("Constructed argument [%s] of kind [%s]: %s",
          argId, klassName, argValueObj.toString()));

      arguments.put(argId, argValueObj);
    }
  }

  private void handleTestsStart() {
    logger.debug("Processing tests...");
  }

  /**
   * Testcases are containers for a testcase identifier and description. The
   * test capability, embedded in the tag, shall inherit the identifier and
   * description from this tag.
   * 
   * @param attrs
   *          - &lt;testcase&gt; XML tag attributes
   */
  private void handleTestCaseStart(String tagName, Attributes attrs)
      throws SAXException {
    if (currentTestCaseId != null || currentTestCaseDesc != null) {
      throw new SAXParseException(String.format("Unexpected opening [%s] tag",
          tagName), locator);
    }

    currentTestCaseId = attrs.getValue(TestsParserConstants.ATTR_TESTSUITE_ID);
    currentTestCaseDesc = attrs
        .getValue(TestsParserConstants.ATTR_TESTSUITE_DESC);

    if (currentTestCaseId == null || currentTestCaseDesc == null
        || currentTestCaseId.length() < 1 || currentTestCaseDesc.length() < 1) {
      throw new SAXParseException(String.format(
          "Attributes for tag [%s] not fully specified", tagName), locator);
    }

    logger.debug(String.format("Processing test case [%s]...",
        currentTestCaseId));

    currentTests = new ArrayList<ITestDescriptor>();
    currentCapability = null;
    currentTestSymbol = null;
  }

  /**
   * Reset after a test case end
   */
  private void handleTestCaseEnd(String tagName) throws SAXException {
    if (currentTestCaseId == null || currentTestCaseDesc == null) {
      throw new SAXParseException(String.format("Unexpected closing [%s] tag",
          tagName), locator);
    }
    currentTestCaseId = null;
    currentTestCaseDesc = null;
  }

  /**
   * Reads a capability tag and validates attributes, defined from the
   * profile.xml. Then, constructs the appropriate object for the attribute and
   * adds it to the test informational map.
   * 
   * Also, does argument value substitution; any argument containing %<argument
   * id>% will have the arguments value replaced. If the argument does not exist
   * at substitution time the operation will fail.
   * 
   * @param tagName
   * @param attrs
   * @throws SAXException
   */
  private void handleCapabilityStart(String tagName, Attributes attrs)
      throws SAXException {
    if (currentTestCaseId == null || currentTestCaseDesc == null
        || currentSetupInfo != null) {
      throw new SAXParseException(String.format("Unexpected opening [%s] tag",
          tagName), locator);
    }

    logger.debug(String.format("Processing capability [%s]...", tagName));

    if (currentCapability != null) {
      throw new SAXParseException(String.format(
          "Mulitple capabilities found in testcase [%s]. "
              + "Only one capability is supported.", currentTestCaseId),
          locator);
    }

    assert (symbols.containsKey(tagName));
    currentCapability = symbols.get(tagName);
    currentSetupInfo = new TestArguments(tagName);
    loadArguments(currentCapability, currentSetupInfo, tagName, attrs, locator);

    // Inherit the environment set in the profile
    currentTestEnvironment = new HashMap<String, String>();
    final List<ISymbol> environmentSymbols = currentCapability
        .getSymbolsInContext(IEnvironmentSymbol.CONTEXT);
    for (ISymbol symbol : environmentSymbols) {
      final IEnvironmentSymbol envSymbol = (IEnvironmentSymbol )symbol;
      currentTestEnvironment.put(envSymbol.getId(), envSymbol.getValue());
    }
  }

  /**
   * Initialises the environment variable substitution. The first map to query
   * is the environment inherited from the profile. If the variable is not here
   * the process's environment is queried.
   * 
   * @param environment
   */
  private void initialiseEnvironmentSubstitution(Map<String, String> environment) {
    environmentSubstitution.clearMaps();
    environmentSubstitution.addMap(environment).addMap(System.getenv());
  }

  /**
   * Substitute any environment variables in a string argument from the set of
   * arguments.
   * 
   * @param args
   *          - ITestArgument object
   * @param environment
   *          - An environment Map<String, String>
   */
  private void environmentSubstitution(ITestArguments args,
      Map<String, String> environment) throws TRException {
    initialiseEnvironmentSubstitution(environment);
    for (String argId : args.keySet()) {
      final Object argValue = args.get(argId);
      if (argValue instanceof String) {
        String newValue = environmentSubstitution.substitute((String )argValue);
        args.put(argId, newValue);
      }
    }
  }

  /**
   * Reset the capability settings; the set-up information and the current
   * capability.
   */
  private void handleCapabilityEnd(String tagName) throws SAXException {
    if (currentCapability == null || currentSetupInfo == null
        || currentTestCaseId == null || currentTestCaseDesc == null
        || currentTests == null || currentTestEnvironment == null) {
      throw new SAXParseException(String.format("Unexpected closing [%s] tag",
          tagName), locator);
    }

    if (currentTests.size() > 0) {
      // Do environment variable substitution
      try {
        environmentSubstitution(currentSetupInfo, currentTestEnvironment);
        for (ITestDescriptor testDesc : currentTests) {
          final ITestArguments testArgs = testDesc.getArguments();
          environmentSubstitution(testArgs, currentTestEnvironment);
        }
      } catch (TRException ex) {
        throw new SAXParseException(
            "Failed to substitute environment varaibles", locator, ex);
      }

      final String className = ((ICapabilitySymbol )currentCapability)
          .getKindClass();
      assert className != null;
      final ITestCaseDescriptor testCase = new TestCaseDescriptor(
          currentTestCaseId, currentTestCaseDesc, className, currentTests,
          currentSetupInfo, currentTestEnvironment);
      testCases.add(testCase);
    } else {
      logger.warn(String.format("Test case [%s] contains no tests",
          currentTestCaseId));
    }

    currentCapability = null;
    currentSetupInfo = null;
    currentTests = null;
  }

  /**
   * Processes the test tag
   * 
   * @param tagName
   * @param attrs
   * @throws SAXException
   */
  private void handleTestStart(String tagName, Attributes attrs)
      throws SAXException {
    if (currentTestId != null || currentTestArguments != null
        || currentCapability == null) {
      throw new SAXParseException(String.format("Unexpected opening [%s] tag",
          tagName), locator);
    }

    final String capId = currentCapability.getId();
    logger.debug(String.format("Starting test [%s] in capability [%s]",
        tagName, capId));

    currentTestSymbol = currentCapability.getSymbol(tagName,
        ITestSymbol.CONTEXT);
    if (currentTestSymbol == null) {
      throw new SAXParseException(String.format("Tag [%s] is not defined",
          tagName), locator);
    }

    currentTestId = currentTestSymbol.getId();

    // Build test argument and environment maps
    currentTestArguments = new TestArguments(currentTestId);

    loadArguments(currentTestSymbol, currentTestArguments, tagName, attrs,
        locator);
  }

  /**
   * Adds the current test to the list of current test descriptors.
   */
  private void handleTestEnd(String tagName) throws SAXException {
    if (currentTestId == null || currentTestArguments == null) {
      throw new SAXParseException(String.format("Unexpected closing [%s] tag",
          tagName), locator);
    }

    logger.debug(String.format("Ending test [%s]", currentTestId));

    final TestDescriptor testDescriptor = new TestDescriptor(currentTestId,
        currentTestArguments);

    currentTests.add(testDescriptor);

    currentTestId = null;
    currentTestArguments = null;
  }

  /**
   * Handle <environment> tag
   * 
   * @throws SAXException
   */
  private void handleEnvironmentStart(String tagName) throws SAXException {
    logger.debug("Starting environment");
    if (currentTestEnvironment == null || currentCapability == null
        || currentTestId != null) {
      throw new SAXParseException(
          String.format("Unexpected [%s] tag", tagName), locator);
    }
  }

  /**
   * Add an environment variable to the environment map.
   * 
   * @param attrs
   *          - Currrent tag's attributes
   * @throws SAXException
   */
  private void handleVariableStart(String tagName, Attributes attrs)
      throws SAXException {
    logger.debug("Starting variable");
    if (currentTestEnvironment == null) {
      throw new SAXParseException(
          String.format("Unexpected [%s] tag", tagName), locator);
    }

    final String varName = attrs
        .getValue(TestsParserConstants.ATTR_VARIABLE_NAME);
    final String varValue = attrs
        .getValue(TestsParserConstants.ATTR_VARIABLE_VALUE);
    if (varName == null || varValue == null) {
      throw new SAXParseException(String.format("Badly formed %s tag",
          TestsParserConstants.TAG_VARIABLE), locator);
    }

    // Do any substitution
    initialiseEnvironmentSubstitution(currentTestEnvironment);
    String newVarValue = null;
    try {
      newVarValue = environmentSubstitution.substitute(varValue);
    } catch (TRException ex) {
      throw new SAXParseException("Environment variable substitution failed",
          locator, ex);
    }
    currentTestEnvironment.put(varName, newVarValue);

    logger.debug(String.format("Read variable: name = [%s], value = [%s]",
        varName, varValue));
  }

  /**
   * Handle opening tags that are defined in the runtime profile.
   * 
   * @param tagName
   *          - Current tag name
   * @param attrs
   *          - Attributes of current tag
   * @throws SAXException
   */
  private void handleSymbolStart(String tagName, Attributes attrs)
      throws SAXException {
    if (symbols.containsKey(tagName)) {
      /*
       * The current tag is a capability Get the capability symbol from the
       * symbol table.
       */
      handleCapabilityStart(tagName, attrs);
    } else {
      handleTestStart(tagName, attrs);
    }
  }

  /**
   * Handle closing tags that are defined in the runtime profile.
   * 
   * @param tagName
   *          - Current tag name
   */
  private void handleSymbolEnd(String tagName) throws SAXException {
    if (symbols.containsKey(tagName)) {
      handleCapabilityEnd(tagName);
    } else {
      handleTestEnd(tagName);
    }
  }

  @Override
  public void startElement(String uri, String localName, String qName,
      Attributes attrs) throws SAXException {
    if (TestsParserConstants.TAG_TESTS.equals(qName)) {
      handleTestsStart();
    } else if (TestsParserConstants.TAG_TESTCASE.equals(qName)) {
      handleTestCaseStart(qName, attrs);
    } else if (TestsParserConstants.TAG_ENVIRONMENT.equals(qName)) {
      handleEnvironmentStart(qName);
    } else if (TestsParserConstants.TAG_VARIABLE.equals(qName)) {
      handleVariableStart(qName, attrs);
    } else {
      handleSymbolStart(qName, attrs);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName)
      throws SAXException {
    if (TestsParserConstants.TAG_TESTS.equals(qName)) {
      logger.debug(String.format("Ending %s", qName));
    } else if (TestsParserConstants.TAG_TESTCASE.equals(qName)) {
      handleTestCaseEnd(qName);
    } else if (TestsParserConstants.TAG_ENVIRONMENT.equals(qName)) {
      logger.debug(String.format("Ending %s", qName));
    } else if (TestsParserConstants.TAG_VARIABLE.equals(qName)) {
      logger.debug(String.format("Ending %s", qName));
    } else {
      handleSymbolEnd(qName);
    }
  }

  @Override
  public void warning(SAXParseException saxEx) {
    logger.warn(
        String.format("Parse warning %s: %s", this.getLocation(),
            saxEx.getMessage()), saxEx);
  }

  @Override
  public void error(SAXParseException saxEx) {
    logger.error(
        String.format("Parse error %s: %s", this.getLocation(),
            saxEx.getMessage()), saxEx);
  }

  @Override
  public void fatalError(SAXParseException saxEx) {
    logger.fatal(
        String.format("Parse error %s: %s", this.getLocation(),
            saxEx.getMessage()), saxEx);
  }

  @Override
  public void setDocumentLocator(Locator loc) {
    locator = loc;
  }
}

public class TestsParser implements IParser {
  private final SAXParser parser;

  private ISymbolMap symbols = null;
  private List<ITestCaseDescriptor> testCases;

  public TestsParser(List<ITestCaseDescriptor> testCases) throws TRException {
    assert testCases != null;
    try {
      this.parser = SAXParserFactory.newInstance().newSAXParser();
    } catch (SAXException saxEx) {
      throw new TRException(saxEx);
    } catch (ParserConfigurationException configEx) {
      throw new TRException(configEx);
    }
    this.testCases = testCases;
  }

  public void parse(InputStream stream) throws ParserException {
    assert symbols != null : "Set symbol table before parsing";
    testCases.clear();
    try {
      parser.parse(stream, new TestsHandler(symbols, testCases));
    } catch (SAXException saxEx) {
      throw new ParserException(saxEx);
    } catch (IOException ioEx) {
      throw new ParserException(ioEx);
    }
  }

  public IParser setSymbolTable(ISymbolMap symbolTable) {
    assert symbolTable != null : "Invalid symbol table";
    symbols = symbolTable;
    return this;
  }
}
