package org.ahp.sqtrlengine.service;

import java.util.Comparator;
import java.util.List;

import org.ahp.sqtrlengine.model.RuleApplication;
import org.ahp.sqtrlengine.model.TransformationNode;
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
		this.maxCost = maxCost;
		
		sortRules();
	}

	public double getMaxCost() {
		return maxCost;
	}

	public void setMaxCost(double maxCost) {
		this.maxCost = maxCost;
	}

	public boolean getNextNode() {

		/* At this step, the rule list is supposed to be ordered based on their transformation costs.
		   The goal is to find the rule applicable with the lower total cost
		 */
		for(TransformationRule rule: rules) {
			for(TransformationNode existingNode : nodes) {
				List<RuleApplication> applications;
				
				if(existingNode.getPendingApplications().isEmpty()) {
					Query nodeQuery;
					if(existingNode.getLevel() == 0) {
						nodeQuery = query; //Level 0 is for the initial node 
					} else { 
						nodeQuery = existingNode.getApplication().getGeneratedQuery();
					}
					
					applications = ruleApplyer.getRuleApplications(nodeQuery, rule, sparqlEndpoint);
				} else {
					applications = existingNode.getPendingApplications();
				}
				
				if(applications != null && !applications.isEmpty()) {
					TransformationNode pendingNode = new TransformationNode();
					//The rule application details are saved for the newly formed node
					pendingNode.setGlobalCost(existingNode.getGlobalCost() + rule.getCost());
					pendingNode.setParentNode(existingNode);
					pendingNode.setLevel(existingNode.getLevel() + 1);
					RuleApplication application = applications.remove(0);
					pendingNode.setApplication(application);
					existingNode.setPendingApplications(applications);
					nodes.add(pendingNode);
					
					logger.info("Rule application " + application);
					
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * Sort the rules based on their transformation cost
	 */
	public void sortRules() {
		rules.sort(Comparator.comparing(TransformationRule::getCost));
		logger.info(rules);
	}
}
