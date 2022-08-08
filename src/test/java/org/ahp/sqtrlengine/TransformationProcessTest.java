package org.ahp.sqtrlengine;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.ahp.sqtrlengine.exception.InvalidRuleFileException;
import org.ahp.sqtrlengine.model.Prefix;
import org.ahp.sqtrlengine.model.RuleApplication;
import org.ahp.sqtrlengine.model.TransformationRule;
import org.ahp.sqtrlengine.service.CostBasedTransformationProcess;
import org.ahp.sqtrlengine.service.RuleApplyer;
import org.ahp.sqtrlengine.service.XMLRuleParser;
import org.ahp.sqtrlengine.utils.QueryUtils;
import org.ahp.sqtrlengine.utils.RuleUtils;
import org.apache.jena.ext.com.google.common.io.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


class TransformationProcessTest {
	private static final Logger logger = LogManager.getLogger(TransformationProcessTest.class);
	
	private static List<TransformationRule> rules;
	private static List<Prefix> prefixes;
	private static final String RULE_FILE = "validRules.xml";
	private static final String SPARQL_ENDPOINT = "http://localhost:3030/full_ahp_corpus";

	@BeforeAll
	static void prepareTransformationRules() throws FileNotFoundException, IOException, InvalidRuleFileException {
		File validFile = new File(TransformationProcessTest.class.getClassLoader().getResource(RULE_FILE).getFile());

		XMLRuleParser parser = new XMLRuleParser(validFile);
		parser.loadXMLDocument();
		rules = parser.parseRuleFile();
		prefixes = parser.parsePrefixes();
		RuleUtils.replacePrefixes(rules, prefixes);
	}

	@ParameterizedTest
	@ValueSource(strings = {"queries/fullCaseQuery.rq"})
	void testCostBasedTransformationProcess(String queryFile) throws IOException {
		
		String query = Resources.toString(getClass().getClassLoader().getResource(queryFile), StandardCharsets.UTF_8);
		
		
		CostBasedTransformationProcess transformationProcess = new CostBasedTransformationProcess(12, rules, query, SPARQL_ENDPOINT, true);
		
		transformationProcess.sortRules();

		while(transformationProcess.getNextNode() != null) {

		}
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"queries/bugTestQuery.rq"})
	void testRuleApplyer(String queryFile) throws IOException {
		
		String query = Resources.toString(getClass().getClassLoader().getResource(queryFile), StandardCharsets.UTF_8);
		String ruleIRI = "http://sqtrl-rules/ahpo/1";
		TransformationRule rule = rules.stream()
				.filter(r -> ruleIRI.equals(r.getIri()))
				.findAny()
				.orElse(null);
		RuleApplyer applyer = new RuleApplyer(SPARQL_ENDPOINT);
		
		List<RuleApplication> applications = applyer.getRuleApplications(QueryUtils.parseQuery(query), rule, SPARQL_ENDPOINT);
		
		logger.info("Query {}", QueryUtils.parseQuery(query));
		logger.info("Rule {}", rule);
		logger.info("SPARQL ENDPOINT {}", SPARQL_ENDPOINT);
		for(RuleApplication a: applications) {
			logger.info(a);
		}
	}

}
