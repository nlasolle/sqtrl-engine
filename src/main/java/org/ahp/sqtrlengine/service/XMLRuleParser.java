package org.ahp.sqtrlengine.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.ahp.sqtrlengine.exception.InvalidFileTypeException;
import org.ahp.sqtrlengine.exception.InvalidRuleFileException;
import org.ahp.sqtrlengine.exception.QueryException;
import org.ahp.sqtrlengine.exception.RuleException;
import org.ahp.sqtrlengine.model.FilteringTransformationRule;
import org.ahp.sqtrlengine.model.Prefix;
import org.ahp.sqtrlengine.model.TransformationRule;
import org.ahp.sqtrlengine.utils.QueryUtils;
import org.ahp.sqtrlengine.utils.RuleUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * Class dedicated to the validation and the parsing
 * of a transformation rule file under XML syntax
 * @author Nicolas Lasolle
 *
 */
public class XMLRuleParser implements RuleParser {

	private static final String SCHEMA_FILE = "src/main/resources/transformationRule.xsd";
	private static final Logger logger = LogManager.getLogger(XMLRuleParser.class);

	private File ruleFile;
	private Document document;

	public XMLRuleParser(File ruleFile) {
		this.setRuleFile(ruleFile);
	}

	@Override
	public boolean isRuleFileValid() throws FileNotFoundException, InvalidFileTypeException {
		if(!ruleFile.exists()) {
			throw new FileNotFoundException();
		}

		String ext = FilenameUtils.getExtension(ruleFile.getPath());

		if(ext == null || !ext.equals("xml")) {
			throw new InvalidFileTypeException(ruleFile.getPath(), "xml");
		}

		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// to be compliant, completely disable DOCTYPE declaration:
		try {
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		} catch (SAXNotRecognizedException e1) {
			e1.printStackTrace();
		} catch (SAXNotSupportedException e1) {
			e1.printStackTrace();
		}

		Schema schema;

		try {
			schema = factory.newSchema(new File(SCHEMA_FILE));
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(ruleFile));
		} catch (SAXException | IOException e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Load the XML document
	 * @throws InvalidRuleFileException 
	 */
	public void loadXMLDocument() throws InvalidRuleFileException {
		SAXBuilder saxBuilder = new SAXBuilder();

		try {
			document = saxBuilder.build(ruleFile);
		} catch (JDOMException | IOException e) {
			throw new InvalidRuleFileException("File " + ruleFile.getName() + "is invalid.");
		}
	}

	@Override
	public List<TransformationRule> parseRuleFile() throws InvalidRuleFileException, RuleException {

		List<TransformationRule> rules = new ArrayList<>(); 
		List<String> existingIri = new ArrayList<>();

		TransformationRule rule;//To store each new rule

		//Get all the elements
		Element classElement = document.getRootElement();

		List<Element> elements = classElement.getChild("rules").getChildren();

		//Get all the rules
		for (int i = 0; i < elements.size(); i++) {  
			rule = new TransformationRule();
			Element ruleElement = elements.get(i);

			//First check, iri must be unique. If not, we skip the rule
			if(rule.getIri() != null && existingIri.contains(rule.getIri())) {
				logger.info("A rule with iri {} has already been defined. Skipping this rule.", rule.getIri());
				break;
			}


			rule.setIri(ruleElement.getAttributeValue("iri"));
			rule.setLabel(ruleElement.getAttributeValue("label"));
			if(ruleElement.getChildren("context") != null) rule.setContext(ruleElement.getChild("context").getText());
			rule.setLeft(ruleElement.getChild("left").getText());
			rule.setRight(ruleElement.getChild("right").getText());
			if(ruleElement.getChildren("cost") != null) rule.setCost(Float.parseFloat(ruleElement.getChild("cost").getText()));	
			if(ruleElement.getChildren("explanation") != null) rule.setExplanation(ruleElement.getChild("explanation").getText());	


			if(ruleElement.getChildren("exception") != null){
				List<String> exceptions = new ArrayList<>();

				for(Element element : ruleElement.getChildren("exception")) {
					exceptions.add(element.getText());
				}

				rule.setExceptions(exceptions );
			}

			if(ruleElement.getName().equals("filteringRule")) {
				FilteringTransformationRule filteringRule = (FilteringTransformationRule) rule;
				try {
					filteringRule.setLeftFilter(QueryUtils.parseExpressions(ruleElement.getAttributeValue("leftFilter")));
					filteringRule.setRightFilter(QueryUtils.parseExpressions(ruleElement.getAttributeValue("rightFilter")));
				} catch (QueryException e) {
					throw new RuleException(e.getMessage());
				}
				
			}

			rules.add(RuleUtils.formatRule(rule));
			existingIri.add(rule.getIri());
		}

		return rules;
	}


	@Override
	public List<Prefix> parsePrefixes() {
		List<Prefix> prefixes = new ArrayList<>();
		Prefix prefix;

		//Get all the elements
		Element classElement = document.getRootElement();

		List<Element> docPrefixes = classElement.getChild("prefixes").getChildren();

		//Get all the prefixes
		for(int i=0;i<docPrefixes.size();i++){
			prefix = new Prefix();
			prefix.setAbbreviation(docPrefixes.get(i).getAttributeValue("label"));
			prefix.setNamespace(docPrefixes.get(i).getAttributeValue("iri"));
			prefixes.add(prefix);
		}

		return prefixes;
	}

	public File getRuleFile() {
		return ruleFile;
	}

	public void setRuleFile(File ruleFile) {
		this.ruleFile = ruleFile;
	}

}
