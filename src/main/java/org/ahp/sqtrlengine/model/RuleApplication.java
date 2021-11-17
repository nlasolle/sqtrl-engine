package org.ahp.sqtrlengine.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;

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
	
	private Query initialQuery;
	private Query generatedQuery;
	
	private String explanation;
	
	public String getRuleIri() {
		return ruleIri;
	}

	public void setRuleIri(String ruleIri) {
		this.ruleIri = ruleIri;
	}

	public Query getInitialQuery() {
		return initialQuery;
	}

	public void setInitialQuery(Query initialQuery) {
		this.initialQuery = initialQuery;
	}

	public Query getGeneratedQuery() {
		return generatedQuery;
	}

	public void setGeneratedQuery(Query generatedQuery) {
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
		return "{ruleIri=" + ruleIri
				+ ",\ncontextBinding=" + contextBinding
				+ ",\nleftBinding=" + leftBinding
				+ ",\nleftTriples=" + leftTriples
				+ ",\nrightTriples=" + rightTriples
				+ ",\ninitialQuery=" + initialQuery
				+ ",\ngeneratedQuery=" + generatedQuery
				+ ",\nexplanation=" + explanation
				+ "}";
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	
}
