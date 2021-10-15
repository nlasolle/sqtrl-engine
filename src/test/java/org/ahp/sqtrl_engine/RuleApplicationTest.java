package org.ahp.sqtrl_engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import org.ahp.sqtrlengine.exception.InvalidRuleFileException;
import org.ahp.sqtrlengine.model.Prefix;
import org.ahp.sqtrlengine.model.RuleApplication;
import org.ahp.sqtrlengine.model.TransformationRule;
import org.ahp.sqtrlengine.service.RuleApplyer;
import org.ahp.sqtrlengine.service.XMLRuleParser;
import org.ahp.sqtrlengine.utils.QueryUtils;
import org.ahp.sqtrlengine.utils.RuleUtils;
import org.apache.jena.ext.com.google.common.io.Resources;
import org.apache.jena.query.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;


public class RuleApplicationTest {
	private static final Logger logger = LogManager.getLogger(RuleApplicationTest.class);
	private static List<TransformationRule> rules;
	private static List<Prefix> prefixes;
	private static final String RULE_FILE = "validRules.xml";
	private static final String SPARQL_ENDPOINT = "http://localhost:3030/HP_0510";

	@BeforeAll
	static void prepareTransformationRules() throws FileNotFoundException, IOException, InvalidRuleFileException {
		File validFile = new File(RuleApplicationTest.class.getClassLoader().getResource(RULE_FILE).getFile());

		XMLRuleParser parser = new XMLRuleParser(validFile);
		parser.loadXMLDocument();
		rules = parser.parseRuleFile();
		prefixes = parser.parsePrefixes();
		RuleUtils.replacePrefixes(rules, prefixes);
	}

	@ParameterizedTest
	@ValueSource(strings = {"http://sqtrl-rules/generic/1"})
	void testContextBindingRetrieval(String ruleIri) {

		TransformationRule rule = rules.stream()
				.filter(r -> r.getIri().equals(ruleIri))
				.findAny()
				.orElse(null);


		RuleApplyer ruleApplyer = new RuleApplyer(SPARQL_ENDPOINT);
		List<HashMap<String, String>> bindings = ruleApplyer.getContextBindings(rule, SPARQL_ENDPOINT);
		logger.debug(bindings);

	}


	@ParameterizedTest
	@CsvSource({
		"http://sqtrl-rules/generic/1, queries/generic1.rq", 
		"http://sqtrl-rules/generic/2, queries/generic2.rq",
		"http://sqtrl-rules/generic/3, queries/generic3.rq",
		"http://sqtrl-rules/generic/4, queries/generic4.rq",
		"http://sqtrl-rules/generic/5, queries/generic5.rq"
	})
	void testGenericRuleApplication(String ruleIri, String queryFile) throws IOException {
		TransformationRule rule = rules.stream()
				.filter(r -> r.getIri().equals(ruleIri))
				.findAny()
				.orElse(null);

		String queryAsString = Resources.toString(getClass().getClassLoader().getResource(queryFile), StandardCharsets.UTF_8);
		Query query = QueryUtils.parseQuery(queryAsString);

		RuleApplyer ruleApplyer = new RuleApplyer(SPARQL_ENDPOINT);
		List<RuleApplication> ruleApplications = ruleApplyer.getRuleApplications(query, rule, SPARQL_ENDPOINT);

		if(ruleApplications.isEmpty()) {
			logger.info("No application for rule " + rule.getIri() + " and for query file " + queryFile);
		} else {
			logger.info(ruleApplications.size() + " applications for rule " + rule.getIri() + " and for query file " + queryFile);
			logger.info(ruleApplications);
		}


	}

	@ParameterizedTest
	@CsvSource({
		"http://sqtrl-rules/ahpo/2, queries/ahpo2.rq",
		"http://sqtrl-rules/ahpo/3, queries/ahpo3.rq",
		"http://sqtrl-rules/ahpo/4, queries/ahpo4.rq",
		"http://sqtrl-rules/ahpo/5, queries/ahpo5.rq",
		"http://sqtrl-rules/ahpo/6, queries/ahpo6.rq"
	})
	void testAHPRuleApplication(String ruleIri, String queryFile) throws IOException {
		TransformationRule rule = rules.stream()
				.filter(r -> r.getIri().equals(ruleIri))
				.findAny()
				.orElse(null);

		String queryAsString = Resources.toString(getClass().getClassLoader().getResource(queryFile), StandardCharsets.UTF_8);
		Query query = QueryUtils.parseQuery(queryAsString);

		RuleApplyer ruleApplyer = new RuleApplyer(SPARQL_ENDPOINT);
		List<RuleApplication> ruleApplications = ruleApplyer.getRuleApplications(query, rule, SPARQL_ENDPOINT);

		if(ruleApplications.isEmpty()) {
			logger.info("No application for rule " + rule.getIri() + " and for query file " + queryFile);
		} else {
			logger.info(ruleApplications.size() + " applications for rule " + rule.getIri() + " and for query file " + queryFile);
			logger.info(ruleApplications);
		}
	}
	
	@ParameterizedTest
	@CsvSource({
		"http://sqtrl-rules/ahpo/1, queries/generic1.rq",
	})
	void testNonApplicableRule(String ruleIri, String queryFile) throws IOException {
		TransformationRule rule = rules.stream()
				.filter(r -> r.getIri().equals(ruleIri))
				.findAny()
				.orElse(null);

		String queryAsString = Resources.toString(getClass().getClassLoader().getResource(queryFile), StandardCharsets.UTF_8);
		Query query = QueryUtils.parseQuery(queryAsString);

		RuleApplyer ruleApplyer = new RuleApplyer(SPARQL_ENDPOINT);
		List<RuleApplication> ruleApplications = ruleApplyer.getRuleApplications(query, rule, SPARQL_ENDPOINT);

		if(ruleApplications.isEmpty()) {
			logger.info("No application for rule " + rule.getIri() + " and for query file " + queryFile);
		} else {
			logger.info(ruleApplications.size() + " applications for rule " + rule.getIri() + " and for query file " + queryFile);
			logger.info(ruleApplications);
		}
	}

}
