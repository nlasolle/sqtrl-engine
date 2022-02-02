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
	private static final String RULE_FILE = "validRules.xml";
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

		/*** Dataset management ***/
		String fullAhpDataset = SPARQL_ENDPOINT + "full_ahp_corpus",
				smallAhpDataset = SPARQL_ENDPOINT + "small_ahp_corpus",
				mediumAhpDataset = SPARQL_ENDPOINT + "medium_ahp_corpus";

		/*** Query management ***/

		//SPARQL queries string are stored within external file
		String smallQueryFile = "queries/smallEvaluationQuery.rq",
				mediumQueryFile = "queries/mediumEvaluationQuery.rq",
				bigQueryFile = "queries/bigEvaluationQuery.rq";

		String smallQuery = Resources.toString(EvaluationManager.class.getClassLoader().getResource(smallQueryFile), StandardCharsets.UTF_8);
		String mediumQuery = Resources.toString(EvaluationManager.class.getClassLoader().getResource(mediumQueryFile), StandardCharsets.UTF_8);
		String bigQuery = Resources.toString(EvaluationManager.class.getClassLoader().getResource(bigQueryFile), StandardCharsets.UTF_8);
		
		combinations.add(new ParameterCombination(true, 2, false, mediumRuleSet, fullAhpDataset , mediumQuery));
		combinations.add(new ParameterCombination(true, 2, true, mediumRuleSet, fullAhpDataset , mediumQuery));
		combinations.add(new ParameterCombination(true, 4, false, mediumRuleSet, fullAhpDataset , mediumQuery));
		combinations.add(new ParameterCombination(true, 4, true, mediumRuleSet, fullAhpDataset , mediumQuery));
		combinations.add(new ParameterCombination(true, 10, false, mediumRuleSet, fullAhpDataset , mediumQuery));
		combinations.add(new ParameterCombination(true, 10, true, mediumRuleSet, fullAhpDataset , mediumQuery));

		return combinations;

	}

}
