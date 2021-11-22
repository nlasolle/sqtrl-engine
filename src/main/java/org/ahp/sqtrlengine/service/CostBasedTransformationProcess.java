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
			String query, String sparqlEndpoint) {
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

	/** Get the next transformation node
	 * 
	 * @return true if a node has been created
	 */
	public TransformationNode getNextNode() {

		/* At this step, the rule list is supposed to be ordered based on their transformation costs.
		   The goal is to find the rule applicable with the lower total cost
		 */
		for(TransformationRule rule: rules) {
			for(TransformationNode existingNode : nodes) {

				if(!existingNode.getAppliedRuleIRI().contains(rule.getIri()) && 
						(rule.getCost() + existingNode.getGlobalCost()) <= maxCost ) {
	
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
					
						RuleApplication application = applications.remove(0);
						
						//Find the cost for the given rule IRI
						TransformationRule currentRule = rules.stream()
								  .filter(r -> application.getRuleIri().equals(r.getIri()))
								  .findAny()
								  .orElse(null);
						pendingNode.setGlobalCost(existingNode.getGlobalCost() + currentRule.getCost());

						 
						pendingNode.setParentNode(existingNode);
						pendingNode.setLevel(existingNode.getLevel() + 1);
						pendingNode.setApplication(application);
						existingNode.setPendingApplications(applications);
						existingNode.addAppliedRuleIRI(currentRule.getIri());
						nodes.add(pendingNode);
						
						pendingNode.setId(existingNode.getId() + existingNode.getAppliedRuleIRI().size());
						
						logger.info("Pending node application " + pendingNode.getApplication());
						logger.info("Pending node cost " + pendingNode.getGlobalCost());
						return pendingNode;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Sort the rules based on their transformation cost
	 */
	public void sortRules() {
		rules.sort(Comparator.comparing(TransformationRule::getCost));
		logger.info(rules);
	}
}
