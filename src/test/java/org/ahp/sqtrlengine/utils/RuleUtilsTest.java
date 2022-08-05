package org.ahp.sqtrlengine.utils;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.ahp.sqtrlengine.model.Prefix;
import org.ahp.sqtrlengine.model.TransformationRule;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test the class dedicated to the manipulation of transformation rules
 * @author Nicolas Lasolle
 *
 */
class RuleUtilsTest {

	/**
	 * List of prefixes which are used for testing purposes
	 * @return a list of prefixes
	 */
	private static List<Prefix> getPrefixes(){
		List<Prefix> prefixes = new ArrayList<Prefix>();
		
		prefixes.add(new Prefix("http://www.w3.org/2000/01/rdf-schema#", "rdfs"));
		
		return prefixes;
	}
	
	private static Stream<Arguments> getRules() {
		
		TransformationRule emptyRule = new TransformationRule();
		
		//The following rule is a completly valid rule which is used to construct several invalid rules
		TransformationRule validRule  = new TransformationRule("http://checkvalidity/rule/1", "Rule Label");
		
		validRule.setCost(5);
		validRule.setContext("?C rdfs:subClassOf ?D");
		validRule.setLeft("?x ?p ?C");
		validRule.setRight("?x ?p ?D");
		
		List<String> exceptions = new ArrayList<>();
		exceptions.add("?C rdfs:subClassOf ?X . ?X rdfs:subClassOf ?D "
				+ "FILTER(?C !=?X && ?X != ?D)");
		exceptions.add("FILTER(?C = ?D)");

		validRule.setExceptions(exceptions);
		validRule.setExplanation("Generalize ?C into ?D");
		
		RuleUtils.formatRule(validRule);
		
		TransformationRule badIriRule = new TransformationRule(validRule);
		badIriRule.setIri("");
		
		TransformationRule badLabelRule = new TransformationRule(validRule);
		badLabelRule.setLabel("");
		
		TransformationRule nullCostRule = new TransformationRule(validRule);
		nullCostRule.setCost(0);
		
		TransformationRule badCostRule = new TransformationRule(validRule);
		badCostRule.setCost(-1);
		
		TransformationRule badContextRule = new TransformationRule(validRule);
		badContextRule.setContext("");
		
		TransformationRule badLeftRule = new TransformationRule(validRule);
		badLeftRule.setLeft("");
		
		TransformationRule badRightRule = new TransformationRule(validRule);
		badRightRule.setRight("");
		
		TransformationRule badExceptionRule = new TransformationRule(validRule);
		badExceptionRule.getExceptions().set(0, "");
		badExceptionRule.getExceptions().set(1, null);
		
		TransformationRule emptyExceptionsListRule = new TransformationRule(validRule);
		emptyExceptionsListRule.setExceptions(new ArrayList<String>());
		
		TransformationRule emptyExplanationRule = new TransformationRule(validRule);
		emptyExplanationRule.setExplanation("");
		
		return Stream.of(
				Arguments.of(null, false),
				Arguments.of(emptyRule, false),
				Arguments.of(validRule, true),
				Arguments.of(badIriRule, false),
				Arguments.of(badLabelRule, false),
				Arguments.of(nullCostRule, true),
				Arguments.of(badCostRule, false),
				Arguments.of(badContextRule, false),
				Arguments.of(badLeftRule, false),
				Arguments.of(badRightRule, false),
				Arguments.of(badExceptionRule, false),
				Arguments.of(emptyExceptionsListRule, true),
				Arguments.of(emptyExplanationRule, true)
				);
	}



	@ParameterizedTest
	@MethodSource("getRules")
	void testIsRuleValid(TransformationRule rule, boolean valid) {
		assertEquals(valid, RuleUtils.isRuleValid(rule, getPrefixes()));
	}

}
