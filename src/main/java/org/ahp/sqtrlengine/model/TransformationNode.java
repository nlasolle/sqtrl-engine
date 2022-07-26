package org.ahp.sqtrlengine.model;


import java.util.ArrayList;
import java.util.List;

public class TransformationNode implements Cloneable{


	//Sum of all required transformation costs
	private double globalCost; 

	//starting at 1 for indicating that it is the first state which has been reached
	private int position; 

	private int level;

	private String id;

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

	public void addAppliedRuleIRI(String iri) {
		appliedRuleIRI.add(iri);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object clone() {
		Object o = null;
		try {
			o = super.clone();
		} catch(CloneNotSupportedException cnse) {

			cnse.printStackTrace(System.err);
		}
		return o;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((application == null) ? 0 : application.hashCode());
		result = prime * result + ((appliedRuleIRI == null) ? 0 : appliedRuleIRI.hashCode());
		long temp;
		temp = Double.doubleToLongBits(globalCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isLeafNode ? 1231 : 1237);
		result = prime * result + level;
		result = prime * result + ((parentNode == null) ? 0 : parentNode.hashCode());
		result = prime * result + ((pendingApplications == null) ? 0 : pendingApplications.hashCode());
		result = prime * result + position;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransformationNode other = (TransformationNode) obj;
		if (application == null) {
			if (other.application != null)
				return false;
		} else if (!application.equals(other.application))
			return false;
		if (appliedRuleIRI == null) {
			if (other.appliedRuleIRI != null)
				return false;
		} else if (!appliedRuleIRI.equals(other.appliedRuleIRI))
			return false;
		if (Double.doubleToLongBits(globalCost) != Double.doubleToLongBits(other.globalCost))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isLeafNode != other.isLeafNode)
			return false;
		if (level != other.level)
			return false;
		if (parentNode == null) {
			if (other.parentNode != null)
				return false;
		} else if (!parentNode.equals(other.parentNode))
			return false;
		if (pendingApplications == null) {
			if (other.pendingApplications != null)
				return false;
		} else if (!pendingApplications.equals(other.pendingApplications))
			return false;
		if (position != other.position)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TransformationNode [globalCost=" + globalCost + ", position=" + position + ", level=" + level + ", id="
				+ id + ", parentNode=" + parentNode + ", isLeafNode=" + isLeafNode + ", pendingApplications="
				+ pendingApplications + ", appliedRuleIRI=" + appliedRuleIRI + ", application=" + application + "]";
	}

}
