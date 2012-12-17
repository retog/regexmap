package org.wymiwyg.regegexmap;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class Matcher {

	/**
	 * 
	 * @param state
	 * @param string
	 * @return true if all chars of strings are accepted and lead ton an EndState, false otherwise
	 */
	public static boolean match(State state, String string) {
		return matchToState(state, string) != null;
	}
	public static State matchToState(State state, String string) {
		try {
			return matchToState(state, new StringReader(string));
		} catch (IOException e) {
			throw new RuntimeException("IO reading from string", e);
		}
	}

	public static State matchToState(State state, Reader reader) throws IOException {
		int currentChar = reader.read();
		if (currentChar == -1) {
			if (state.isEndState()) {
				return state;
			}
		} else {
			for (Transition t : state.getOutgoingTransitions()) {
				if (t.accepts((char) currentChar)) {
					//We deal with DFA so only one transitions accepts
					return matchToState(t.getTarget(), reader);
				}
			}
		}
		return null;
		
	}
}
