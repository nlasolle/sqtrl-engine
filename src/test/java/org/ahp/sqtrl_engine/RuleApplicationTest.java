package org.ahp.sqtrl_engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import org.ahp.sqtrlengine.exception.InvalidRuleFileException;
import org.ahp.sqtrlengine.model.RuleApplication;
import org.ahp.sqtrlengine.model.TransformationRule;
import org.ahp.sqtrlengine.service.RuleApplyer;
import org.ahp.sqtrlengine.service.XMLRuleParser;
import org.ahp.sqtrlengine.utils.QueryUtils;
import org.apache.jena.ext.com.google.common.io.Resources;
import org.apache.jena.query.Query;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;


public class RuleApplicationTest {

	static List<TransformationRule> rules;
	private static final String RULE_FILE = "validRules.xml";
	private static final String SPARQL_ENDPOINT = "http://localhost:3030/hp";
	
	@BeforeAll
	static void prepareTransformationRules() throws FileNotFoundException, IOException, InvalidRuleFileException {
		File validFile = new File(RuleApplicationTest.class.getClassLoader().getResource(RULE_FILE).getFile());

		XMLRuleParser parser = new XMLRuleParser();

		rules = parser.parseRuleFile(validFile);
	}
	
	
	@ParameterizedTest
	@ValueSource(strings = {"http://sqtrl-rules/generic/1"})
	void testContextBindingRetrieval(String ruleIri) {
		
		TransformationRule rule = rules.stream()
				.filter(r -> r.getIri().equals(ruleIri))
				.findAny()
				.orElse(null);


		RuleApplyer ruleApplyer = new RuleApplyer();
		List<HashMap<String, String>> bindings = ruleApplyer.getContextBindings(rule, SPARQL_ENDPOINT);
		System.out.println(bindings);

	}
	
	@ParameterizedTest
	@ValueSource(strings = {"http://sqtrl-rules/generic/1"})
	void testLeftBind(String ruleIri) throws IOException {

		TransformationRule rule = rules.stream()
				.filter(r -> r.getIri().equals(ruleIri))
				.findAny()
				.orElse(null);


		RuleApplyer ruleApplyer = new RuleApplyer();
		List<HashMap<String, String>> bindings = null;
		//ruleApplyer.bindLeftMember(rule, bindings );
	}
	
	@ParameterizedTest
	@CsvSource({"http://sqtrl-rules/generic/1, queries/ObjGenQuery.rq"})
	void testFullRuleApplication(String ruleIri, String queryFile) throws IOException {

		TransformationRule rule = rules.stream()
				.filter(r -> r.getIri().equals(ruleIri))
				.findAny()
				.orElse(null);

		String queryAsString = Resources.toString(getClass().getClassLoader().getResource(queryFile), StandardCharsets.UTF_8);
		Query query = QueryUtils.parseQuery(queryAsString);
		
		RuleApplyer ruleApplyer = new RuleApplyer();
		List<HashMap<String, String>> contextBindings = ruleApplyer.getContextBindings(rule, SPARQL_ENDPOINT);
		
		List<String> boundLeft = ruleApplyer.bindLeftMember(rule, contextBindings);

		
		List<RuleApplication> ruleApplications = ruleApplyer.getRuleApplication(rule, query, contextBindings);
		System.out.println("--- Rule applications ---");
		System.out.println(ruleApplications);
		/*for(RuleApplication application : ruleApplications) {
			System.out.println(application.getRuleIri());
			System.out.println(application.getContextBinding());
			System.out.println(application.getLeftBinding());
		}*/
		
	}
}
