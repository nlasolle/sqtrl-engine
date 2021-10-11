package org.ahp.sqtrlengine.service;

import java.util.List;

import org.ahp.sqtrlengine.model.RuleApplication;
import org.ahp.sqtrlengine.model.TransformationRule;
import org.apache.jena.query.Query;

/**
 * 
 * @author Nicolas Lasolle
 *
 */
public class CostBasedTransformationProcess extends TransformationProcess {
	
	private double maxCost;
	
	public CostBasedTransformationProcess(double maxCost, List<TransformationRule> rules, 
			Query query, String sparqlEndpoint) {
		super(rules, query, sparqlEndpoint);
		this.setMaxCost(maxCost);
	}

	public double getMaxCost() {
		return maxCost;
	}

	public void setMaxCost(double maxCost) {
		this.maxCost = maxCost;
	}
	
	public RuleApplication getNextRuleApplication() {
		
		RuleApplication application = null;
		//ruleApplyer.getRuleApplication(rule, query, contextBindings);
		return application ;
		
	}
}
