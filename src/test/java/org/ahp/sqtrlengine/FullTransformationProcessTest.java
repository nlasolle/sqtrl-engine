package org.ahp.sqtrlengine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.ahp.sqtrlengine.exception.InvalidRuleFileException;
import org.ahp.sqtrlengine.model.Prefix;
import org.ahp.sqtrlengine.model.TransformationRule;
import org.ahp.sqtrlengine.service.CostBasedTransformationProcess;
import org.ahp.sqtrlengine.service.XMLRuleParser;
import org.ahp.sqtrlengine.utils.RuleUtils;
import org.apache.jena.ext.com.google.common.io.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


class FullTransformationProcessTest {
	private static final Logger logger = LogManager.getLogger(FullTransformationProcessTest.class);
	
	private static List<TransformationRule> rules;
	private static List<Prefix> prefixes;
	private static final String RULE_FILE = "validRules.xml";
	private static final String SPARQL_ENDPOINT = "http://localhost:3030/full_ahp_corpus";

	@BeforeAll
	static void prepareTransformationRules() throws FileNotFoundException, IOException, InvalidRuleFileException {
		File validFile = new File(FullTransformationProcessTest.class.getClassLoader().getResource(RULE_FILE).getFile());
		
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
			
		CostBasedTransformationProcess transformationProcess = new CostBasedTransformationProcess(6, rules, query, SPARQL_ENDPOINT, true);
		
		transformationProcess.sortRules();
		
		while(transformationProcess.getNextNode() != null) {
			logger.info("Size of nodes {}", transformationProcess.getNodes().size());
		}
	}

}
