package org.ahp.sqtrlengine.model;

/**
 * SPARQL term
 * @author Nicolas Lasolle
 *
 */
public class Term {

	public enum TermType {
	   IRI,
	   VARIABLE,
	   LITERAL
	}
	
	private String value; //Value of the term (the string extracted from the SPARQL query)
	private TermType type;
	
	public  Term(String value, TermType type) {
		this.value = value;
		this.type = type;
	}
	
	public boolean isIRI() {
		return type.equals(TermType.IRI);
	}
	
	public boolean isVariable() {
		return type.equals(TermType.VARIABLE);
	}
	
	public boolean isLiteral() {
		return type.equals(TermType.LITERAL);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public TermType getType() {
		return type;
	}

	public void setType(TermType type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Term other = (Term) obj;
		if (type != other.type)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return value;
	}
	
}
