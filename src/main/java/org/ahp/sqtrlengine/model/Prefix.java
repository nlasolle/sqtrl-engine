package org.ahp.sqtrlengine.model;


/**
 * Represent a Semantic Web namespace with the associated abbreviation
 * @author Nicolas Lasolle
 *
 */
public class Prefix {

	private String namespace;
	private String abbreviation;

	public Prefix() {
		
	}
	
	public Prefix(String namespace, String abbreviation) {
		this.namespace = namespace;
		this.abbreviation = abbreviation;
	}
	
	
	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
		result = prime * result + ((abbreviation == null) ? 0 : abbreviation.hashCode());
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
		Prefix other = (Prefix) obj;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
		if (abbreviation == null) {
			if (other.abbreviation != null)
				return false;
		} else if (!abbreviation.equals(abbreviation))
			return false;
		return true;
	}

	@Override
	public String toString(){
		return "@prefix "+ abbreviation + ": <" + namespace + ">";
	}
	
}