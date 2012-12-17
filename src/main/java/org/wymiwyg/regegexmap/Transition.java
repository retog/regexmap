package org.wymiwyg.regegexmap;

import java.util.Set;

public abstract class Transition {

	private State target;
	
	Transition(State target) {
		super();
		this.target = target;
	}

	public abstract boolean accepts(char ch);

	public State getTarget() {
		return target;
	}

	abstract boolean isWildCard();

	/*return null if wildcard
	 * 
	 */
	public abstract Set<Character> getAcceptedChars();

}
