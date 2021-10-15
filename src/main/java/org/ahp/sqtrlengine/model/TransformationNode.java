package org.ahp.sqtrlengine.model;

import java.util.ArrayList;
import java.util.List;

public class TransformationNode {
	
	//Sum of all required transformation costs
	private double globalCost; 
	
	//starting at 1 for indicating that it is the first state which has been reached
	private int position; 
	
	private int level;
	
	//Direct parent node
	private TransformationNode parentNode;

	//Indicate if this state is at the end of the branch(because no rule is applicable considering the maximum cost)
	private boolean isLeafNode;
	
	//All the pending rule applications for this state (possible future child in the search tree) ordered based on transformation cost
	private List<RuleApplication> pendingApplications = new ArrayList<>();
	private List<String> appliedRuleIRI = new ArrayList<>();
	
	//The rule application which created this node
	private RuleApplication application;
	
	public double getGlobalCost() {
		return globalCost;
	}

	public void setGlobalCost(double globalCost) {
		this.globalCost = globalCost;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public TransformationNode getParentNode() {
		return parentNode;
	}

	public void setParentNode(TransformationNode parentNode) {
		this.parentNode = parentNode;
	}

	public boolean isLeafNode() {
		return isLeafNode;
	}

	public void setLeafNode(boolean isLeafNode) {
		this.isLeafNode = isLeafNode;
	}

	public List<RuleApplication> getPendingApplications() {
		return pendingApplications;
	}

	public void setPendingApplications(List<RuleApplication> applications) {
		this.pendingApplications = applications;
	}

	public RuleApplication getApplication() {
		return application;
	}

	public void setApplication(RuleApplication application) {
		this.application = application;
	}

	public List<String> getAppliedRuleIRI() {
		return appliedRuleIRI;
	}

	public void setAppliedRuleIRI(List<String> appliedRuleIRI) {
		this.appliedRuleIRI = appliedRuleIRI;
	}

	public void addAppliedRuleIRI(String IRI) {
		appliedRuleIRI.add(IRI);
	}
	
}
