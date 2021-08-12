package org.ahp.sqtrlengine.model;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

/**
 * SPARQL query prologue
 * @author Nicolas Lasolle
 *
 */
public class Prologue {
	private String base; //The base over which the query should be executed
	private List<Prefix> prefixes; //List of prefixed that can be used in the SPARQL query
	
	public String getBase() {
		return base;
	}
	public void setBase(String base) {
		this.base = base;
	}
	public List<Prefix> getPrefixes() {
		return prefixes;
	}
	public void setPrefixes(List<Prefix> prefixes) {
		this.prefixes = prefixes;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((base == null) ? 0 : base.hashCode());
		result = prime * result + ((prefixes == null) ? 0 : prefixes.hashCode());
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
		Prologue other = (Prologue) obj;
		if (base == null) {
			if (other.base != null)
				return false;
		} else if (!base.equals(other.base))
			return false;
		if (prefixes == null) {
			if (other.prefixes != null)
				return false;
		} else if (!CollectionUtils.isEqualCollection(prefixes, other.prefixes))
			return false;
		return true;
	}
	@Override
	public String toString() {
		String result = " ";
		
		if(base!= null && !base.isBlank()) {
			result += "BASE " + base + "\n";
		}
		
		for (Prefix prefix : prefixes) {
			result += "PREFIX " + prefix.getPrefix() + " <" + prefix.getNamespace() + ">\n";
		}
		
		return result;
	}
	
	
	
	
}
