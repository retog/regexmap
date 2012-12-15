package org.wymiwyg.regegexmap;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

public class Parser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//State startSparse("hello");

	}

	public static State parse(String string) {
		try {
			return parse(new PushbackReader(new StringReader(string), 1));
		} catch (IOException e) {
			throw new RuntimeException("IO reading from string", e);
		}
	}

	private static State parse(PushbackReader in) throws IOException {
		State result = new State();
		parseInto(result, in);
		return result;
	}

	private static void parseInto(State result, PushbackReader in) throws IOException {
		int currentChar = in.read();
		if (currentChar == -1) {
			result.markAsEndState();
		} else {	
			int nextChar = in.read();
			State targetState;
			if (nextChar != -1) {
				if (nextChar == '*') {
					targetState = result;
				} else {
					in.unread(nextChar);
					targetState = new State();
				}
			} else {
				targetState = new State();
			}
			parseInto(targetState, in);
			if (currentChar == '.') {
				result.addTransition(new AcceptAny(targetState));
			} else {
				result.addTransition(new AcceptSingle(targetState, (char) currentChar));
			}
		}
	}

}
