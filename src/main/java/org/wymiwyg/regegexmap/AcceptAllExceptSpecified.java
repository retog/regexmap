package org.wymiwyg.regegexmap;

import java.util.HashSet;
import java.util.Set;

public class AcceptAllExceptSpecified extends Transition {

	private Set<Character> exceptions = new HashSet<Character>();
	
	AcceptAllExceptSpecified(State state) {
		super(state);
	}

	@Override
	public boolean accepts(char ch) {
		return !exceptions.contains(ch);
	}

	@Override
	void exclude(char ch) {
		exceptions.add(ch);
	}

}
