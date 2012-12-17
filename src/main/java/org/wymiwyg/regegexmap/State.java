package org.wymiwyg.regegexmap;

import java.util.HashSet;
import java.util.Set;

public class State {
	
	int id;

	private boolean endState;
	private TransitionManager tm;
	
	State(TransitionManager tm) {
		this.tm = tm;
		id = tm.getStateId();
	}

	public Set<Transition> getOutgoingTransitions() {
		return tm.getTransitionsFrom(this);
	}


	void markAsEndState() {
		endState = true;
	}

	public boolean isEndState() {
		return endState;
	}

	@Override
	public String toString() {
		return "[State "+id+"]";
	}
	
	

}
