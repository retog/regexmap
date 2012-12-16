package org.wymiwyg.regegexmap;

public abstract class Transition {

	private State state;
	
	Transition(State state) {
		super();
		this.state = state;
	}

	public abstract boolean accepts(char ch);

	public State getTarget() {
		return state;
	}

	abstract void exclude(char ch);

}
