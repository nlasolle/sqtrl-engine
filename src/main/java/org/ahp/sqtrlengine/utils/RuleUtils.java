package org.ahp.sqtrlengine.utils;

import java.util.ArrayList;
import java.util.List;

import org.ahp.sqtrlengine.model.Prefix;
import org.ahp.sqtrlengine.model.TransformationRule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RuleUtils {

	private static final Logger logger = LogManager.getLogger(RuleUtils.class);
	
	public static void replacePrefixes(List<TransformationRule> rules, List<Prefix> prefixes) {
		for(TransformationRule rule : rules) {
			for(Prefix prefix : prefixes) {
				rule.setContext(replaceAllPrefixes(rule.getContext(), prefix));
				rule.setLeft(replaceAllPrefixes(rule.getLeft(), prefix));
				rule.setRight(replaceAllPrefixes(rule.getRight(), prefix));

				List<String> exceptions = new ArrayList<>();		
				
				for(String exception : rule.getExceptions()) {
					exceptions.add(replaceAllPrefixes(exception, prefix));
				}

				rule.setExceptions(exceptions);
			}
		}
	}

	/**
	 * Replace all prefix occurrences with full IRI value within a string
	 * @param field 
	 * @param prefix 
	 * @return
	 */
	private static String replaceAllPrefixes(String field, Prefix prefix) {
		while(field.contains(prefix.getAbbreviation())) {
			int prefixIndex = field.indexOf(prefix.getAbbreviation());
			int propertyIndex = prefixIndex + prefix.getAbbreviation().length();
			int nextSpaceIndex = field.indexOf(" ", propertyIndex);
			
			if (nextSpaceIndex == -1) {
				nextSpaceIndex = field.length(); 
			}
			
			field = field.substring(0, 
					prefixIndex)
					+ "<"
					+ prefix.getNamespace()
					+ field.substring(propertyIndex, nextSpaceIndex)
					+ ">"
					+ field.substring(nextSpaceIndex);
		}
		
		return field;
	}
	
/**
 * Indicate if a transformation rule is valid. Perform all the checks before returning the value.
 * List all the errors in the log
 * @param rule the transformation rule object
 * @return true if the rule is valid
 */
	public static boolean isRuleValid(TransformationRule rule, List<Prefix> prefixes) {
		boolean isValid = true;
		
		logger.info("Starting validity check of rule " + rule);
		
		if(rule == null) {
			return false;
		}
		
		//Mandatory fields check
		if(rule.getIri() == null || rule.getIri().isBlank()) {
			logger.error("IRI is a mandatory attribute and must be unique");
			isValid = false;
		}
		
		if(rule.getLabel() == null || rule.getLabel().isBlank()) {
			logger.error("Label is a mandatory attribute and must be unique");
			isValid = false;
		}
		
		if(rule.getCost() < 0) {
			logger.error("Cost must be positive or null");
			isValid = false;
		}
		
		//Check graph patterns validity	
		if(!QueryUtils.isGraphPatternValid(rule.getContext(), prefixes)) {
			logger.error("Context field is not a valid SPARQL graph pattern");
			isValid = false;
		}
		
		if(!QueryUtils.isGraphPatternValid(rule.getLeft(), prefixes)) {
			logger.error("Left field is not a valid SPARQL graph pattern");
			isValid = false;
		}
		
		if(!QueryUtils.isGraphPatternValid(rule.getRight(), prefixes)) {
			logger.error("Right field is not a valid SPARQL graph pattern");
			isValid = false;
		}

		for(int i = 0; i < rule.getExceptions().size(); i++) {
			if(!QueryUtils.isGraphPatternValid(rule.getExceptions().get(i), prefixes)) {
				logger.error("The exception number {i+1} is not a valid SPARQL graph pattern");
				isValid = false;
			}
		}

		return isValid;
	}
	
	/**
	 * Remove unwanted space characters for graph pattern fields (newline, series of white space or tabs)
	 * @param rule the transformation rule to format
	 * @return a transformation rule with formatted String fields
	 */
	public static TransformationRule formatRule(TransformationRule rule) {

		TransformationRule formattedRule = new TransformationRule();
		formattedRule.setIri(rule.getIri());
		formattedRule.setCost(rule.getCost());
		formattedRule.setLabel(rule.getLabel().trim().replaceAll("\\R+", " ").replaceAll("(\\s)+", " "));
		formattedRule.setContext(rule.getContext().trim().replaceAll("\\R+", " ").replaceAll("(\\s)+", " "));
		formattedRule.setLeft(rule.getLeft().trim().replaceAll("\\R+", " ").replaceAll("(\\s)+", " "));
		formattedRule.setRight(rule.getRight().trim().replaceAll("\\R+", " ").replaceAll("(\\s)+", " "));
		formattedRule.setExplanation(rule.getExplanation().trim().replaceAll("\\R+", " ").replaceAll("(\\s)+", " "));

		List<String> exceptions = new ArrayList<>();

		for(String exception : rule.getExceptions()) {
			exceptions.add(exception.trim().replaceAll("\\R+", " ").replaceAll("(\\s)+", " "));
		}

		formattedRule.setExceptions(exceptions);
		return formattedRule;
	}
}
