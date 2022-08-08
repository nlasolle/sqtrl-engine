package org.ahp.sqtrlengine.dao;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Interface to dialog with an RDF base by executing SPARQL queries using Jena API
 * @author Nicolas Lasolle
 *
 */
public class JenaWrapper {


	private RDFConnection conn;
	private QueryExecution qExec;
	private String endpoint;

	private static Logger logger = LogManager.getLogger(JenaWrapper.class);

	/**
	 * Creating JenaWrapper for a given endpoint
	 * @param endpoint SPARQL endpoint iri
	 */
	public JenaWrapper(String endpoint) {
		logger.info("Configuring Jena Wrapper for executing SPARQL queries over the endpoint <{}>", endpoint);
		conn = RDFConnectionFactory.connect(endpoint);
		this.endpoint = endpoint;
	}

	/**
	 * Should be called after iterating over the result of a query execution
	 */
	public void closeExecution() {
		qExec.close();
	}

	/**
	 * Execute a SELECT SPARQL query using a read only or update SPARQL endpoint
	 * @param queryString the SELECT SPARQL query as a string
	 * @return the results of the query
	 */
	public ResultSet executeSelectQuery(String queryString) {	
		Query query = QueryFactory.create(queryString) ;
		return executeSelectQuery(query);
	}


	/**
	 * Execute a SELECT SPARQL query using a read  only or update SPARQL endpoint
	 * @param queryString the SELECT SPARQL query
	 * @return the results of the query
	 */
	public ResultSet executeSelectQuery(Query query) {	
		qExec = conn.query(query) ;

		logger.info("Executing select SPARQL query:\n {}", query);

		return qExec.execSelect();
	}



	/**
	 * Execute a CONSTRUCT SPARQL query using a read only or update SPARQL endpoint
	 * @param queryString the SELECT SPARQL query as a String
	 * @return the results of the query
	 */
	public Model executeConstructQuery(String queryString) {
		Query query = QueryFactory.create(queryString) ;

		return executeConstruct(query);
	}

	/**
	 * Execute a CONSTRUCT SPARQL query using a read only or update SPARQL endpoint
	 * @param queryString the SELECT SPARQL query as a String
	 * @return the results of the query
	 */
	public Model executeConstruct(Query query) {
		qExec = conn.query(query) ;
		logger.info("Executing contruct SPARQL query:\n {}", query);
		return qExec.execConstruct();
	}

	/**
	 * Execute an update SPARQL query using an update SPARQL endpoint
	 * @param queryString the SPARQL query to update the graph
	 * @return the results of the query
	 */
	public void executeUpdateQuery(String queryString) {
		executeUpdateQuery(queryString, endpoint);
	}

	/**
	 * Execute an update SPARQL query using an update SPARQL endpoint
	 * @param queryString the SPARQL query to update the graph
	 * @param the SPARQL endpoint
	 * @return the results of the query
	 */
	public void executeUpdateQuery(String queryString, String endpoint) {

		logger.info("Executing update SPARQL query:\n {}", queryString);

		UpdateRequest request = UpdateFactory.create(queryString) ;
		UpdateProcessor processor = UpdateExecutionFactory.createRemote(request, endpoint);

		processor.execute();
	}

	/**
	 * Execute a select SPARQL query 
	 * @param queryString the SELECT SPARQL query 
	 * @param model the target graph
	 */

	public static ResultSet executeLocalSelectQuery(String queryString, Model model) {
		Query query = QueryFactory.create(queryString) ;

		QueryExecution qexec = QueryExecutionFactory.create(query, model);

		return qexec.execSelect();
	}

	/**
	 * Get the count of triples in the current database
	 * @return
	 */
	public int getCount() {

		logger.info("Counting triple for endpoint <{}>", endpoint);

		String query = "SELECT (COUNT(*) as ?c)\r\n" + 
				"WHERE{ \r\n" + 
				"     ?s ?p ?o.\r\n" + 
				"  }";

		ResultSet results = executeSelectQuery(query);

		return results.next().getLiteral("?c").getInt();
	}
}
