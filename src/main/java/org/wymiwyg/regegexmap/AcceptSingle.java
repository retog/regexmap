package org.wymiwyg.regegexmap;

public class AcceptSingle extends Transition {

	private char acceptedChar;

	public AcceptSingle(State target, char acceptedChar) {
		super(target);
		this.acceptedChar = acceptedChar;
	}

	public boolean accepts(char ch) {
		return ch == acceptedChar;
	}

	@Override
	void exclude(char ch) {
		throw new RuntimeException("Cannot exlude single char");
	}

	@Override
	public String toString() {
		return "transition acception only: '"+acceptedChar+"'.";
	}
	
	

}
