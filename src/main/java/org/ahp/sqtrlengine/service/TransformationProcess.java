package org.ahp.sqtrlengine.service;

import java.util.ArrayList;
import java.util.List;

import org.ahp.sqtrlengine.exception.QueryException;
import org.ahp.sqtrlengine.model.TransformationNode;
import org.ahp.sqtrlengine.model.TransformationRule;
import org.ahp.sqtrlengine.utils.QueryUtils;
import org.apache.jena.query.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Store all transformation process steps related
 * to a initial query, a set of rules and an RDF base
 * @author Nicolas Lasolle
 *
 */
public abstract class TransformationProcess {

	protected static final Logger logger = LogManager.getLogger(TransformationProcess.class);
	
	protected List<TransformationRule> rules;
	protected Query query;
	protected String sparqlEndpoint;

	protected RuleApplyer ruleApplyer;
	
	protected List<TransformationNode> nodes = new ArrayList<>();
	
	protected TransformationProcess(List<TransformationRule> rules, String query, String sparqlEndpoint) throws QueryException {
		this.rules = rules;
		this.query = QueryUtils.parseQuery(query);
		this.sparqlEndpoint = sparqlEndpoint;
		ruleApplyer = new RuleApplyer(sparqlEndpoint);
		TransformationNode initialNode = new TransformationNode();
		initialNode.setLevel(0);
		initialNode.setGlobalCost(0);
		initialNode.setId("Q");
		nodes.add(initialNode);
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
	
	public List<TransformationNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<TransformationNode> nodes) {
		this.nodes = nodes;
	}
	
}
