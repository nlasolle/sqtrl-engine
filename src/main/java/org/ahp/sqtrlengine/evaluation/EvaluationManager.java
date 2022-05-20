package org.ahp.sqtrlengine.evaluation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ahp.sqtrlengine.exception.InvalidRuleFileException;
import org.ahp.sqtrlengine.model.Prefix;
import org.ahp.sqtrlengine.model.TransformationRule;
import org.ahp.sqtrlengine.service.CostBasedTransformationProcess;
import org.ahp.sqtrlengine.service.XMLRuleParser;
import org.ahp.sqtrlengine.utils.RuleUtils;
import org.apache.jena.ext.com.google.common.io.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Prepare and run the evaluation for a set of parameters
 * @author Nicolas Lasolle
 *
 */
public class EvaluationManager {

	private static final Logger logger = LogManager.getLogger(EvaluationManager.class);
	private static final String RULE_FILE = "evaluation/rules/validRules.xml";
	private static final String SPARQL_ENDPOINT = "http://localhost:3030/";

	public static CombinationResult runEvaluation(ParameterCombination combination) {
		CombinationResult result = new CombinationResult();	
		Set<Double> applicationTimes = new HashSet<Double>();
		int queries = 0;

		Instant start = Instant.now() ; 

		CostBasedTransformationProcess transformationProcess = new CostBasedTransformationProcess(combination.getMaxCost(), 
				combination.getRules(), combination.getQuery(), combination.getDataset(), combination.isPruning());

		transformationProcess.sortRules();

		Instant startRuleApplication = Instant.now();

		while(transformationProcess.getNextNode() != null) {
			Duration d = Duration.between( startRuleApplication, Instant.now() );
			double queryGenerationTime = Double.parseDouble(d.getSeconds() + "." + d.getNano()) ;
			logger.info(" Query generation time " + queryGenerationTime);
			applicationTimes.add( queryGenerationTime );

			queries++;
			startRuleApplication = Instant.now() ;
		}

		//Compute the full application time and an average value for rule application time
		Instant stop = Instant.now() ;
		Duration d = Duration.between(start, stop);
		double fullTreeTime = Double.parseDouble(d.getSeconds() + "." + d.getNano());

		double averageApplicationTime = applicationTimes.stream()
				.mapToDouble(e -> e)
				.average()
				.orElse(0);

		//Save the results
		result.setParameterCombination(combination);
		result.setFullTreeTime(fullTreeTime);
		result.setAverageRuleApplicationTime(averageApplicationTime);
		result.setGeneratedQueries(queries);

		logger.info("Full Tree time " + fullTreeTime + " s");
		logger.info("Average rule application time " + averageApplicationTime + " s");
		logger.info("#Generated queries " + queries);

		return result;
	}


