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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.splandroid.tr.parsers.IArgumentSymbol;
import org.splandroid.tr.parsers.IEnvironmentSymbol;
import org.splandroid.tr.parsers.IParser;
import org.splandroid.tr.parsers.ISymbol;
import org.splandroid.tr.parsers.ISymbolMap;
import org.splandroid.tr.parsers.ITestSymbol;
import org.splandroid.tr.parsers.ParserException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

interface IMissingEnvironment {
  public Collection<String> getMissingEnviroment();
}

class ProfileHandler extends DefaultHandler implements IMissingEnvironment {
  private static Logger logger = Logger.getLogger(ProfileHandler.class);

  private final ISymbolMap symbols;

  private Locator locator = null;

  private CapabilitySymbol currentCapability;
  private TestSymbol currentTest;
  private ArgumentSymbol currentArgument;
  private EnvironmentSymbol currentEnvironment;

  private HashSet<String> missingEnvironment;

  private boolean inCapability;
  private boolean inTest;
  private boolean inArguments;
  private boolean inEnvironment;

  public ProfileHandler(ISymbolMap symbolTable) {
    super();
    symbols = symbolTable;
  }

  private String getLocation() {
    return String.format("at line %d and column %d", locator.getLineNumber(),
        locator.getColumnNumber());
  }

  private void handleCapabilityStart(Attributes attrs) throws SAXException {
    final String id = attrs.getValue(ProfileParserConstants.ATTR_CAPABILITY_ID);
    final String desc = attrs
        .getValue(ProfileParserConstants.ATTR_CAPABILITY_DESC);
    final String className = attrs
        .getValue(ProfileParserConstants.ATTR_CAPABILITY_CLASS);
    if (id == null || desc == null || className == null || id.length() < 1
        || desc.length() < 1 || className.length() < 1) {
      throw new SAXParseException(String.format(
          "Capability not does fully specify its "
              + "attributes: id = [%s], description = [%s], class = [%s]", id,
          desc, className), locator);
    }
    logger.debug(String.format(
        "Building capability symbol with ID [%s] and description [%s]", id,
        desc));
    currentCapability = new CapabilitySymbol(id, desc, className);
  }

  private void handleCapabilityEnd() {
    symbols.put(currentCapability.getId(), currentCapability);
    currentCapability = null;
  }

  private void handleTestStart(Attributes attrs) throws SAXException {
    final String id = attrs.getValue(ProfileParserConstants.ATTR_TEST_ID);
    if (id == null || id.length() < 1) {
      throw new SAXParseException(String.format(
          "Test does not fully specify its attribute [%s]", id), locator);
    }
    currentTest = new TestSymbol(id);
  }

  private void handleTestEnd() throws SAXException {
    try {
      currentCapability.addSymbol(currentTest, ITestSymbol.CONTEXT);
    } catch (ParserException ex) {
      throw new SAXParseException(String.format(
          "Capability with ID [%s] contains duplicate test with ID [%s]",
          currentCapability.getId(), currentTest.getId()), locator, ex);
    }
    currentTest = null;
  }

  private void handleArgumentStart(String tagName, Attributes attrs)
      throws SAXException {
    if (!inArguments) {
      throw new SAXParseException(String.format("Unexpected opening [%s] tag",
          tagName), locator);
    }

    final String id = attrs.getValue(ProfileParserConstants.ATTR_ARGUMENT_ID);
    final String klassName = attrs
        .getValue(ProfileParserConstants.ATTR_ARGUMENT_KIND);
    if (id == null || klassName == null || id.length() < 1
        || klassName.length() < 1) {
      throw new SAXParseException(String.format(
          "Argument does not fully specifiy it "
              + "attributes: id = [%s], class = [%s]", id, klassName), locator);
    }
    logger.debug(String.format("Defining argument [%s] of kind [%s]", id,
        klassName));
    currentArgument = new ArgumentSymbol(id, klassName);
  }

  private void handleArgumentEnd() throws SAXException {
    try {
      ISymbol symbolForArg = null;
      if (inTest) {
        symbolForArg = currentTest;
      } else {
        symbolForArg = currentCapability;
      }
      symbolForArg.addSymbol(currentArgument, IArgumentSymbol.CONTEXT);
    } catch (ParserException ex) {
      throw new SAXParseException(String.format(
          "Test with ID [%s] contains duplicate argument with ID [%s]",
          currentTest.getId(), currentArgument.getId()), locator, ex);
    }
    currentArgument = null;
  }

  private void handleEnvironmentStart(String tagName) throws SAXException {
    if (!inCapability || inTest) {
      throw new SAXParseException(String.format("Unexpected opening [%s] tag",
          tagName), locator);
    }
  }

  private void handleVariableStart(String tagName, Attributes attrs)
      throws SAXException {
    if (!inEnvironment) {
      throw new SAXParseException(String.format("Unexpected opening [%s] tag",
          tagName), locator);
    }
    final String name = attrs
        .getValue(ProfileParserConstants.ATTR_VARIABLE_NAME);
    final String value = attrs
        .getValue(ProfileParserConstants.ATTR_VARIABLE_VALUE);
    if (name == null || value == null || name.length() < 1) {
      throw new SAXParseException(String.format(
          "Environment variable does not fully specify "
              + "its attributes: name = [%s], value = [%s]", name, value),
          locator);
    }
    currentEnvironment = new EnvironmentSymbol(name, value);
  }

