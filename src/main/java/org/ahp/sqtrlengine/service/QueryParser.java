package org.ahp.sqtrlengine.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.ahp.sqtrlengine.model.Filter;
import org.ahp.sqtrlengine.model.GraphPattern;
import org.ahp.sqtrlengine.model.Query;
import org.ahp.sqtrlengine.model.SelectClause;
import org.ahp.sqtrlengine.model.TriplePattern;
import org.ahp.sqtrlengine.model.WhereClause;
import org.ahp.sqtrlengine.utils.QueryUtils;
import org.apache.jena.graph.Triple;

/**
 * Parse a SPARQL query and save it as an object
 * @author Nicolas Lasolle
 *
 */
public class QueryParser {

	/**
	 * Is the SPARQL query under valid syntax ?
	 * @param queryString the query as an input string
	 * @return 
	 */
	public static boolean isQueryValid(String queryString) {
		return true;
	}
	
	/**
	 * Construct the query Java representation
	 * @param queryString the query as an input string
	 * @return an object representing the query
	 */
	public static Query parseQuery(String queryString) {
		Query query = new Query();
		query.setSelectClause(parseSelectClause(queryString));
		query.setWhereClause(parseWhereClause(queryString));
		return query;
	}
	
	public static SelectClause parseSelectClause (String queryString) {
		String selectString = queryString.substring(queryString.indexOf("SELECT"), queryString.indexOf("WHERE"));
		
		String[] values = Pattern.compile("\\?\\S+|\\(.*\\)")
                .matcher(selectString)
                .results()
                .map(MatchResult::group)
                .toArray(String[]::new);
		
		List<String> vars = new ArrayList<String>();
		List<String> expressions = new ArrayList<String>();
		
		for(String value : values) {
			//System.out.println("value " + value);
			if (value.startsWith("(")) {
				expressions.add(value);
			} else {
				vars.add(value);
			}
		}
	
		SelectClause selectClause = new SelectClause();
		selectClause.setDistinct(queryString.contains("\\s DISTINCT \\s" ));
		selectClause.setReduced(queryString.contains("\\s REDUCED \\s" ));
		selectClause.setVars(vars);
		selectClause.setExpressions(expressions);
		
		return selectClause;
	}
	
	public static WhereClause parseWhereClause (String queryString) {
		String whereString = queryString.substring(queryString.indexOf("{"), queryString.lastIndexOf("}"));
		GraphPattern graphPattern = new GraphPattern();
		List<Filter> filters = new ArrayList<Filter>();
		List<TriplePattern> triplePatterns = new ArrayList<TriplePattern>();
		
		String[] values = whereString.split("\\.");
		
		for(String value : values) {
			
			if (value.contains("FILTER")) {
				Filter filter = new Filter();
				filter.setExpression("");
				filters.add(filter);
			} else {
				
				TriplePattern triplePattern = new TriplePattern();
				triplePatterns.add(triplePattern);
			}
		}
		
		List<Triple> triples = QueryUtils.extractTriplePatterns(QueryUtils.parseQuery(queryString));
		for(Triple t : triples) {
			System.out.println(t);
		}
		
		QueryUtils.extractFilters(QueryUtils.parseQuery(queryString));
		WhereClause whereClause = new WhereClause();
		whereClause.setGraphPattern(graphPattern);
		
		return whereClause;
	}
	
}
