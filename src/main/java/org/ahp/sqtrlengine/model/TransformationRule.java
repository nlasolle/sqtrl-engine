package org.ahp.sqtrlengine.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

public class TransformationRule {

	private String iri; //Rule identifier
	private String label;  //Rule human-readable short description

	//Graph pattern, to find matching on the RDF graph
	private String context;

	//Graph pattern to look for in the original SPARQL query
	private String left;

	//Graph pattern replacing the left pattern
	private String right;

	//Rule transformation cost to prevent application of the rule given a maximum process cost
	private float cost; 
	
	//A list of graph pattern, representing exception for which the rule should be applied
	private List<String> exceptions = new ArrayList<>();

	//Rule explanation template, whose instantiation can reused variables from context, left, right fields
	private String explanation; 

	//To manage special rule types, with different behaviour from the general rules (ex: numerical extension within filter)
		private String type;
	
	public TransformationRule() {

	}

	public TransformationRule(String iri, String label) {
		this.iri = iri;
		this.label = label;
	}
	
	@SuppressWarnings("unchecked")
	public TransformationRule(TransformationRule rule) {
		this.type = rule.type;
		this.iri = rule.iri;
		this.label = rule.label;
		this.context = rule.context;
		this.left = rule.left;
		this.right = rule.right;
		this.cost = rule.cost;
		this.exceptions = (ArrayList<String>) ((ArrayList<String>) rule.exceptions).clone();
		this.explanation = rule.explanation;
	}

	
	public String getIri() {
		return iri;
	}

	public void setIri(String iri) {
		this.iri = iri;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLeft() {
		return left;
	}

	public void setLeft(String left) {
		this.left = left;
	}

	public String getRight() {
		return right;
	}

	public void setRight(String right) {
		this.right = right;
	}

	public List<String> getExceptions() {
		return exceptions;
	}

	public void setExceptions(List<String> exceptions) {
		this.exceptions = exceptions;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	@Override
	public String toString(){
		return  "{" +
				"\n\tiri = " + iri +
				"\n\tlabel = " + label +
				"\n\tcontext = " + context +
				"\n\tleft = " + left +
				"\n\tright = " + right +
				"\n\texceptions = " + exceptions +
				"\n\tcost = " + cost +
				"\n\texplanation = " + explanation + "\n}";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((exceptions == null) ? 0 : exceptions.hashCode());
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		
		return result;
	}

	/**
	 * Two rules are equal if their context, left, right and exception fields are equal.
	 */
	@Override
	public boolean equals(Object obj) {

		if(obj == null) {
			return false;
		}
		
		if (this.getClass() != obj.getClass())
		    return false;
		
		TransformationRule transformationRule = (TransformationRule) obj;
		
		if(!CollectionUtils.isEqualCollection(transformationRule.getExceptions(), exceptions)) {
			return false;
		}

		return transformationRule.getLeft().equals(this.left) 
				&& transformationRule.getRight().equals(this.right)
				&& transformationRule.getContext().equals(this.context) ;

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
