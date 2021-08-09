package org.ahp.sqtrl_engine;


import java.io.File;
import java.io.FileNotFoundException;

import org.ahp.sqtrlengine.exception.InvalidFileTypeException;
import org.ahp.sqtrlengine.service.XMLRuleParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test the class dedicated to the parsing of transformation rule file
 * @author Nicolas Lasolle
 *
 */
class RuleParserTest {

	@Test
	void testXMLFileNotFound() {

		File nonExistingFile = new File("nonExistingFileName.xml");

		XMLRuleParser parser = new XMLRuleParser();

		Assertions.assertThrows(FileNotFoundException.class, () -> {
			parser.isRuleFileValid(nonExistingFile);
		});
	}

	@Test
	void testInvalidFileType() {
		String invalidFileName = "InvalidFile.txt";
		File invalidFile = new File(getClass().getClassLoader().getResource(invalidFileName).getFile());

		XMLRuleParser parser = new XMLRuleParser();

		Assertions.assertThrows(InvalidFileTypeException.class, () -> {
			parser.isRuleFileValid(invalidFile);
		});
	}

	@Test
	void testInvalidRuleFile() throws FileNotFoundException, InvalidFileTypeException {
		String invalidFileName = "invalidRules.xml";

		File invalidFile = new File(getClass().getClassLoader().getResource(invalidFileName).getFile());

		XMLRuleParser parser = new XMLRuleParser();

		Assertions.assertFalse(parser.isRuleFileValid(invalidFile));
	}

	@Test
	void testValidRuleFile() throws FileNotFoundException, InvalidFileTypeException {
		String validFileName = "validRules.xml";

		File validFile = new File(getClass().getClassLoader().getResource(validFileName).getFile());

		XMLRuleParser parser = new XMLRuleParser();

		Assertions.assertTrue(parser.isRuleFileValid(validFile));

	}

}
