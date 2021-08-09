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
	
	//A list of graph pattern, representing exception for which the rule should be applied
	private List<String> exceptions = new ArrayList<String>();
	
	//Rule transformation cost to prevent application of the rule given a maximum process cost
	private float cost; 
	
	//TO DO remove this field
	private String type;
	
	//Rule explanation template, whose instantiation can reused variables from context, left, right fields
	private String explanation; 
	
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
		return "Transformation rule " + iri + "{" +
				"\n\tLabel = " + label +
				"\n\tcontext = " + context +
				"\n\tleft = " + left +
				"\n\tright = " + right +
				"\n\texceptions = " + exceptions +
				"\n\tcost = " + cost +
				"\n\texplanation = " + explanation + "\n}";
	}
	
	/**
	 * Two rules are equal if their context, left, right and exception fields are equal.
	 */
	@Override
	public boolean equals(Object rule) {
		
		TransformationRule transformationRule = (TransformationRule) rule;
		
		if(!CollectionUtils.isEqualCollection(transformationRule.getExceptions(), exceptions)) {
			return false;
		};
		
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
