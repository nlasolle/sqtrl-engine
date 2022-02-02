package org.ahp.sqtrlengine.evaluation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ahp.sqtrlengine.exception.InvalidRuleFileException;
/**
 * Launcher for a technical evaluation of the SQTR engine
 * @author Nicolas Lasolle
 *
 */
public class EvaluationLauncher {

	public static void main(String args []) throws InvalidRuleFileException, IOException {
		//First, the goal is to construct the full list of parameter combination (16 combinations for this evaluation)
		List<ParameterCombination> combinations = EvaluationManager.prepareEvaluation();
		List<CombinationResult> results = new ArrayList<CombinationResult>();

		//Run the evaluation for a combination, and get a result
		for(ParameterCombination combination : combinations) {
			CombinationResult result = EvaluationManager.runEvaluation(combination);
			if(result != null) {
				results.add(result);
			}
		}
		
		//Export of the full results list in a CSV file
		ResultsExporter exporter = new ResultsExporter();
		exporter.export(results);
	}
}
