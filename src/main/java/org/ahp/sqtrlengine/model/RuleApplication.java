package org.ahp.sqtrlengine.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.jena.graph.Triple;

/**
 * Represent a rule application, composed of several bindings
 * @author Nicolas Lasolle
 *
 */
public class RuleApplication {
	private String ruleIri; // Id of the transformation rule;
	private HashMap<String, String> contextBinding = new HashMap<>();
	private HashMap<String, String> leftBinding = new HashMap<>();
	private List<Triple> leftTriples = new ArrayList<Triple>();
	private List<Triple> rightTriples = new ArrayList<Triple>();
	
	private String initialQuery;
	private String generatedQuery;
	
	
	
	public String getRuleIri() {
		return ruleIri;
	}

	public void setRuleIri(String ruleIri) {
		this.ruleIri = ruleIri;
	}

	public String getInitialQuery() {
		return initialQuery;
	}

	public void setInitialQuery(String initialQuery) {
		this.initialQuery = initialQuery;
	}

	public String getGeneratedQuery() {
		return generatedQuery;
	}

	public void setGeneratedQuery(String generatedQuery) {
		this.generatedQuery = generatedQuery;
	}

	public HashMap<String, String> getContextBinding() {
		return contextBinding;
	}
	
	public void setContextBinding(HashMap<String, String> contextBinding) {
		this.contextBinding = contextBinding;
	}
	
	public HashMap<String, String> getLeftBinding() {
		return leftBinding;
	}
	
	public void setLeftBinding(HashMap<String, String> leftBinding) {
		this.leftBinding = leftBinding;
	}

	public List<Triple> getLeftTriples() {
		return leftTriples;
	}

	public void setLeftTriples(List<Triple> triples) {
		this.leftTriples = triples;
	}
	
	public List<Triple> getRightTriples() {
		return rightTriples;
	}

	public void setRightTriples(List<Triple> triples) {
		this.rightTriples = triples;
	}

	@Override
	public String toString() {
		return "RuleApplication [ruleIri=" + ruleIri + ",\ncontextBinding=" + contextBinding + ",\nleftBinding="
				+ leftBinding + ",\nleftTriples=" + leftTriples
				+ ",\ninitialQuery=" + initialQuery + ",\ngeneratedQuery=" + generatedQuery + "]";
	}
	
}
