package org.ahp.sqtrlengine.evaluation;

/**
 * Evaluation result for a single run (a set of parameters values)
 * @author Nicolas Lasolle
 *
 */
public class CombinationResult {
	private double averageRuleApplicationTime;
	private double fullTreeTime;
	private int generatedQueries;

	private ParameterCombination parameterCombination;

	public double getAverageRuleApplicationTime() {
		return averageRuleApplicationTime;
	}

	public void setAverageRuleApplicationTime(double averageRuleApplicationTime) {
		this.averageRuleApplicationTime = averageRuleApplicationTime;
	}

	public double getFullTreeTime() {
		return fullTreeTime;
	}

	public void setFullTreeTime(double fullTreeTime) {
		this.fullTreeTime = fullTreeTime;
	}

	public int getGeneratedQueries() {
		return generatedQueries;
	}

	public void setGeneratedQueries(int generatedQueries) {
		this.generatedQueries = generatedQueries;
	}

	public ParameterCombination getParameterCombination() {
		return parameterCombination;
	}

	public void setParameterCombination(ParameterCombination parameterCombination) {
		this.parameterCombination = parameterCombination;
	}

}
