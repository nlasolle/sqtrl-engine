package org.ahp.sqtrlengine.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.ahp.sqtrlengine.exception.InvalidFileTypeException;
import org.ahp.sqtrlengine.exception.InvalidRuleFileException;
import org.ahp.sqtrlengine.model.TransformationRule;

public interface RuleParser {

	//Check the validity of rules
	public boolean isRuleFileValid(File ruleFile) throws FileNotFoundException, InvalidFileTypeException, InvalidRuleFileException;
	
	//Parse the transformation rules
	public List<TransformationRule> parseRuleFile(File ruleFile) throws FileNotFoundException, IOException, InvalidRuleFileException;
	
	//public List<Prefix> parsePrefixes(File ruleFile);

}
