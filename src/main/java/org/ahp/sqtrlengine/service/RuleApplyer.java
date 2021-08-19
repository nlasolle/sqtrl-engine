package org.ahp.sqtrlengine.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ahp.sqtrlengine.dao.JenaWrapper;
import org.ahp.sqtrlengine.model.TransformationRule;
import org.ahp.sqtrlengine.utils.QueryUtils;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.lang.ParserARQ;
import org.apache.jena.sparql.syntax.Element;

/**
 * Service dedicated to the application of a transformation rule for a given SPARQL query and an RDF graph
 * @author Nicolas Lasolle
 *
 */
public class RuleApplyer {

	/**
	 * Check for matching given the transformation rule and by looking for the body of the query
	 * @param rule a SPARQL query transformation rule
	 * @param query a SPARQL query
	 */
	public void mapLeftMember(TransformationRule rule, Query query) {
		String leftAsString = "{" + rule.getLeft() + "}";
		//Element leftPattern = new Element();


		Element element = ParserARQ.parseElement(leftAsString);
		System.out.println("--- Left ---\n" + leftAsString);
		System.out.println(element);


		List<Triple> triples = QueryUtils.extractTriplePatterns(query);
		System.out.println("--- Query body ---");

		for(Triple t : triples) {
			System.out.println(t);
		}
	}

	/**
	 * Generate and execute a SPARQL to find context bindings for the given transformation rule and RDFS base
	 * @param rule a SQTRL rule
	 * @param sparqlEndpoint
	 */
	public List<HashMap<String, String>> getContextBindings(TransformationRule rule, String sparqlEndpoint) {
		List<HashMap<String, String>> bindings = new ArrayList<>();
		
		String query = generateContextQuery(rule);

		System.out.println(query);
		List<String> variables = getBindingVariables(rule.getContext());
		System.out.println("VARIABLES " + variables);
		//Execute the query over the SPARQL endpoint
		ResultSet results = JenaWrapper.executeRemoteSelectQuery(query, sparqlEndpoint);
		
		//Save the bindings with the values for each variable
		while( results.hasNext() ){
			HashMap<String, String> binding = new HashMap<>();
			QuerySolution solution = results.next();

			for (String var : variables) {
				binding.put(var, solution.get(var).toString());
			}
				
			bindings.add(binding);
		}
		
	
		return bindings;
	}

	/**
	 * Retrieve all variables that occur in the rule context
	 * @param context SQTRL rule context graph pattern
	 * @return the variables as a String List
	 */
	private List<String> getBindingVariables(String context) {
		Pattern pattern = Pattern.compile("(\\?|\\$)[^\\s)({}]+");
		Matcher matcher = pattern.matcher(context);
		
		List<String> variables = new ArrayList<>();
		
		while (matcher.find()) {
			if(!variables.contains(matcher.group())) {
				variables.add(matcher.group());
			}
		}
		
		return variables;
	}

	/**
	 * Generate a SPARQL query based on the context and exception fields of a given transformation rule
	 * @param rule a SQTRL rule
	 * @return the generated SPARQL query to retrieve context bindings
	 */
	private String generateContextQuery(TransformationRule rule) {
		String generatedQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\nSELECT * WHERE {\n";

		if(rule.getContext() != null && !rule.getContext().isBlank()) {
			generatedQuery += rule.getContext() + "\n";
		}

		if(rule.getExceptions() != null && !rule.getExceptions().isEmpty()) {
			for(String exception : rule.getExceptions()) {

				if(!exception.toUpperCase().contains("FILTER")) {
					generatedQuery += "FILTER NOT EXISTS { "
							+ exception 
							+ " }\n";
				} else {
					generatedQuery += exception + "\n";
				}
			}
		}

		generatedQuery += "}";

		return generatedQuery;
	}
}
