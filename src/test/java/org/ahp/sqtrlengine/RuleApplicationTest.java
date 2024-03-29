package org.ahp.sqtrlengine;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.ahp.sqtrlengine.exception.InvalidRuleFileException;
import org.ahp.sqtrlengine.exception.QueryException;
import org.ahp.sqtrlengine.exception.RuleException;
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


class RuleApplicationTest {
	private static final Logger logger = LogManager.getLogger(RuleApplicationTest.class);
	private static List<TransformationRule> rules;
	private static List<Prefix> prefixes;
	private static final String RULE_FILE = "validRules.xml";
	private static final String SPARQL_ENDPOINT = "http://localhost:3030/full_ahp_corpus";

	@BeforeAll
	static void prepareTransformationRules() throws FileNotFoundException, IOException, InvalidRuleFileException, RuleException {
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
		
		try {
			ruleApplyer.getContextBindings(rule, SPARQL_ENDPOINT);
		} catch (QueryException e) {
			e.printStackTrace();
		}

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
		Query query;

		try {
			query = QueryUtils.parseQuery(queryAsString);
		} catch (QueryException e) {
			assert(false);
			return;
		}

		RuleApplyer ruleApplyer = new RuleApplyer(SPARQL_ENDPOINT);
		List<RuleApplication> ruleApplications = ruleApplyer.getRuleApplications(query, rule, SPARQL_ENDPOINT);

		if(ruleApplications.isEmpty()) {
			logger.info("No application for rule {}  and for query file {}", rule.getIri(), queryFile);
		} else {
			logger.info("{} applications for rule {}  and for query file {}", ruleApplications.size(), rule.getIri(), queryFile);
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
		Query query;

		try {
			query = QueryUtils.parseQuery(queryAsString);
		} catch (QueryException e) {
			assert(false);
			return;
		}

		RuleApplyer ruleApplyer = new RuleApplyer(SPARQL_ENDPOINT);
		List<RuleApplication> ruleApplications = ruleApplyer.getRuleApplications(query, rule, SPARQL_ENDPOINT);

		if(ruleApplications.isEmpty()) {
			logger.info("No application for rule {}  and for query file {}", rule.getIri(), queryFile);
		} else {
			logger.info("{} applications for rule {}  and for query file {}", ruleApplications.size(), rule.getIri(), queryFile);
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
		Query query;
		try {
			query = QueryUtils.parseQuery(queryAsString);
		} catch (QueryException e) {
			assert(false);
			return;
		}

		RuleApplyer ruleApplyer = new RuleApplyer(SPARQL_ENDPOINT);
		List<RuleApplication> ruleApplications = ruleApplyer.getRuleApplications(query, rule, SPARQL_ENDPOINT);

		if(ruleApplications.isEmpty()) {
			logger.info("No application for rule {}  and for query file {}", rule.getIri(), queryFile);
		} else {
			logger.info("{} applications for rule {}  and for query file {}", ruleApplications.size(), rule.getIri(), queryFile);
			logger.info(ruleApplications);
		}
	}

	@ParameterizedTest
	@CsvSource({
		"http://sqtrl-rules/generic/1, queries/ahpo3.rq",
		"http://sqtrl-rules/generic/5, queries/ahpo1.rq",
		"http://sqtrl-rules/generic/3, queries/ahpo1.rq",
		"http://sqtrl-rules/generic/4, queries/ahpo6.rq",
	})
	void testExplicationGeneration(String ruleIri, String queryFile) throws IOException {
		TransformationRule rule = rules.stream()
				.filter(r -> r.getIri().equals(ruleIri))
				.findAny()
				.orElse(null);

		String queryAsString = Resources.toString(getClass().getClassLoader().getResource(queryFile), StandardCharsets.UTF_8);
		Query query;

		try {
			query = QueryUtils.parseQuery(queryAsString);
		} catch (QueryException e) {
			assert(false);
			return;
		}

		RuleApplyer ruleApplyer = new RuleApplyer(SPARQL_ENDPOINT);
		List<RuleApplication> ruleApplications = ruleApplyer.getRuleApplications(query, rule, SPARQL_ENDPOINT);


		assertNotNull(ruleApplications);
		assertNotEquals(0, ruleApplications.size());

		for(RuleApplication application : ruleApplications) {
			logger.warn("Explication: {}", application.getExplanation());
			assertFalse(application.getExplanation().isBlank());

		}

	}

}
