package org.wymiwyg.regegexmap;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Parser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//State startSparse("hello");

	}

	/**
	 * 
	 * @param string
	 * @return the startState
	 */
	public static State parse(String string) {
		TransitionManager tm = new TransitionManager();
		State startState = new State(tm);
		parseInto(tm, startState, string);
		return startState;
	}
	
	/**
	 * 
	 * @param tm
	 * @param startState
	 * @param string
	 * @return the exit states
	 */
	public static Set<State> parseInto(TransitionManager tm, State startState, String string) {
		try {
			return parseInto(tm, startState, new PushbackReader(new StringReader(string), 1));
		} catch (IOException e) {
			throw new RuntimeException("IO reading from string", e);
		}
	}

	private static Set<State> parseInto(TransitionManager tm, State startState, PushbackReader in) throws IOException {
		Set<State> exitStates = parseInto(startState, startState, in, tm);
		for (State state : exitStates) {
			state.markAsEndState();
		}
		return exitStates;
	}

	/**
	 * @return the exit states
	 */
	private static Set<State> parseInto(State startState, State state, PushbackReader in, TransitionManager tm) throws IOException {
		//make sure nothing point back to startState, if so create clone of startstate to whih things oint back
		int currentChar = in.read();
		switch (currentChar) {
			case -1 : state.markAsEndState(); return Collections.singleton(state);
			case '|':
				Set<State> exitStates = new HashSet<State>();
				state.markAsEndState();
				exitStates.add(state); 
				exitStates.addAll(parseInto(startState, startState, in, tm));
				return exitStates;
			default:
				int nextChar = in.read();
				State targetState;
				switch (nextChar) {
					case -1: targetState = new State(tm); break;
					case '*': targetState = state; break;
					default: in.unread(nextChar); targetState = new State(tm);
				}
				
				if (currentChar == '.') {
					//apart from adding the wildcard transition we must
					//(exclude all concrete chars from it and) copy our target 
					//into the targets of the concrete chars relations
					//if a wildcard transition is already there we only copy its target into
					//our target and remove the existing transition
					//definition of "copy s1 into s2": copy all outgoing transitions from s1 to s2, if 
					//s1 is and endstate s2 becomes an endstate too.
					Set<State> existingTargets = new HashSet<State>();
					Set<State> existingWildcardTargets = new HashSet<State>();
					Iterator<Transition> existingIter = state.getOutgoingTransitions().iterator();
					while (existingIter.hasNext()) {
						Transition existingTransition = existingIter.next();
						
						if (existingTransition.isWildCard()) {
							tm.removeWildcard(state, existingTransition.getTarget());
							existingWildcardTargets.add(existingTransition.getTarget());
						} else {
							if (existingTransition.getTarget().equals(targetState)) {
								tm.removeCharacterTransitions(state, targetState, existingTransition.getAcceptedChars());
							}
							existingTargets.add(existingTransition.getTarget());
						}
					}
					tm.addWildcard(state, targetState);
					Set<State> result = parseInto(startState, targetState, in, tm);
					for (State existingTarget : existingTargets) {				
						copyInto(targetState, existingTarget, tm);
					}
					for (State existingTarget : existingWildcardTargets) {				
						copyInto(existingTarget, targetState, tm);
					}
					return result;
				} else {
					Set<State> statesOfWhichTransitionsMustBeAddedToTarget = new HashSet<State>();
						//= removeCharFromTransitions(state, (char) currentChar, tm);
					Transition existingCharTransition = null;
					for (Transition t : state.getOutgoingTransitions()) {
						if (t.isWildCard()) {
							statesOfWhichTransitionsMustBeAddedToTarget.add(t.getTarget());
						} else {
							if (t.accepts((char) currentChar)) {
								existingCharTransition = t;
							}
						}
					}
					if (existingCharTransition != null) {
						targetState = existingCharTransition.getTarget();
					}
					tm.addAccepting(state, targetState, (char) currentChar);
					for (State stateToCopyIntoTarget : statesOfWhichTransitionsMustBeAddedToTarget) {				
						copyInto(stateToCopyIntoTarget, targetState, tm);
					}
					Set<State> result = parseInto(startState, targetState, in, tm);
					
					return result;
				}
				
				
		}
	}

	//definition of "copy s1 into s2": copy all outgoing transitions from s1 to s2, if 
	//s1 is and endstate s2 becomes an endstate too.
	//if a wildcard transition is copied to s2 then the targets of al other relatiosn are copied into its target
	private static void copyInto(State from, State s2, TransitionManager tm) {
		copyInto(from, s2, tm, new HashSet<StatePair>());
	}
	/*
	 * the stack argument to prevent infinite recursion
	 */
	private static void copyInto(State from, State s2, TransitionManager tm, Set<StatePair> stack) {
		if (!stack.add(new StatePair(from, s2))) {
			return;
		}
		if (from == s2) {
			return;
		}
		if (from.isEndState()) {
			s2.markAsEndState();
		}
		State wildCardTarget = null;
		for (Transition t : from.getOutgoingTransitions()) {
			if (t.isWildCard()) {
				wildCardTarget = t.getTarget();
				tm.addWildcard(s2, t.getTarget());
			} else {
				tm.addAccepting(s2, t.getTarget(), t.getAcceptedChars());
			}
		}
		if (wildCardTarget != null) {
			//we must recurse to copy t.getTargets() into all character targets of s2
			for (Transition u : s2.getOutgoingTransitions()) {
				if (!u.isWildCard()) {
					copyInto(wildCardTarget, u.getTarget(), tm, stack);
				}
			}
		}
		
	}

	/**
	 * excludes a char from all transitions in a set and returns the targets of 
	 * the affected transitions, transition that will no longer accept anything are removed from the set
	 */
	private static Set<State> removeCharFromTransitions(State from,
			char ch, TransitionManager tm) {
		final Set<State> result = new HashSet<State>();
		Set<Transition> transitions = tm.getTransitionsFrom(from);
		Iterator<Transition> iter = transitions.iterator();
		while (iter.hasNext()) {
			Transition transition = iter.next();
			if (transition.accepts(ch)) {
				tm.removeCharacterTransitions(from, transition.getTarget(), ch);
				result.add(transition.getTarget());
			}
		}
		return result;
	}

}
