package org.ahp.sqtrlengine.exception;

import java.io.IOException;

public class InvalidFileTypeException extends IOException {
	
	private static final long serialVersionUID = -1653085439688698198L;

	public InvalidFileTypeException(String path, String acceptedTypeMask){
        super(String.format(
            "File type '{0}' does not fall within the expected range: '{1}'", 
            path, 
            acceptedTypeMask));
    }
}