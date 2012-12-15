package org.wymiwyg.regegexmap;

import java.util.HashSet;
import java.util.Set;

public class State {
	
	private Set<Transition> transitions = new HashSet<Transition>();
	private boolean endState;

	public Set<Transition> getOutgoingTransitions() {
		return transitions;
	}

	void addTransition(Transition transition) {
		transitions.add(transition);
		
	}

	void markAsEndState() {
		endState = true;
	}

	public boolean isEndState() {
		return endState;
	}

}
