package org.wymiwyg.regegexmap;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

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

	private static void parseInto(State state, PushbackReader in) throws IOException {
		int currentChar = in.read();
		if (currentChar == -1) {
			state.markAsEndState();
		} else {	
			int nextChar = in.read();
			State targetState;
			switch (nextChar) {
				case -1: targetState = new State(); break;
				case '*': targetState = state; break;
				default: in.unread(nextChar); targetState = new State();
			}
			if (currentChar == '.') {
				state.transitions.clear();
				state.addTransition(new AcceptAllExceptSpecified(targetState));
			} else {
				final Set<State> statesOfWhichTransitionsMustBeaddedToTarget 
					= removeCharFromTransitions(state.transitions, (char) currentChar);
				state.addTransition(new AcceptSingle(targetState, (char) currentChar));
				for (State stateToCopyIntoTarget : statesOfWhichTransitionsMustBeaddedToTarget) {
					targetState.transitions.addAll(stateToCopyIntoTarget.transitions);
				}
			}
			parseInto(targetState, in);
		}
	}

	/**
	 * excludes a char from all transitions in a set and returns the targets of 
	 * the affected transitions
	 */
	private static Set<State> removeCharFromTransitions(Set<Transition> transitions,
			char ch) {
		final Set<State> result = new HashSet<State>();
		for (Transition transition : transitions) {
			if (transition.accepts(ch)) {
				transition.exclude(ch);
				result.add(transition.getTarget());
			}
		}
		return result;
	}

}
