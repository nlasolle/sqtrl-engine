package org.ahp.sqtrlengine.service;

import java.util.List;

import org.ahp.sqtrlengine.model.TransformationRule;
import org.apache.jena.query.Query;

/**
 * Store all transformation process steps related
 * to a initial query, a set of rules and an RDF base
 * @author Nicolas Lasolle
 *
 */
public abstract class TransformationProcess {

	protected List<TransformationRule> rules;
	protected Query query;
	protected String sparqlEndpoint;
	
	protected RuleApplyer ruleApplyer;
	
	public TransformationProcess(List<TransformationRule> rules, Query query, String sparqlEndpoint) {
		this.rules = rules;
		this.query = query;
		this.sparqlEndpoint = sparqlEndpoint;
	}

	public List<TransformationRule> getRules() {
		return rules;
	}

	public void setRules(List<TransformationRule> rules) {
		this.rules = rules;
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public String getSparqlEndpoint() {
		return sparqlEndpoint;
	}

	public void setSparqlEndpoint(String sparqlEndpoint) {
		this.sparqlEndpoint = sparqlEndpoint;
	}
	
}
