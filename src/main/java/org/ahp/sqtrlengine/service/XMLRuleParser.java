package org.ahp.sqtrlengine.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ahp.sqtrlengine.exception.InvalidFileTypeException;
import org.ahp.sqtrlengine.exception.InvalidRuleFileException;
import org.ahp.sqtrlengine.exception.RuleException;
import org.ahp.sqtrlengine.model.TransformationRule;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * Class dedicated to the validation and the parsing
 * of a transformation rule file under XML syntax
 * @author Nicolas Lasolle
 *
 */
public class XMLRuleParser implements RuleParser {


	@Override
	public boolean isRuleFileValid(File ruleFile) throws FileNotFoundException, InvalidFileTypeException {
		if(!ruleFile.exists()) {
			throw new FileNotFoundException();
		}

		if(!FilenameUtils.getExtension(ruleFile.getPath()).equals("xml")) {
			throw new InvalidFileTypeException(ruleFile.getPath(), "xml");
		}


		return true;
	}

	@Override
	public List<TransformationRule> parseRuleFile(File ruleFile) throws IOException, FileNotFoundException, InvalidRuleFileException {

		List<TransformationRule> rules = new ArrayList<TransformationRule>(); 

		TransformationRule rule;//To store each new rule

		//Load the document
		SAXBuilder saxBuilder = new SAXBuilder();
		Document document;
		
		try {
			document = saxBuilder.build(ruleFile);
		} catch (JDOMException e) {
			throw new InvalidRuleFileException("File " + ruleFile.getName() + "is invalid.");
		}

		//Get all the elements
		Element classElement = document.getRootElement();

		List<Element> elements = classElement.getChild("rules").getChildren();

		//Get all the rules
		for (int i = 0; i < elements.size(); i++) {  
			rule = new TransformationRule();
			Element ruleElement = elements.get(i);
			if(ruleElement.getName().equals("rule")){
				rule.setType("classic");
				rule.setIri(ruleElement.getAttributeValue("name"));
				rule.setContext(ruleElement.getChild("context").getText());
				rule.setLeft(ruleElement.getChild("left").getText());
				rule.setRight(ruleElement.getChild("right").getText());
				rule.setCost(Float.parseFloat(ruleElement.getChild("cost").getText()));
				rule.setExplanation(ruleElement.getChild("explanation").getText());	

			}else {
				rule.setType("special");
				rule.setIri(ruleElement.getAttributeValue("name"));
				//rule.setLower(Integer.parseInt(ruleElement.getChild("lower").getText()));
				//rule.setHigher(Integer.parseInt(ruleElement.getChild("higher").getText()));
				rule.setCost(Float.parseFloat(ruleElement.getChild("cost").getText()));
				rule.setExplanation(ruleElement.getChild("explanation").getText());	
			}

			for(TransformationRule tempRule:rules){
				if(tempRule.getIri().equals(rule.getIri())){
					//LOGGER.INFO(A rule with iri + " + tempRule.getIri() " has already been defined. Skipping this rule");
				}
			}

			rules.add(rule);
		}
		return rules;
	}


}
