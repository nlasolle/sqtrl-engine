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
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Service dedicated to the application of a transformation rule for a given SPARQL query and an RDF graph
 * @author Nicolas Lasolle
 *
 */
public class RuleApplyer {

	private static final Logger logger = LogManager.getLogger(RuleApplyer.class);

	/**
	 * Generate and execute a SPARQL to find context bindings for the given transformation rule and RDFS base
	 * @param rule a SQTRL rule
	 * @param sparqlEndpoint
	 */
	public List<HashMap<String, String>> getContextBindings(TransformationRule rule, String sparqlEndpoint) {
		List<HashMap<String, String>> bindings = new ArrayList<>();

		String query = generateContextQuery(rule);

		logger.debug(query);
		List<String> variables = getBindingVariables(rule.getContext());

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
		logger.debug("QUERY " + query);
		logger.debug("RULE " + rule);
		List<Triple> queryTriples = QueryUtils.extractTriplePatterns(query);
		logger.debug(queryTriples);
		List<Triple> leftTriples;

		/*We need to find associations between 
		 * context bindings related to the rule context and 
		 * the left pattern of the transformation rule */
		first:
			for(HashMap<String, String> contextBinding : contextBindings) {
				//First, we construct the boundLeft, by assigning context binding variable values
				leftTriples =  getBoundLeftTriples(rule, query, contextBinding);

				List<Triple> boundLeftTriples = new ArrayList<>();

				HashMap<String, String> leftBinding = new HashMap<>();

				second:
					for(Triple leftTriple : leftTriples) {

						for(int i = 0; i < queryTriples.size(); i++) {	

							Triple queryTriple = queryTriples.get(i);
							Node subject = null, predicate = null, object = null; // Fields of one final bound triple part of the left pattern

							HashMap<String, String> tempBinding = new HashMap<>();

							if(leftTriple.getSubject().isVariable()) {
								subject = queryTriple.getSubject();
								if(subject.isURI()) {
									tempBinding.put(leftTriple.getSubject().toString(), "<" + subject.toString() + ">");
								} else {
									tempBinding.put(leftTriple.getSubject().toString(), subject.toString());
								}
							} else if(!leftTriple.getSubject().equals(queryTriple.getSubject())){
								//This means that no matching has been found for a triple which is part of the current binding
								if(i == queryTriples.size()-1) {
									continue first;
								}
								continue;
							} else {
								subject = queryTriple.getSubject();
							}
							
							logger.debug("PREDICATE " + leftTriple.getPredicate());
							logger.debug("PREDICATE query " + queryTriple.getPredicate());
							if(leftTriple.getPredicate().isVariable()) {
								logger.debug("1");
								predicate = queryTriple.getPredicate();
								if(predicate.isURI()) {
									tempBinding.put(leftTriple.getPredicate().toString(), "<" + predicate.toString() + ">");
								} else {
									tempBinding.put(leftTriple.getPredicate().toString(), predicate.toString());
								}
							} else if(!leftTriple.getPredicate().equals(queryTriple.getPredicate())){
								logger.debug("2");
								if(i == queryTriples.size()-1) {
									continue first;
								}
								continue;
							} else {
								logger.debug("3");
								predicate = queryTriple.getPredicate();
							}

							if(leftTriple.getObject().isVariable()) {
								object = queryTriple.getObject();
								if(object.isURI()) {
									tempBinding.put(leftTriple.getObject().toString(), "<" + object.toString() + ">");
								} else {
									tempBinding.put(leftTriple.getObject().toString(), object.toString());
								}
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
							logger.debug("LEFT BINDING " + leftBinding);
							continue second;
						}
					}

				List<Triple> boundRightTriples = getBoundRightTriples(rule, query, contextBinding, leftBinding);

				RuleApplication application = new RuleApplication();
				application.setRuleIri(rule.getIri());
				application.setLeftTriples(boundLeftTriples);

				application.setRightTriples(boundRightTriples);
				application.setContextBinding(contextBinding);
				application.setLeftBinding(leftBinding);
				application.setInitialQuery(query);
				logger.debug(application);

				Query generatedQuery = applyTransformation(application);
				application.setGeneratedQuery(generatedQuery);

				applications.add(application);
				//bindings.add(binding);
			}


		return applications;

	}

	/**
	 * Find associations between context bindings related to the rule context and the left pattern of the transformation rule
	 * @param rule the transformation rule
	 * @param query the initial SPARQL query
	 * @param contextBinding the context bindings
	 * @return bindings for the rule left pattern
	 */
	private List<Triple> getBoundLeftTriples(TransformationRule rule, Query query, HashMap<String, String> contextBinding) {
		String boundLeft = rule.getLeft();
		String boundRight = rule.getRight();

		for(Entry<String, String> entry : contextBinding.entrySet()) {
			logger.debug("ENTRY " + entry);
			boundLeft = boundLeft.replaceAll(entry.getKey().replaceAll("\\?", "\\\\?").replaceAll("\\$", "\\\\$"), 
					entry.getValue());
			boundRight = boundRight.replaceAll(entry.getKey().replaceAll("\\?", "\\\\?").replaceAll("\\$", "\\\\$"), 
					entry.getValue());
		}

		logger.debug("BOUND LEFT " + boundLeft);
		Element boundLeftPattern = ParserARQ.parseElement("{" + boundLeft + "}");

		return QueryUtils.extractTriplePatterns(boundLeftPattern);
	}

	/**
	 * Find associations between context bindings related to the rule context, the rule left pattern and the right pattern of the transformation rule
	 * @param rule the transformation rule
	 * @param query the initial SPARQL query
	 * @param contextBinding the context bindings
	 * @param leftBinding the left bindings calculated for the initial query
	 * @return bindings for the rule left pattern
	 */
	private List<Triple> getBoundRightTriples(TransformationRule rule, Query query,
			HashMap<String, String> contextBinding, HashMap<String, String> leftBinding) {

		String boundRight = rule.getRight();

		for(Entry<String, String> entry : contextBinding.entrySet()) {
			boundRight = boundRight.replaceAll(entry.getKey().replaceAll("\\?", "\\\\?").replaceAll("\\$", "\\\\$"), 
					entry.getValue());
		}

		for(Entry<String, String> entry : leftBinding.entrySet()) {
			boundRight = boundRight.replaceAll(entry.getKey().replaceAll("\\?", "\\\\?").replaceAll("\\$", "\\\\$"), 
					entry.getValue());
		}

		logger.debug("BOUND RIGHT " + boundRight);
		Element boundRightPattern = ParserARQ.parseElement("{" + boundRight + "}");
		return QueryUtils.extractTriplePatterns(boundRightPattern);
	}


	/**
	 * Apply the transformation to the Jena Query object
	 * @param application the application with all information for applying the transformation
	 * @return a generated query as a Jena object
	 */
	private Query applyTransformation(RuleApplication application) {
		Query generatedQuery = application.getInitialQuery().cloneQuery();
		List<Triple> queryTriples = QueryUtils.extractTriplePatterns(application.getInitialQuery());

		ElementGroup fullGraphPattern = new ElementGroup();
		ElementTriplesBlock graphPattern = new ElementTriplesBlock();
		List<Triple> rightTriples = application.getRightTriples();		
		List<Triple> appLeftTriples = application.getLeftTriples();

		for(int j = 0; j < queryTriples.size(); j++) {	
			//We don't include triples part of the left graph pattern (as they are supposed to be replaced with right graph pattern)
			if(!appLeftTriples.contains(queryTriples.get(j))){
				graphPattern.addTriple(queryTriples.get(j));
			}

		}

		//Add all right graph pattern
		for(int i = 0 ; i < rightTriples.size() ; i++) {
			graphPattern.addTriple(rightTriples.get(i));
		}

		fullGraphPattern.addElement(graphPattern);
		//We also need to add all the filter part of the graph pattern
		List<ElementFilter> elementFilters = QueryUtils.extractFilters(application.getInitialQuery());

		for(ElementFilter filter : elementFilters) {
			fullGraphPattern.addElementFilter(filter);
		}

		generatedQuery.setQueryPattern(fullGraphPattern);

		return generatedQuery;
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
