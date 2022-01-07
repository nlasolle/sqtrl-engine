package org.ahp.sqtrlengine.evaluation;

import java.util.List;

import org.ahp.sqtrlengine.model.TransformationRule;

/**
 * Set of parameter for a combination in the SQTRL engine evaluation
 * @author Nicolas Lasolle
 *
 */
public class ParameterCombination {

	private boolean localEndpoint;
	private double maxCost; 
	private boolean pruning;
	private List<TransformationRule> rules;
	private String dataset;
	private String query;
	
	public ParameterCombination (boolean localEndpoint, double maxCost, boolean pruning,
			List<TransformationRule> rules, String dataset, String query) {
		this.localEndpoint = localEndpoint;
		this.maxCost = maxCost;
		this.pruning = pruning;
		this.rules = rules;
		this.dataset = dataset;
		this.query = query;
	}

	public boolean isLocalEndpoint() {
		return localEndpoint;
	}

	public void setLocalEndpoint(boolean localEndpoint) {
		this.localEndpoint = localEndpoint;
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
