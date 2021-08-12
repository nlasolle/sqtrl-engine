package org.ahp.sqtrl_engine;


import java.io.File;
import java.io.FileNotFoundException;

import org.ahp.sqtrlengine.exception.InvalidFileTypeException;
import org.ahp.sqtrlengine.service.XMLRuleParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the class dedicated to the parsing of transformation rule file
 * @author Nicolas Lasolle
 *
 */
class RuleParserTest {

	@Test
	void testNullFile() {

		XMLRuleParser parser = new XMLRuleParser();

		Assertions.assertThrows(NullPointerException.class, () -> {
			parser.isRuleFileValid(null);
		});
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"", "nonExistingFileName.xml"})
	void testXMLFileNotFound(String fileName) {

		File nonExistingFile = new File(fileName);

		XMLRuleParser parser = new XMLRuleParser();

		Assertions.assertThrows(FileNotFoundException.class, () -> {
			parser.isRuleFileValid(nonExistingFile);
		});
	}

	@ParameterizedTest
	@ValueSource(strings = {"rules.txt", "rules.json", "rules", ""})
	void testInvalidFileType(String fileName) {
		File invalidFile = new File(getClass().getClassLoader().getResource(fileName).getFile());

		XMLRuleParser parser = new XMLRuleParser();

		Assertions.assertThrows(InvalidFileTypeException.class, () -> {
			parser.isRuleFileValid(invalidFile);
		});
	}

	@ParameterizedTest
	@ValueSource(strings = {"invalidRules.xml"})
	void testInvalidRuleFile(String fileName) throws FileNotFoundException, InvalidFileTypeException {
		File invalidFile = new File(getClass().getClassLoader().getResource(fileName).getFile());

		XMLRuleParser parser = new XMLRuleParser();

		Assertions.assertFalse(parser.isRuleFileValid(invalidFile));
	}

	@ParameterizedTest
	@ValueSource(strings = {"validRules.xml"})
	void testValidRuleFile(String fileName) throws FileNotFoundException, InvalidFileTypeException {
		File validFile = new File(getClass().getClassLoader().getResource(fileName).getFile());

		XMLRuleParser parser = new XMLRuleParser();

		Assertions.assertTrue(parser.isRuleFileValid(validFile));

	}

}
