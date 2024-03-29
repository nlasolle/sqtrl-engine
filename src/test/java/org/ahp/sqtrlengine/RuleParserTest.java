package org.ahp.sqtrlengine;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.ahp.sqtrlengine.exception.InvalidFileTypeException;
import org.ahp.sqtrlengine.exception.InvalidRuleFileException;
import org.ahp.sqtrlengine.exception.RuleException;
import org.ahp.sqtrlengine.model.Prefix;
import org.ahp.sqtrlengine.model.TransformationRule;
import org.ahp.sqtrlengine.service.JSONRuleParser;
import org.ahp.sqtrlengine.service.XMLRuleParser;
import org.ahp.sqtrlengine.utils.RuleUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the class dedicated to the parsing of transformation rule files
 * @author Nicolas Lasolle
 *
 */
class RuleParserTest {

	private static final Logger logger = LogManager.getLogger(RuleParserTest.class);
	private static List<Prefix> prefixes;
	
	@Test
	void testNullFile() {
		XMLRuleParser parser = new XMLRuleParser(null);
		Assertions.assertThrows(NullPointerException.class, () -> {
			parser.parseRuleFile();
		});
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"", "nonExistingFileName.xml"})
	void testXMLFileNotFound(String fileName) {

		File nonExistingFile = new File(fileName);
		XMLRuleParser parser = new XMLRuleParser(nonExistingFile);
		Assertions.assertThrows(FileNotFoundException.class, () -> {
			parser.isRuleFileValid();
		});
	}

	@ParameterizedTest
	@ValueSource(strings = {"rules.txt", "rules.json", ""})
	void testInvalidFileType(String fileName) {
		File invalidFile = new File(getClass().getClassLoader().getResource(fileName).getFile());
		XMLRuleParser parser = new XMLRuleParser(invalidFile);
		
		Assertions.assertThrows(InvalidFileTypeException.class, () -> {
			parser.isRuleFileValid();
		});
	}

	@ParameterizedTest
	@ValueSource(strings = {"invalidRules.xml"})
	void testInvalidRuleFile(String fileName) throws FileNotFoundException, InvalidFileTypeException {
		File invalidFile = new File(getClass().getClassLoader().getResource(fileName).getFile());

		XMLRuleParser parser = new XMLRuleParser(invalidFile);

		Assertions.assertFalse(parser.isRuleFileValid());
	}

	@ParameterizedTest
	@ValueSource(strings = {"validRules.xml"})
	void testValidRuleFile(String fileName) throws FileNotFoundException, InvalidFileTypeException {
		File validFile = new File(getClass().getClassLoader().getResource(fileName).getFile());

		XMLRuleParser parser = new XMLRuleParser(validFile);

		Assertions.assertTrue(parser.isRuleFileValid());

	}
	
	
	@ParameterizedTest
	@CsvSource({"validRules.xml, 20"})
	void testXMLRuleParsing(String fileName, int numberOfRules) throws IOException, InvalidRuleFileException, RuleException {
		File validFile = new File(getClass().getClassLoader().getResource(fileName).getFile());

		XMLRuleParser parser = new XMLRuleParser(validFile);
		parser.loadXMLDocument();
		List<TransformationRule> rules = parser.parseRuleFile();
		prefixes = parser.parsePrefixes();
		RuleUtils.replacePrefixes(rules, prefixes);
		logger.info(rules);
		Assertions.assertNotNull(rules);
		Assertions.assertEquals(numberOfRules, rules.size());

	}
	
	@ParameterizedTest
	@CsvSource({"validRules.json, 20"})
	void testJSONRuleParsing(String fileName, int numberOfRules) throws IOException, InvalidRuleFileException {
		File validFile = new File(getClass().getClassLoader().getResource(fileName).getFile());

		JSONRuleParser parser = new JSONRuleParser(validFile);
		
		List<TransformationRule> rules = parser.parseRuleFile();
		prefixes = parser.parsePrefixes();
		logger.info(rules);
		//RuleUtils.replacePrefixes(rules, prefixes);
		Assertions.assertNotNull(rules);
		Assertions.assertEquals(numberOfRules, rules.size());

	}
}