  private void handleVariableEnd() throws SAXException {
    try {
      currentCapability.addSymbol(currentEnvironment,
          IEnvironmentSymbol.CONTEXT);
    } catch (ParserException ex) {
      throw new SAXParseException(String.format(
          "Test with ID [%s] contains duplicate environment "
              + "variable with ID [%s]", currentTest.getId(),
          currentArgument.getId()), locator, ex);
    }
    currentEnvironment = null;
  }

  private void handleExistsStart(String tagName, Attributes attrs)
      throws SAXException {
    if (!inEnvironment) {
      throw new SAXParseException(String.format("Unexpected opening [%s] tag",
          tagName), locator);
    }
    final String envVarName = attrs
        .getValue(ProfileParserConstants.ATTR_VARIABLE_NAME);
    if (envVarName == null) {
      throw new SAXParseException(String.format(
          "Missing attribute [%s] in [%s] tag",
          ProfileParserConstants.ATTR_VARIABLE_NAME, tagName), locator);
    }
    if (System.getenv(envVarName) == null) {
      if (!missingEnvironment.contains(envVarName)) {
        missingEnvironment.add(envVarName);
      }
      logger.warn(String.format("Expected environment variable [%s] not found "
          + "for capability [%s]", envVarName, currentCapability.getId()));
    } else {
      logger.debug(String.format(
          "Expected environment variable [%s] exists for " + "capability [%s]",
          envVarName, currentCapability.getId()));
    }
  }

  @Override
  public void startDocument() {
    logger.debug("Start parsing of profile");
    currentCapability = null;
    currentTest = null;
    missingEnvironment = new HashSet<String>();
    inCapability = false;
    inTest = false;
    inArguments = false;
    inEnvironment = false;
  }

  @Override
  public void endDocument() {
    logger.debug("Finished parsing of profile");
  }

  @Override
  public void startElement(String uri, String localName, String qName,
      Attributes attrs) throws SAXException {
    logger.debug(String.format("Start tag [%s]", qName));
    if (ProfileParserConstants.TAG_CAPABILITY.equals(qName)) {
      inCapability = true;
      handleCapabilityStart(attrs);
    } else if (ProfileParserConstants.TAG_TEST.equals(qName)) {
      inTest = true;
      handleTestStart(attrs);
    } else if (ProfileParserConstants.TAG_ARGUMENTS.equals(qName)) {
      inArguments = true;
    } else if (ProfileParserConstants.TAG_ARGUMENT.equals(qName)) {
      handleArgumentStart(qName, attrs);
    } else if (ProfileParserConstants.TAG_ENVIRONMENT.equals(qName)) {
      inEnvironment = true;
      handleEnvironmentStart(qName);
    } else if (ProfileParserConstants.TAG_VARIABLE.equals(qName)) {
      handleVariableStart(qName, attrs);
    } else if (ProfileParserConstants.TAG_EXISTS.equals(qName)) {
      handleExistsStart(qName, attrs);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName)
      throws SAXException {
    logger.debug(String.format("End tag [%s]", qName));
    if (ProfileParserConstants.TAG_CAPABILITY.equals(qName)) {
      handleCapabilityEnd();
      inCapability = false;
    } else if (ProfileParserConstants.TAG_TEST.equals(qName)) {
      handleTestEnd();
      inTest = false;
    } else if (ProfileParserConstants.TAG_ARGUMENTS.equals(qName)) {
      inArguments = false;
    } else if (ProfileParserConstants.TAG_ARGUMENT.equals(qName)) {
      handleArgumentEnd();
    } else if (ProfileParserConstants.TAG_ENVIRONMENT.equals(qName)) {
      inEnvironment = false;
    } else if (ProfileParserConstants.TAG_VARIABLE.equals(qName)) {
      handleVariableEnd();
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

  public Collection<String> getMissingEnviroment() {
    return missingEnvironment;
  }
}

public class ProfileParser implements IParser {
  private final SAXParser parser;

  private ISymbolMap symbolTable;

  public ProfileParser() throws ProfileParserException {
    try {
      this.parser = SAXParserFactory.newInstance().newSAXParser();
    } catch (SAXException saxEx) {
      throw new ProfileParserException(saxEx);
    } catch (ParserConfigurationException configEx) {
      throw new ProfileParserException(configEx);
    }
  }

  public IParser setSymbolTable(ISymbolMap symbolTab) {
    symbolTable = symbolTab;
    return this;
  }

  public void parse(InputStream stream) throws ProfileParserException {
    final ProfileHandler handler = new ProfileHandler(symbolTable);
    try {
      parser.parse(stream, handler);
    } catch (SAXException saxEx) {
      throw new ProfileParserException(saxEx);
    } catch (IOException ioEx) {
      throw new ProfileParserException(ioEx);
    }

    final Collection<String> missingEnvironment = handler
        .getMissingEnviroment();
    if (missingEnvironment.size() > 0) {
      throw new ProfileParserException(missingEnvironment);
    }
  }
}
