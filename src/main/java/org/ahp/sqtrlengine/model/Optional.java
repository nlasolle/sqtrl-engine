package org.ahp.sqtrlengine.model;

import java.util.List;

/**
 * SPARQL OPTIONAL block
 * @author Nicolas Lasolle
 *
 */
public class Optional {
	private List<TriplePattern> triplePatterns;

	public List<TriplePattern> getTriplePatterns() {
		return triplePatterns;
	}

	public void setTriplePatterns(List<TriplePattern> triplePatterns) {
		this.triplePatterns = triplePatterns;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((triplePatterns == null) ? 0 : triplePatterns.hashCode());
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
		Optional other = (Optional) obj;
		if (triplePatterns == null) {
			if (other.triplePatterns != null)
				return false;
		} else if (!triplePatterns.equals(other.triplePatterns))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String result = "";
		
		for (TriplePattern triplePattern : triplePatterns) {
			result += "\t" + triplePattern + " .\n";
		}
		
		return result;
	}
	
	
	
}
