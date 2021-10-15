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
import org.ahp.sqtrlengine.service.CostBasedTransformationProcess;
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


public class TransformationProcessTest {
	private static final Logger logger = LogManager.getLogger(TransformationProcessTest.class);
	
	private static List<TransformationRule> rules;
	private static List<Prefix> prefixes;
	private static final String RULE_FILE = "validRules.xml";
	private static final String SPARQL_ENDPOINT = "http://localhost:3030/HP_0510";

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
	@ValueSource(strings = {"queries/generic1.rq"})
	void testCostBasedTransformationProcess(String queryFile) throws IOException {
		
		String queryAsString = Resources.toString(getClass().getClassLoader().getResource(queryFile), StandardCharsets.UTF_8);
		Query query = QueryUtils.parseQuery(queryAsString);
		
		CostBasedTransformationProcess transformationProcess = new CostBasedTransformationProcess(2,
				rules, query, SPARQL_ENDPOINT);
		
		transformationProcess.sortRules();
		int i = 0;
		while(transformationProcess.getNextNode() && i<5) {
			i++;
			logger.info("Tour " + i);
		}
	}

}