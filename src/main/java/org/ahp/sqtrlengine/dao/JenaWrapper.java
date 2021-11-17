package org.ahp.sqtrlengine.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Logger;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFReaderI;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;

/**
 * Interface to dialog with an RDF base using Jena API
 * @author Nicolas Lasolle
 *
 */
public class JenaWrapper {

	//RDF Model containing all the triples of the database (multiple files)
	Model model = ModelFactory.createDefaultModel();

	Model inferenceModel = ModelFactory.createDefaultModel();

	//This model is used to store the current model
	Model updateModel = ModelFactory.createDefaultModel(); 

	String updateFile;
	
	RDFConnection conn;
	QueryExecution qExec;
	private static Logger LOGGER = Logger.getLogger(JenaWrapper.class.getName());
	
	/**
	 * 
	 * @param endpoint sparql endpoint iri
	 */
	public JenaWrapper(String endpoint) {
		conn = RDFConnectionFactory.connect(endpoint);
	}
	
	public void loadGraph(String file) {

		//filesList should never be empty
		if(file.isBlank()) {
			throw new IllegalArgumentException("No RDF file provided.");
		}

		//Construct the model by loading all files in the RDF directory (which is a property to be configured in the .yml file)
		RDFReaderI localReader = model.getReader("TURTLE");
		localReader.setProperty("WARN_REDEFINITION_OF_ID","EM_IGNORE");

		try {
			localReader.read(model, new FileInputStream(file), file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}


		inferenceModel = ModelFactory.createRDFSModel(model);

	}

	public void loadGraph(List<String> files) {

		//filesList should never be empty
		if(files.isEmpty()) {
			throw new IllegalArgumentException("There is no element in the list of RDF files.");
		}

		//Construct the model by loading all files in the RDF directory (which is a property to be configured in the .yml file)
		RDFReaderI localReader = model.getReader("TURTLE");
		localReader.setProperty("WARN_REDEFINITION_OF_ID","EM_IGNORE");

		InputStream inputStream = null;

		for(String file : files.subList(1, files.size())) {
			try {
				inputStream = new FileInputStream(file);
				localReader.read(model, inputStream, file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		inferenceModel = ModelFactory.createRDFSModel(model);

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
	 * Execute a local update SPARQL query (DELETE, INSERT)
	 * @param querythe SPARQL query to update the dataset	 
	 * @throws FileNotFoundException 
	 */
	public void executeLocalUpdateQuery(String query) throws FileNotFoundException {

		//Update the model that is used by this application
		UpdateAction.parseExecute(query, model);
		UpdateAction.parseExecute(query, updateModel);
		UpdateAction.parseExecute(query, inferenceModel);

		//Update the file by writing the new model value
		OutputStream out = new FileOutputStream(new File(updateFile));
		updateModel.write(out, "TURTLE");

	}


	/**
	 * Execute a SELECT SPARQL query using a read SPARQL endpoint
	 * @param queryString the SELECT SPARQL query
	 * @param endpoint the URL of SPARQL endpoint
	 * @return the results of the query
	 */
	public ResultSet executeRemoteSelectQuery(String queryString, String endpoint) {	
		Query query = QueryFactory.create(queryString) ;
		qExec = conn.query(query) ;
		ResultSet execResults = qExec.execSelect();
		return execResults; 

	}
	
	/**
	 * Should be called after iterating over the result of a query execution
	 */
	public void closeExecution() {
		qExec.close();
	}

	/**
	 * Execute a SELECT SPARQL query using a read SPARQL endpoint
	 * @param queryString the SELECT SPARQL query
	 * @param endpoint the URL of SPARQL endpoint
	 * @return the results of the query
	 */
	public Model executeRemoteConstructQuery(String queryString, String endpoint) {
		RDFConnection conn = RDFConnectionFactory.connect(endpoint);	
		Query query = QueryFactory.create(queryString) ;

		qExec = conn.query(query) ;
		Model result = qExec.execConstruct();
		return result; 

	}
	
	/**
	 * Execute an update SPARQL query using a SPARQL update endpoint
	 * @param queryString the SPARQL query to update the dataset
	 * @param endpoint the URL of update SPARQL endpoint
	 * @return the results of the query
	 */
	public void executeRemoteUpdateQuery(String queryString, String endpoint) {
		LOGGER.info("--- Update query ---");
		LOGGER.info(queryString);
		
		UpdateRequest request = UpdateFactory.create(queryString) ;
		UpdateProcessor processor = UpdateExecutionFactory.createRemote(request, endpoint);
		
		processor.execute();
	}

	/**
	 * Get the count of triples in the current database
	 * @return
	 */
	public int getCount() {
		int count;

		String query = "SELECT (COUNT(*) as ?c)\r\n" + 
				"WHERE{ \r\n" + 
				"     ?s ?p ?o.\r\n" + 
				"  }";

		ResultSet results = executeLocalSelectQuery(query, model);
		count = results.next().getLiteral("?c").getInt();

		return count;
	}
}
