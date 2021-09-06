package org.ahp.sqtrlengine.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ahp.sqtrlengine.dao.JenaWrapper;
import org.ahp.sqtrlengine.model.RuleApplication;
import org.ahp.sqtrlengine.model.TransformationRule;
import org.ahp.sqtrlengine.utils.QueryUtils;
import org.apache.jena.graph.Node;
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
				if(solution.get(var).isResource()) {
					binding.put(var, "<" + solution.get(var).toString() + ">");
				} else if(solution.get(var).isLiteral()) {
					binding.put(var, "\"" + solution.get(var).toString() + "\"");
				} else {
					binding.put(var, solution.get(var).toString());
				}
			}

			bindings.add(binding);
		}

		return bindings;
	}

	/**
	 * Bind left member using the bindings related to rule context
	 * @param rule a SPARQL query transformation rule
	 * @param bindings the context bindings
	 */
	public List<String> bindLeftMember(TransformationRule rule, List<HashMap<String, String>> bindings) {
		List<String> boundLeft = new ArrayList<>();
		String leftToContext;

		for(HashMap<String, String> binding : bindings) {
			leftToContext = rule.getLeft();

			for(Entry<String, String> entry : binding.entrySet()) {
				leftToContext = leftToContext.replaceAll(entry.getKey().replaceAll("\\?", "\\\\?").replaceAll("\\$", "\\\\$"), 
						entry.getValue());
			}

			boundLeft.add(leftToContext);
		}

		return boundLeft;
	}

	/**
	 * Match bound left to query for transformation
	 * @param rule a SQTRL rule
	 * @param query the initial SPARQL query
	 * @param boundLeftList list of left field with context
	 */
	public List<RuleApplication> getRuleApplication(TransformationRule rule, Query query, List<HashMap<String, String>> contextBindings) {
		List<RuleApplication> applications = new ArrayList<>();
		System.out.println("QUERY " + query);
		List<Triple> queryTriples = QueryUtils.extractTriplePatterns(query);
		System.out.println(queryTriples);
		List<Triple> leftTriples;

		first:
			for(HashMap<String, String> contextBinding : contextBindings) {

				//First, we construct the boundLeft and bound right value, by assigning context binding variable values
				String boundLeft = rule.getLeft();

				List<Triple> boundLeftTriples = new ArrayList<>();

				for(Entry<String, String> entry : contextBinding.entrySet()) {
					System.out.println("ENTRY " + entry);
					boundLeft = boundLeft.replaceAll(entry.getKey().replaceAll("\\?", "\\\\?").replaceAll("\\$", "\\\\$"), 
							entry.getValue());
				}
				
				System.out.println("bound left " + boundLeft);
				Element boundLeftPattern = ParserARQ.parseElement("{" + boundLeft + "}");
				leftTriples = QueryUtils.extractTriplePatterns(boundLeftPattern);
				HashMap<String, String> leftBinding = new HashMap<>();

				second:
					for(Triple leftTriple : leftTriples) {

							for(int i = 0; i < queryTriples.size(); i++) {	
								
								Triple queryTriple = queryTriples.get(i);
								Node subject = null, predicate = null, object = null; // Fields of one final bound triple part of the left pattern

								HashMap<String, String> tempBinding = new HashMap<>();

								if(leftTriple.getSubject().isVariable()) {
									subject = queryTriple.getSubject();
									tempBinding.put(leftTriple.getSubject().toString(), queryTriple.getSubject().toString());
								} else if(!leftTriple.getSubject().equals(queryTriple.getSubject())){
									//This means that no matching has been found for a triple which is part of the current binding
									if(i == queryTriples.size()-1) {
										continue first;
									}
									continue;
								} else {
									subject = queryTriple.getSubject();
								}

								if(leftTriple.getPredicate().isVariable()) {
									predicate = queryTriple.getPredicate();
									tempBinding.put(leftTriple.getPredicate().toString(), queryTriple.getPredicate().toString());
								} else if(!leftTriple.getPredicate().equals(queryTriple.getPredicate())){
									if(i == queryTriples.size()-1) {
										continue first;
									}
									continue;
								} else {
									predicate = queryTriple.getPredicate();
								}

								if(leftTriple.getObject().isVariable()) {
									object = queryTriple.getObject();
									tempBinding.put(leftTriple.getObject().toString(), queryTriple.getObject().toString());
								} else if(!leftTriple.getObject().equals(queryTriple.getObject())){
									if(i == queryTriples.size()-1) {
										continue first;
									}

									continue;
								} else {
									object = queryTriple.getObject();
								}

								//This line is reached if a match has been found. 
								//It is possible to construct the final bound left triple and switch to the next one if necessary
								boundLeftTriples.add(new Triple(subject, predicate, object));
								leftBinding.putAll(tempBinding); 
								continue second;

							}
					}


				RuleApplication application = new RuleApplication();
				application.setRuleIri(rule.getIri());
				application.setLeftTriples(boundLeftTriples);
				//application.setRightTriples(boundRightTriples);
				application.setContextBinding(contextBinding);
				application.setLeftBinding(leftBinding);
				applications.add(application);
				//bindings.add(binding);
			}

		System.out.println("SIZE " + applications.size());
		return applications;

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
