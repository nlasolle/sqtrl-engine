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
			Instant stopRuleApplication = Instant.now();
			Duration duration = Duration.between( startRuleApplication, stopRuleApplication );
			
			//Problem with leading zeros in getNano().  Ex.. 1.05 --> 1 + . + 5 instead of 1 + . + 05
			//double queryGenerationTime = Double.parseDouble(duration.getSeconds() + "." + duration.getNano()) ; 
			String textualDuration = duration.toString().substring(2, duration.toString().length() -2);
			if(textualDuration.equals("0") || textualDuration.equals("")) {
				applicationTimes.add( 0.0 );
			} else {
				double queryGenerationTime = Double.parseDouble(textualDuration);
				applicationTimes.add( queryGenerationTime );
			}	
			queries++;
			startRuleApplication = Instant.now() ;	
		}

		//Compute the full application time and an average value for rule application time
		Instant stop = Instant.now();
		Duration durationFull = Duration.between( start, stop );
		double fullTreeTime = Double.parseDouble(durationFull.getSeconds() + "." + durationFull.getNano());

		double averageApplicationTime = applicationTimes.stream()
				.mapToDouble(e -> e)
				.average()
				.orElse(0);

		//Save the results
		result.setParameterCombination(combination);
		result.setFullTreeTime(fullTreeTime);
		result.setAverageRuleApplicationTime(averageApplicationTime);
		result.setGeneratedQueries(queries);

		logger.warn("Full Tree time " + fullTreeTime + " s");
		logger.warn("Average rule application time " + averageApplicationTime + " s");
		logger.warn("#Generated queries " + queries);

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
		//File perseeRuleFile = new File(EvaluationManager.class.getClassLoader().getResource("evaluation/rules/perseeRules.xml").getFile());
		File dbpediaRuleFile = new File(EvaluationManager.class.getClassLoader().getResource("evaluation/rules/dbpediaRules.xml").getFile());

		XMLRuleParser parser = new XMLRuleParser(ruleFile);
		parser.loadXMLDocument();

		List<TransformationRule> fullRuleSet = parser.parseRuleFile();
		List<Prefix> prefixes = parser.parsePrefixes();

		RuleUtils.replacePrefixes(fullRuleSet, prefixes);

		//Shuffle and create the subset
		Collections.shuffle(fullRuleSet);
		List<TransformationRule> bigRuleSet = fullRuleSet.subList(0, 14);
		Collections.shuffle(bigRuleSet);
		List<TransformationRule> mediumRuleSet = bigRuleSet.subList(0, 10);
		Collections.shuffle(mediumRuleSet);
		List<TransformationRule> smallRuleSet = mediumRuleSet.subList(0, 5);
		Collections.shuffle(smallRuleSet);
		List<TransformationRule> tinyRuleSet = smallRuleSet.subList(0, 2);	

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
		combinations.add(new ParameterCombination("Henri Poincaré correspondence corpus", 10, true, mediumRuleSet, fullAhpDataset , mediumQuery));
		//C1
		combinations.add(new ParameterCombination("Henri Poincaré correspondence corpus", 10, false, mediumRuleSet, fullAhpDataset , mediumQuery));
		//C2
		combinations.add(new ParameterCombination("Henri Poincaré correspondence corpus", 3, true, mediumRuleSet, fullAhpDataset , mediumQuery));
		//C3
		combinations.add(new ParameterCombination("Henri Poincaré correspondence corpus", 5, true, mediumRuleSet, fullAhpDataset , mediumQuery));
		//C4
		combinations.add(new ParameterCombination("Henri Poincaré correspondence corpus", 20, true, mediumRuleSet, fullAhpDataset , mediumQuery));
		//C5
		combinations.add(new ParameterCombination("Henri Poincaré correspondence corpus", Integer.MAX_VALUE, true, mediumRuleSet, fullAhpDataset , mediumQuery));
		//C6
		combinations.add(new ParameterCombination("Henri Poincaré correspondence corpus", 10, true, tinyRuleSet, fullAhpDataset , mediumQuery));
		//C7
		combinations.add(new ParameterCombination("Henri Poincaré correspondence corpus", 10, true, smallRuleSet, fullAhpDataset , mediumQuery));
		//C8
		combinations.add(new ParameterCombination("Henri Poincaré correspondence corpus", 10, true, bigRuleSet, fullAhpDataset , mediumQuery));
		//C9
		//combinations.add(new ParameterCombination("HP", 10, true, mediumRuleSet , smallAhpDataset , mediumQuery));
		//C10
		//combinations.add(new ParameterCombination("HP", 10, true, mediumRuleSet, mediumAhpDataset , mediumQuery));
		//C11
		//combinations.add(new ParameterCombination("Persée", 10, true, perseeRuleSet, perseeDataset , mediumQuery));
		//C12
		combinations.add(new ParameterCombination("DBpedia", 10, true, dbpediaRuleSet, dbpediaDataset , dbpediaQuery));
		//C13
		combinations.add(new ParameterCombination("Henri Poincaré correspondence corpus", 10, true, mediumRuleSet, fullAhpDataset , smallQuery));
		//C14
		combinations.add(new ParameterCombination("Henri Poincaré correspondence corpus", 10, true, mediumRuleSet, fullAhpDataset , mediumQuery));
		//C15
		combinations.add(new ParameterCombination("Henri Poincaré correspondence corpus", 10, true, mediumRuleSet, fullAhpDataset , bigQuery));
		

		return combinations;

	}

}
