package org.ahp.sqtrlengine.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.ahp.sqtrlengine.exception.InvalidFileTypeException;
import org.ahp.sqtrlengine.exception.InvalidRuleFileException;
import org.ahp.sqtrlengine.model.TransformationRule;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.SAXException;

/**
 * Class dedicated to the validation and the parsing
 * of a transformation rule file under XML syntax
 * @author Nicolas Lasolle
 *
 */
public class XMLRuleParser implements RuleParser {

	final static String SCHEMA_FILE = "src/main/resources/transformationRule.xsd";

	@Override
	public boolean isRuleFileValid(File ruleFile) throws FileNotFoundException, InvalidFileTypeException {
		if(!ruleFile.exists()) {
			throw new FileNotFoundException();
		}

		String ext = FilenameUtils.getExtension(ruleFile.getPath());

		if(ext == null || !ext.equals("xml")) {
			throw new InvalidFileTypeException(ruleFile.getPath(), "xml");
		}

		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		Schema schema;


		try {
			schema = factory.newSchema(new File(SCHEMA_FILE));
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(ruleFile));
		} catch (SAXException | IOException e) {
			e.printStackTrace();
			return false;
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
				rule.setIri(ruleElement.getAttributeValue("iri"));
				rule.setLabel(ruleElement.getAttributeValue("label"));
				if(ruleElement.getChildren("context") != null) rule.setContext(ruleElement.getChild("context").getText());
				rule.setLeft(ruleElement.getChild("left").getText());
				rule.setRight(ruleElement.getChild("right").getText());
				if(ruleElement.getChildren("cost") != null) rule.setCost(Float.parseFloat(ruleElement.getChild("cost").getText()));	
				if(ruleElement.getChildren("explanation") != null) rule.setExplanation(ruleElement.getChild("explanation").getText());	


				if(ruleElement.getChildren("exception") != null){
					List<String> exceptions = new ArrayList<String>();

					for(Element element : ruleElement.getChildren("exception")) {
						exceptions.add(element.getText());
					}

					rule.setExceptions(exceptions );
				}

			} else {
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
