package org.ahp.sqtrlengine.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ahp.sqtrlengine.dao.JenaWrapper;
import org.ahp.sqtrlengine.model.RuleApplication;
import org.ahp.sqtrlengine.model.TransformationRule;
import org.ahp.sqtrlengine.utils.QueryUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.Binding;
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

	private JenaWrapper wrapper;
	private static final Logger logger = LogManager.getLogger(RuleApplyer.class);

	public RuleApplyer(String endpoint) {
		wrapper = new JenaWrapper(endpoint);
	}

	public List<RuleApplication> getRuleApplications(Query query, TransformationRule rule, String sparqlEndpoint){
		logger.debug("Searching for applications for rule {rule.getIri()}");

		List<RuleApplication> applications = new ArrayList<>(); 

		logger.info(rule.getContext() == null);
		logger.info(rule.getContext().isEmpty());
		//First situation, rule context is not empty
		if(!rule.getContext().isEmpty()) {
		
			List<HashMap<String, String>> contextBindingsList = getContextBindings(rule, sparqlEndpoint);

			if(contextBindingsList.isEmpty()) {
				logger.debug("No application for rule {rule.getIri} context field and for the SPARQL endpoint {sparqlEndpoint}");
				return applications;
			}

			for(HashMap<String, String> contextBindings : contextBindingsList) {
				//retrieveLeftBindings(rule, application);
				List<RuleApplication> tempApplications = retrieveLeftBindingsBis(rule, query, contextBindings);

				if(tempApplications.isEmpty()) {
					continue;
				}

				for(RuleApplication application : tempApplications) {
					getBoundRightTriples(rule, application);
					applyTransformation(application);
					generateExplanation(rule, application, sparqlEndpoint);
				}

				applications.addAll(tempApplications);
			}

		} 
		//Empty rule context
		else {
			//retrieveLeftBindings(rule, application);
			applications = retrieveLeftBindingsBis(rule, query, new HashMap<String, String>());
			if(applications.isEmpty()) {
				return applications;
			}

			for(RuleApplication application : applications) {
				getBoundRightTriples(rule, application);
				applyTransformation(application);
				generateExplanation(rule, application, sparqlEndpoint);
			}

		}
		return applications;
	}

	/**
	 * Generate a human-readable explanation from the bindings 
	 * @param rule the transformation rule
	 * @param application full set rule application object with all bindings known
	 */
	private void generateExplanation(TransformationRule rule, RuleApplication application, String sparqlEndpoint) {
		String explanation = rule.getExplanation();

		for(Entry<String, String> entry : application.getContextBinding().entrySet()) {
			explanation = explanation.replaceAll(entry.getKey().replaceAll("\\?", "\\\\?").replaceAll("\\$", "\\\\$"), 
					entry.getValue());
		}

		for(Entry<String, String> entry : application.getLeftBinding().entrySet()) {
			explanation = explanation.replaceAll(entry.getKey().replaceAll("\\?", "\\\\?").replaceAll("\\$", "\\\\$"), 
					entry.getValue());
		}

		//Now, the objective is to  identify all the IRI which are part of the explanation
		//A SPARQL query is generated to retrieve their potential labels 

		String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"PREFIX dcterms: <http://purl.org/dc/terms/>\n" + 
				"SELECT";

		String varsPattern = "";
		String graphPattern = "";

		Pattern pattern = Pattern.compile("(http|https):/[^\\)\\>\\s]*");
		Matcher matcher = pattern.matcher(explanation);
	
		HashMap<String, String> varsAssociations = new HashMap<>();

		int i = 0;
		while (matcher.find()) {
			varsAssociations.put("?rdfs" + i, matcher.group());
			varsAssociations.put("?dc" + i, matcher.group());

			varsPattern += " (STR(?r" + i + ") AS ?rdfs" + i + ") (STR(?d" +i + ") AS ?dc" + i +")";
			graphPattern += "OPTIONAL {<" + matcher.group() + "> rdfs:label ?r" + i + " .\n"
					+ "FILTER ( LANG(?r" + i + ") = \"en\" || LANG(?r" + i + ") = \"\")} .\n"
					+ "OPTIONAL {<" + matcher.group() + "> dcterms:title ?d" + i + " .\n"
					+ "FILTER ( LANG(?d" + i + ") = \"en\" || LANG(?d" + i + ") = \"\")} .\n";
			i++;
		}

		//If no IRI exists in the explanation field, only return the current explanation which is already human-readable
		if(varsAssociations.isEmpty()) {
			application.setExplanation(explanation);
			return;
		}

		query+= varsPattern + " {\n" + graphPattern + "}";
		ResultSet results = wrapper.executeRemoteSelectQuery(query);

		//Save the bindings with the values for each variable
		if( results.hasNext() ){
			QuerySolution solution = results.next();
			for (Entry<String, String> association : varsAssociations.entrySet()) {

				if(solution.get(association.getKey()) != null) {
					String label = solution.get(association.getKey()).toString();

					explanation = explanation.replaceAll(association.getValue(), label);
				} 
			}
		}
		
		wrapper.closeExecution();
		application.setExplanation(explanation);	
	}

	/**
	 * Generate and execute a SPARQL to find context bindings for the given transformation rule and RDFS base
	 * @param rule a SQTRL rule
	 * @param sparqlEndpoint
	 */
	public List<HashMap<String, String>> getContextBindings(TransformationRule rule, String sparqlEndpoint) {
		List<HashMap<String, String>> bindings = new ArrayList<>();

		String query = generateContextQuery(rule);

		List<String> variables = getVariablesFromString(rule.getContext());

		//Execute the query over the SPARQL endpoint
		ResultSet results = wrapper.executeRemoteSelectQuery(query);

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

		wrapper.closeExecution();
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
	 * Match bound left to query for transformation for a given context binding (based on the RDF graph)
	 * @param rule a SQTRL rule
	 * @param application the currently in construction Rule application with given the contextBinding and the iitial SPARQL query
	 */
	public void retrieveLeftBindings(TransformationRule rule, RuleApplication application) {
		List<Triple> queryTriples = QueryUtils.extractTriplePatterns(application.getInitialQuery());

		HashMap<String, String> contextBinding = (HashMap<String, String>) application.getContextBinding();

		//First, we construct the left triples by assigning context binding variable values
		List<Triple> leftTriples = getBoundLeftTriples(rule, application.getInitialQuery(), contextBinding);

		/*We need to find associations between 
		 * context bindings related to the rule context and 
		 * the left pattern of the transformation rule */
		List<Triple> boundLeftTriples = new ArrayList<>();

		HashMap<String, String> leftBindings = new HashMap<>();

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
						break;
					}
					continue;
				} else {
					subject = queryTriple.getSubject();
				}

				if(leftTriple.getPredicate().isVariable()) {
					predicate = queryTriple.getPredicate();
					if(predicate.isURI()) {
						tempBinding.put(leftTriple.getPredicate().toString(), "<" + predicate.toString() + ">");
					} else {
						tempBinding.put(leftTriple.getPredicate().toString(), predicate.toString());
					}
				} else if(!leftTriple.getPredicate().equals(queryTriple.getPredicate())){
					if(i == queryTriples.size()-1) {
						break;
					}
					continue;
				} else {
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
						break;
					}

					continue;
				} else {
					object = queryTriple.getObject();
				}

				//This line is reached if a match has been found. 
				//It is possible to construct the final bound left triple and switch to the next one if necessary
				boundLeftTriples.add(new Triple(subject, predicate, object));					
				leftBindings.putAll(tempBinding); 
			}
		}

		application.setLeftBinding(leftBindings);
		application.setLeftTriples(boundLeftTriples);

	}

	public List<RuleApplication> retrieveLeftBindingsBis (TransformationRule rule, Query query, HashMap<String, String> contextBindings) {
	
		List<Triple> queryTriples = QueryUtils.extractTriplePatterns(query);
		List<RuleApplication> applications = new ArrayList<>();
		RuleApplication application;
		//First, we construct the left triples by assigning context binding variable values
		List<Triple> leftTriples = getBoundLeftTriples(rule, query, contextBindings);

		/*We need to find associations between 
		 * context bindings related to the rule context and 
		 * the left pattern of the transformation rule */
		List<Triple> boundLeftTriples = new ArrayList<>();

		HashMap<String, String> leftBindings = new HashMap<>();

		Model model = ModelFactory.createDefaultModel();

		for(Triple queryTriple : queryTriples) {
			model.getGraph().add(queryTriple);

		}

		String leftAsString = "";

		for(Triple leftTriple : leftTriples) {
			leftAsString += leftTriple + "\n";
		}

		//Construct and execute the SPARQL query for querying the input SPARQL query body
		String bodyQuery = "SELECT "; 

		List<String> variables = getVariablesFromString(leftAsString);

		//In this situation, no variable remains on the left pattern (all resolved with context binding)
		//Only one rule application exists for the given rule and the context binding
		if(variables.isEmpty()) {
			RuleApplication singleApplication = new RuleApplication();
			singleApplication.setRuleIri(rule.getIri());
			singleApplication.setInitialQuery(query);
			singleApplication.setContextBinding(contextBindings);
			singleApplication.setLeftBinding(leftBindings);
			singleApplication.setLeftTriples(boundLeftTriples);
			applications.add(singleApplication);
			return applications;
		}

		for(String var : variables) {
			bodyQuery += var + " ";
		}

		bodyQuery += "WHERE { \n";

		for(Triple leftTriple : leftTriples) {
			String subject, predicate, object;

			if(leftTriple.getSubject().isURI()) {
				subject = "<" + leftTriple.getSubject() + ">";
			} else {
				subject = leftTriple.getSubject().toString();
			}

			if(leftTriple.getPredicate().isURI()) {
				predicate = "<" + leftTriple.getPredicate() + ">";
			} else {
				predicate = leftTriple.getPredicate().toString();
			}

			if(leftTriple.getObject().isURI()) {
				object = "<" + leftTriple.getObject() + ">";
			} else {
				object = leftTriple.getObject().toString();
			}

			bodyQuery += subject + " "
					+ predicate + " "
					+ object + " .\n";
		}

		bodyQuery += "}";

		ResultSet results = JenaWrapper.executeLocalSelectQuery(bodyQuery, model);

		while(results.hasNext()) {
			application = new RuleApplication();
			Binding binding = results.nextBinding();
			leftBindings = new HashMap<String, String>();
			boundLeftTriples = new ArrayList<>();
			Iterator<Var> vars = binding.vars();
			while (vars.hasNext()) {
				Var jenaVar = vars.next();
				String bindingValue = binding.get(jenaVar).toString();

				leftBindings.put(jenaVar.toString(), bindingValue);
			}



			for(Triple leftTriple : leftTriples) {
				Node subject = leftTriple.getSubject();
				Node predicate = leftTriple.getPredicate();
				Node object = leftTriple.getObject();

				for(Entry<String, String> entry : leftBindings.entrySet()) {

					if(subject.toString().equals(entry.getKey())) {
						subject = NodeFactory.createURI(entry.getValue());
					}

					if(predicate.toString().equals(entry.getKey())) {
						predicate = NodeFactory.createURI(entry.getValue());
					}


					if(object.toString().equals(entry.getKey())) {
						object = NodeFactory.createURI(entry.getValue());
					}

				}

				boundLeftTriples.add(new Triple(subject, predicate, object));

			}

			application.setRuleIri(rule.getIri());
			application.setInitialQuery(query);
			application.setContextBinding(contextBindings);
			application.setLeftBinding(leftBindings);
			application.setLeftTriples(boundLeftTriples);
			applications.add(application);

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

		for(Entry<String, String> entry : contextBinding.entrySet()) {
			boundLeft = boundLeft.replaceAll(entry.getKey().replaceAll("\\?", "\\\\?").replaceAll("\\$", "\\\\$"), 
					entry.getValue());
		}

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
	private void getBoundRightTriples(TransformationRule rule, RuleApplication application) {

		String boundRight = rule.getRight();

		for(Entry<String, String> entry : application.getContextBinding().entrySet()) {
			boundRight = boundRight.replaceAll(entry.getKey().replaceAll("\\?", "\\\\?").replaceAll("\\$", "\\\\$"), 
					entry.getValue());
		}

		for(Entry<String, String> entry : application.getLeftBinding().entrySet()) {
			String value = entry.getValue();
			if(value.startsWith("http:") || value.startsWith("https:")) {
				value = "<" + value + ">";
			}

			boundRight = boundRight.replaceAll(entry.getKey().replaceAll("\\?", "\\\\?").replaceAll("\\$", "\\\\$"), 
					value);
		}


		Element boundRightPattern = ParserARQ.parseElement("{" + boundRight + "}");
		List<Triple> boundRightTriples = QueryUtils.extractTriplePatterns(boundRightPattern);
		application.setRightTriples(boundRightTriples);

	}


	/**
	 * Apply the transformation to the Jena Query object
	 * @param application the application with all information for applying the transformation
	 */
	public void applyTransformation(RuleApplication application) {
		Query generatedQuery = application.getInitialQuery().cloneQuery();
		List<Triple> queryTriples = QueryUtils.extractTriplePatterns(application.getInitialQuery());

		ElementGroup fullGraphPattern = new ElementGroup();
		ElementTriplesBlock graphPattern = new ElementTriplesBlock();
		List<Triple> rightTriples = application.getRightTriples();		
		List<Triple> appLeftTriples = application.getLeftTriples();

		first:
			for(int j = 0; j < queryTriples.size(); j++) {	
				//We don't include triples part of the left graph pattern (as they are supposed to be replaced with right graph pattern)
				Triple qt = queryTriples.get(j);

				for(Triple t : appLeftTriples) {
					if(t.getSubject().toString().equals(qt.getSubject().toString()) &&
							t.getPredicate().toString().equals(qt.getPredicate().toString()) &&
							t.getObject().toString().equals(qt.getObject().toString())
							){ 
						continue first;
					}
				}

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
		generatedQuery = QueryUtils.parseQuery(generatedQuery.toString());
		application.setGeneratedQuery(generatedQuery);
	}

	/**
	 * Retrieve all variables that occur in a String
	 * @param context SQTRL rule context graph pattern
	 * @return the variables as a String List
	 */
	private List<String> getVariablesFromString(String query) {
		Pattern pattern = Pattern.compile("(\\?|\\$)[^\\s)({}]+");
		Matcher matcher = pattern.matcher(query);

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

				generatedQuery += "FILTER NOT EXISTS { "
						+ exception
						+ " }\n";
			}
		}

		generatedQuery += "}";

		return generatedQuery;
	}
}
