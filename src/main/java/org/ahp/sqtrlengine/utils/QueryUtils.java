package org.ahp.sqtrlengine.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.ahp.sqtrlengine.exception.QueryException;
import org.ahp.sqtrlengine.model.Prefix;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;

/**
 * Query manipulation using Jena libs
 * @author Nicolas Lasolle
 *
 */
public class QueryUtils {

	/**
	 * Return a Jena Query representation based on a query string
	 * @param queryString the input query as a string
	 * @return a Query object
	 */
	public static Query parseQuery(String queryString) throws QueryException {
		if(queryString == null || queryString.isBlank()) {
			throw new QueryException("Null or blank SPARQL query string input.");
		}
		
		Query query;
		try {
			query =  QueryFactory.create(queryString);
		} catch (Exception e) {
			throw new QueryException(e.getMessage());
		}

		return query;
	}


	/**
	 * Extract requested var (within SELECT clause)
	 * @return a list of variables
	 */
	public static List<Var> extractSelectVariables(Query query) {
		return query.getProjectVars();
	}

	/**
	 * Extract triple patterns from a query
	 * @return
	 */
	public static List<Triple>  extractTriplePatterns(Query query) {
		final List<Triple> triples = new ArrayList<>();

		ElementWalker.walk(query.getQueryPattern(),
				new ElementVisitorBase() {

			@Override
			public void visit(ElementPathBlock el) {
				ListIterator<TriplePath> triplesIterator = el.getPattern().iterator();

				for( ;  triplesIterator.hasNext() ;) {
					TriplePath path = triplesIterator.next();

					triples.add(path.asTriple());
				}
			}});

		return triples;
	}

	/**
	 * Extract triples patterns from an element
	 * @return
	 */
	public static List<Triple>  extractTriplePatterns(Element element) {
		final List<Triple> triples = new ArrayList<>();

		ElementWalker.walk(element,
				new ElementVisitorBase() {

			@Override
			public void visit(ElementPathBlock el) {
				ListIterator<TriplePath> triplesIterator = el.getPattern().iterator();

				for( ;  triplesIterator.hasNext() ;) {
					TriplePath path = triplesIterator.next();

					triples.add(path.asTriple());
				}
			}});

		return triples;
	}

	public static List<ElementFilter> extractFilters(Query query) {
		List<ElementFilter> filters = new ArrayList<>();

		ElementWalker.walk(query.getQueryPattern(),
				new ElementVisitorBase() {

			@Override
			public void visit(ElementFilter el) {
				filters.add(el);
			}});

		return filters;
	}

	/**
	 * Simple check for query equivalence (triple patterns list and filters)
	 * @param q1 first SPARQL query
	 * @param q2 second SPARQL query
	 * @return true if the queries are equivalent
	 */
	public static boolean equivalent(Query q1, Query q2) {
		List<Triple> tp1 = QueryUtils.extractTriplePatterns(q1);
		List<Triple> tp2 = QueryUtils.extractTriplePatterns(q2);

		for(Triple t : tp1) {
			if(!tp2.contains(t)) {
				return false;
			}
		}

		List<ElementFilter> fp1 = QueryUtils.extractFilters(q1);
		List<ElementFilter> fp2 = QueryUtils.extractFilters(q1);

		for(ElementFilter f : fp1) {
			if(!fp2.contains(f)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Indicate if a string representing a graph pattern is valid
	 * @param triplePattern the triple pattern as a string
	 * @return true if the pattern is valid
	 */
	public static boolean isGraphPatternValid(String pattern, List<Prefix> prefixes) {

		if (pattern == null || pattern.isBlank()) {
			return false;
		}

		/* We use the Jena query parser to check the given pattern.
		   This is not ideal, we should rather find a Jena set
		    of functions to perform the check directly on the pattern */

		String queryString = "";

		for(Prefix p : prefixes) {
			queryString+= "PREFIX " + p.getAbbreviation() + ": <" + p.getNamespace() + ">\n";
		}

		queryString += "\nSELECT * WHERE {\n\t" + pattern + "\n}";

		System.out.println("QUERY STRING " + queryString) ;
		try {
			QueryUtils.parseQuery(queryString);
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
