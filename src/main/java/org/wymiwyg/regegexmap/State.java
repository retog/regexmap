package org.wymiwyg.regegexmap;

import java.util.HashSet;
import java.util.Set;

public class State {
	
	Set<Transition> transitions = new HashSet<Transition>();
	private boolean endState;

	public Set<Transition> getOutgoingTransitions() {
		return transitions;
	}

	//remove as set has same visibility
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
