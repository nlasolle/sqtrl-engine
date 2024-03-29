package org.ahp.sqtrlengine.service;

import java.util.Comparator;
import java.util.List;

import org.ahp.sqtrlengine.exception.QueryException;
import org.ahp.sqtrlengine.model.RuleApplication;
import org.ahp.sqtrlengine.model.TransformationNode;
import org.ahp.sqtrlengine.model.TransformationRule;
import org.ahp.sqtrlengine.utils.QueryUtils;
import org.apache.jena.query.Query;

/**
 * 
 * @author Nicolas Lasolle
 *
 */
public class CostBasedTransformationProcess extends TransformationProcess {

	private boolean pruning;
	private double maxCost;

	public CostBasedTransformationProcess(double maxCost, List<TransformationRule> rules,
			String query, String sparqlEndpoint, boolean pruning) throws QueryException {
		super(rules, query, sparqlEndpoint);
		this.maxCost = maxCost;
		this.pruning = pruning;
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
		double currentBestCost = maxCost;
		TransformationNode pendingNode = null;
		TransformationNode candidateNode = null;
		TransformationNode candidateExistingNode = null;

		for(TransformationRule rule: rules) {
			for(TransformationNode existingNode : nodes) {

				if(!existingNode.getAppliedRuleIRI().contains(rule.getIri()) && 
						(rule.getCost() + existingNode.getGlobalCost()) <= maxCost &&
						(rule.getCost() + existingNode.getGlobalCost()) <= currentBestCost) {
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

						pendingNode = new TransformationNode();

						//The rule application details are saved for the newly formed node
						RuleApplication application = applications.remove(0);

						//Find the cost for the given rule IRI
						TransformationRule currentRule = rules.stream()
								.filter(r -> application.getRuleIri().equals(r.getIri()))
								.findAny()
								.orElse(null);
						
						if(currentRule == null) {
							logger.info("No rule with iri {} has been found in the rule list", application.getRuleIri());
							break;
						}
						
						pendingNode.setGlobalCost(existingNode.getGlobalCost() + currentRule.getCost());

						pendingNode.setParentNode(existingNode);
						pendingNode.setLevel(existingNode.getLevel() + 1);
						pendingNode.setApplication(application);
						existingNode.setPendingApplications(applications);
						//existingNode.addAppliedRuleIRI(currentRule.getIri());
						candidateExistingNode = existingNode;
						int idLastPart = existingNode.getAppliedRuleIRI().size() + 1;
						pendingNode.setId(existingNode.getId() + idLastPart);
						currentBestCost = pendingNode.getGlobalCost();

						//This statement checks if the query has already been generated with a lower cost
						if(pendingNode != null) {
							if(pruning && isQueryExisting(pendingNode.getApplication().getGeneratedQuery())) {
								candidateExistingNode.addAppliedRuleIRI(pendingNode.getApplication().getRuleIri()); 
							} else {
								candidateNode = (TransformationNode) pendingNode.clone();
							}
						} 

						logger.info("Pending node application {}", pendingNode.getApplication());
						logger.info("Pending node cost {}", pendingNode.getGlobalCost());

					}
				}
			}
		}

		//We need to save that the given rule has been applied for
		if(candidateNode != null) {
			candidateExistingNode.addAppliedRuleIRI(candidateNode.getApplication().getRuleIri());
			nodes.add(candidateNode);
		}

		return candidateNode;
	}

	/**
	 * Sort the rules based on their transformation cost
	 */
	public void sortRules() {
		rules.sort(Comparator.comparing(TransformationRule::getCost));
		logger.info(rules);
	}

	/**
	 * Check if the query has already been generated (with lower cost)
	 */
	private boolean isQueryExisting(Query query) {

		for(TransformationNode node : nodes) {
			if(node.getLevel() == 0) { 
				if(QueryUtils.equivalent(query, this.query)){
					return true;
				} 
			} else if (QueryUtils.equivalent(query, node.getApplication().getGeneratedQuery())) {
				return true;
			} 

		}

		return false;
	}
}
