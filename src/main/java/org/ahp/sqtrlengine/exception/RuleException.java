package org.ahp.sqtrlengine.exception;

import java.io.Serializable;

/**
 * Should be thrown when there is a problem with the validity of a transformation rule
 * @author Nicolas Lasolle
 *
 */
public class RuleException extends Exception implements Serializable {
	
	private static final long serialVersionUID = -6222158049092800194L;

	public RuleException(){
		super();
	}
	
	public RuleException(String message){
		super(message);
	}
}
