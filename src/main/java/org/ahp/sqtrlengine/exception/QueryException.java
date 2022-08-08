package org.ahp.sqtrlengine.exception;

import java.io.Serializable;

/**
 * Should be thrown when there is a problem with the validity of an input SPARQL query
 * @author Nicolas Lasolle
 *
 */
public class QueryException extends Exception implements Serializable {

	private static final long serialVersionUID = -8795827082775457692L;

	public QueryException(){
		super();
	}
	
	public QueryException(String message){
		super(message);
	}
}