	/**
	 * Prepare the evaluation by setting parameter combination
	 * @return list of parameter combination
	 * @throws InvalidRuleFileException 
	 * @throws IOException 
	 */
	public static List<ParameterCombination> prepareEvaluation() throws InvalidRuleFileException, IOException {
		List<ParameterCombination> combinations = new ArrayList<ParameterCombination>();

		/*** Rule file management ****/
		//First get the full rule file and then create sub sets with various sizes
		File ruleFile = new File(EvaluationManager.class.getClassLoader().getResource(RULE_FILE).getFile());
		File dbpediaRuleFile = new File(EvaluationManager.class.getClassLoader().getResource("evaluation/rules/dbpediaRules.xml").getFile());

		XMLRuleParser parser = new XMLRuleParser(ruleFile);
		parser.loadXMLDocument();

		List<TransformationRule> fullRuleSet = parser.parseRuleFile();
		List<Prefix> prefixes = parser.parsePrefixes();

		RuleUtils.replacePrefixes(fullRuleSet, prefixes);

		//Shuffle the lists
		Collections.shuffle(fullRuleSet);
		List<TransformationRule> tinyRuleSet = fullRuleSet.subList(0, 2);
		Collections.shuffle(fullRuleSet);
		List<TransformationRule> smallRuleSet = fullRuleSet.subList(0, 5);
		Collections.shuffle(fullRuleSet);
		List<TransformationRule> mediumRuleSet = fullRuleSet.subList(0, 10);

		parser = new XMLRuleParser(dbpediaRuleFile);
		parser.loadXMLDocument();

		List<TransformationRule> dbpediaRuleSet = parser.parseRuleFile();
		List<Prefix> dbpediaPrefixes = parser.parsePrefixes();

		RuleUtils.replacePrefixes(dbpediaRuleSet, dbpediaPrefixes);

		/*** Dataset management ***/
		String fullAhpDataset = SPARQL_ENDPOINT + "full_ahp_corpus",
				smallAhpDataset = SPARQL_ENDPOINT + "small_ahp_corpus",
				mediumAhpDataset = SPARQL_ENDPOINT + "medium_ahp_corpus";

		/*** Query management ***/
		int i =1;
		//SPARQL queries string are stored within external file
		String smallQueryFile = "evaluation/queries/smallEvaluationQuery" + i + ".rq",
				mediumQueryFile = "evaluation/queries/mediumEvaluationQuery" + i + ".rq",
				bigQueryFile = "evaluation/queries/bigEvaluationQuery" + i + ".rq";

		String dbpediaDataset = "https://dbpedia.org/sparql";

		String smallQuery = Resources.toString(EvaluationManager.class.getClassLoader().getResource(smallQueryFile), StandardCharsets.UTF_8);
		String mediumQuery = Resources.toString(EvaluationManager.class.getClassLoader().getResource(mediumQueryFile), StandardCharsets.UTF_8);
		String bigQuery = Resources.toString(EvaluationManager.class.getClassLoader().getResource(bigQueryFile), StandardCharsets.UTF_8);
		String dbpediaQuery = Resources.toString(EvaluationManager.class.getClassLoader().getResource("evaluation/queries/dbpedia1.rq"), StandardCharsets.UTF_8);


		//C0
		combinations.add(new ParameterCombination(true, 10, false, mediumRuleSet, fullAhpDataset , mediumQuery));
		//C1
		//combinations.add(new ParameterCombination(true, 10, false, mediumRuleSet, fullAhpDataset_distant , mediumQuery));
		//C2
		combinations.add(new ParameterCombination(true, 5, false, mediumRuleSet, fullAhpDataset , mediumQuery));
		//C3
		//combinations.add(new ParameterCombination(true, 20, false, mediumRuleSet, fullAhpDataset , mediumQuery));
		//C4
		//combinations.add(new ParameterCombination(true, 100, false, mediumRuleSet, fullAhpDataset , mediumQuery));
		//C5
		combinations.add(new ParameterCombination(true, Integer.MAX_VALUE, true, mediumRuleSet, fullAhpDataset , mediumQuery));
		//C6
		combinations.add(new ParameterCombination(true, 10, true, mediumRuleSet, fullAhpDataset , mediumQuery));
		//C7
		combinations.add(new ParameterCombination(true, 10, false, tinyRuleSet, fullAhpDataset , mediumQuery));
		//C8
		combinations.add(new ParameterCombination(true, 10, true, smallRuleSet, fullAhpDataset , mediumQuery));
		//C9
		combinations.add(new ParameterCombination(true, 10, false,fullRuleSet , fullAhpDataset , mediumQuery));
		//C10
		combinations.add(new ParameterCombination(true, 10, false, mediumRuleSet, fullAhpDataset , mediumQuery));
		//C11
		combinations.add(new ParameterCombination(true, 10, false, dbpediaRuleSet, dbpediaDataset , dbpediaQuery));
		//C12
		combinations.add(new ParameterCombination(true, 10, false, mediumRuleSet, fullAhpDataset , smallQuery));
		//C13
		combinations.add(new ParameterCombination(true, 10, false, mediumRuleSet, fullAhpDataset , mediumQuery));
		//C14
		combinations.add(new ParameterCombination(true, 10, false, mediumRuleSet, fullAhpDataset , bigQuery));
		//C15


		return combinations;

	}

}
