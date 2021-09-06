package org.ahp.sqtrlengine.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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
 * @author lasolle1
 *
 */
public class QueryUtils {

	/**
	 * Return a Jena Query representation based on a query string
	 * @param queryString the input query as a string
	 * @return a Query object
	 */
	public static Query parseQuery(String queryString) {
		return QueryFactory.create(queryString);
	}
	
	
	/**
	 * Extract requested var (within SELECT clause)
	 * @return
	 */
	public static List<Var> extractSelectVariables(Query query) {

		System.out.println(query.getGroupBy());
		System.out.println(query.getLimit());
		System.out.println(query.isDistinct());
		System.out.println("VARS " + query.getResultVars());
		return query.getProjectVars();
	}
	
	/**
	 * Extract triples patterns from a query
	 * @return
	 */
	public static List<Triple>  extractTriplePatterns(Query query) {
		final List<Triple> triples = new ArrayList<Triple>();


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
		final List<Triple> triples = new ArrayList<Triple>();


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
		List<ElementFilter> filters = new ArrayList<ElementFilter>();
		
		ElementWalker.walk(query.getQueryPattern(),
				new ElementVisitorBase() {

			@Override
			public void visit(ElementFilter el) {
				filters.add(el);
			}});
		
		return filters;
	}


}
