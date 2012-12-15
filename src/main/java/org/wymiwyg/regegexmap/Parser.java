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
		int currentChar = in.read();
		if (currentChar == -1) {
			return new EndState();
		} else {
			State result = new State();
			int nextChar = in.read();
			if (nextChar != -1) {
				if (nextChar == '*') {
					throw new UnsupportedOperationException();
				} else {
					in.unread(nextChar);
				}
			}
			if (currentChar == '.') {
				result.addTransition(new AcceptAny(parse(in)));
			} else {
				result.addTransition(new AcceptSingle(parse(in), (char) currentChar));
			}
			return result;
		}
	}

}
