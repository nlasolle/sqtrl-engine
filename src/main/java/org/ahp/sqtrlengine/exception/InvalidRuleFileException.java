package org.ahp.sqtrlengine.exception;

import java.io.Serializable;

/**
 * Should be thrown when there is a problem with the validity of a transformation rule file.
 * @author Nicolas Lasolle
 *
 */
public class InvalidRuleFileException extends Exception implements Serializable {

	private static final long serialVersionUID = -7549128357189246081L;

	public InvalidRuleFileException(){
		super();
	}
	
	public InvalidRuleFileException(String message){
		super(message);
	}
}
