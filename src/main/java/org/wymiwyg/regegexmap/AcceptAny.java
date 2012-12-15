package org.wymiwyg.regegexmap;

public class AcceptAny extends Transition {

	AcceptAny(State state) {
		super(state);
	}

	@Override
	public boolean accepts(char ch) {
		return true;
	}

}
