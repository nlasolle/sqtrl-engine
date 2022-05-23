package org.ahp.sqtrlengine.evaluation;

import java.util.List;

import org.ahp.sqtrlengine.model.TransformationRule;

/**
 * Set of parameter for a combination in the SQTRL engine evaluation
 * @author Nicolas Lasolle
 *
 */
public class ParameterCombination {

	private String graphName; //The corpus name
	private double maxCost; 
	private boolean pruning; //Is pruning applying in order to prevent multiple query generation
	private List<TransformationRule> rules; //The set of rules
	private String dataset; 
	private String query; //The initial SPARQL query
	
	public ParameterCombination (String graphName, double maxCost, boolean pruning,
			List<TransformationRule> rules, String dataset, String query) {
		this.graphName = graphName;
		this.maxCost = maxCost;
		this.pruning = pruning;
		this.rules = rules;
		this.dataset = dataset;
		this.query = query;
	}

	public String getGraphName() {
		return graphName;
	}

	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}
	
	public double getMaxCost() {
		return maxCost;
	}

	public void setMaxCost(double maxCost) {
		this.maxCost = maxCost;
	}

	public boolean isPruning() {
		return pruning;
	}

	public void setPruning(boolean pruning) {
		this.pruning = pruning;
	}

	public List<TransformationRule> getRules() {
		return rules;
	}

	public void setRules(List<TransformationRule> rules) {
		this.rules = rules;
	}

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}


}
