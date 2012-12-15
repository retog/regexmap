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
		try {
			return match(state, new StringReader(string));
		} catch (IOException e) {
			throw new RuntimeException("IO reading from string", e);
		}
	}

	public static boolean match(State state, Reader reader) throws IOException {
		int currentChar = reader.read();
		if (currentChar == -1) {
			return state.isEndState();
		} else {
			for (Transition t : state.getOutgoingTransitions()) {
				if (t.accepts((char) currentChar)) {
					//We deal with DFA so only one transitions accepts
					return match(t.getTarget(), reader);
				}
			}
			return false;
		}
		
	}
}
