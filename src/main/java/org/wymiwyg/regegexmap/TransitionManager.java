package org.wymiwyg.regegexmap;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TransitionManager {
	
	private static class AcceptanceDescriptor {
		Set<Character> acceptedChars = new HashSet<Character>();

		boolean accepts(char ch) {
			return acceptedChars.contains(ch);
		}
		
	}
	
	
	
	private Map<StatePair, AcceptanceDescriptor> transitions = 
			new HashMap<StatePair, AcceptanceDescriptor>();
	
	private Set<StatePair> wildCards = new HashSet<StatePair>();
	private Map<State, Set<StatePair>> fromIndex = new HashMap<State, Set<StatePair>>();

	private int stateCount;
	
	/*public Set<Character> getAcceptingChars(State from, State to) {
		final StatePair pair = new StatePair(from, to);
		return getAcceptingChars(pair);
	}
	
    private Set<Character> getAcceptingChars(StatePair pair) {
		return transitions.get(pair).acceptedChars;
	}
    
    public Set<Character> isWildCardTransition(State from, State to) {
		final StatePair pair = new StatePair(from, to);
		return wildCards.contains(pair);
	}*/

	/**
     * Makes the transition accepts theses chars additionally
     */
	public void addAccepting(State from, State to, Character... chs) {
		addAccepting(from, to, Arrays.asList(chs));
	}
	public void addAccepting(State from, State to, Collection<Character> chs) {
		final StatePair pair = new StatePair(from, to);
		if (wildCards.contains(pair)) {
			//throw new RuntimeException("There's already a wildcard transition");
			//we do nothing
			return;
		}
		AcceptanceDescriptor acceptanceDescriptor = transitions.get(pair);
		if (acceptanceDescriptor == null) {
			acceptanceDescriptor = new AcceptanceDescriptor();
			transitions.put(pair, acceptanceDescriptor);
			addToIndex(pair);
		}
		acceptanceDescriptor.acceptedChars.addAll(chs);
	}


	private void addToIndex(final StatePair pair) {
		State from = pair.s1;
		Set<StatePair> pairsWithFrom = fromIndex.get(from);
		if (pairsWithFrom == null) {
			pairsWithFrom = new HashSet<StatePair>();
			fromIndex.put(from, pairsWithFrom);
		}
		pairsWithFrom.add(pair);
	}
	
	private void removeFromIndex(final StatePair pair) {
		State from = pair.s1;
		Set<StatePair> pairsWithFrom = fromIndex.get(from);
		if (pairsWithFrom == null) {
			throw new RuntimeException("not in index");
		}
		pairsWithFrom.remove(pair);
	}
	
	
	public void addWildcard(State from, State to) {
		final StatePair pair = new StatePair(from, to);
		if (transitions.containsKey(pair)) {
			System.err.println("Attempt to adding wild card transition from "+from+" to "+to);
			for (Transition t : from.getOutgoingTransitions()) {
				System.err.println("Existing transition: "+t);
			}
			throw new RuntimeException("There's already a character transition");
		}
		if (!wildCards.add(pair)) {
			//throw new RuntimeException("Cannot add two wildcards");
			//added the same again
		}
		addToIndex(pair);
		for (StatePair outgoingPair : fromIndex.get(from)) {
			if (!outgoingPair.equals(pair) && wildCards.contains(outgoingPair)) {
				//throw new RuntimeException("Cannot add two wildcards");
				//just removing the old one
				removeWildcard(outgoingPair.s1, outgoingPair.s2);
				break;
			}
		}
	}
	public void removeWildcard(State from, State to) {
		final StatePair pair = new StatePair(from, to);
		if (!wildCards.remove(pair)) {
			throw new RuntimeException("No such wildcard!");
		}
		if (!transitions.containsKey(pair)) {
			removeFromIndex(pair);
		}
	}
	
	public void removeCharacterTransitions(State from, State to, Character... chs) {
		removeCharacterTransitions(from, to, Arrays.asList(chs));
	}
	
	public void removeCharacterTransitions(State from, State to, Collection<Character> chs) {	
		final StatePair pair = new StatePair(from, to);
		AcceptanceDescriptor acceptanceDescriptor = transitions.get(pair);
		if (acceptanceDescriptor == null) {
			throw new RuntimeException("No such character-transition");
		}
		if (!acceptanceDescriptor.acceptedChars.containsAll(chs)) {
			throw new RuntimeException("Character not in character-transition");
		}
		acceptanceDescriptor.acceptedChars.removeAll(chs);
		if (acceptanceDescriptor.acceptedChars.isEmpty()) {
			transitions.remove(pair);
			if (!wildCards.contains(pair)) {
				removeFromIndex(pair);
			}
		}
	}

	private Set<Character> concreteCharsFrom(State state) {
		Set<Character> result = new HashSet<Character>();
		Set<StatePair> pairs = fromIndex.get(state);
		for (StatePair statePair : pairs) {
			if (transitions.containsKey(statePair)) {
				result.addAll(transitions.get(statePair).acceptedChars);
			}
		}
		return result;
	}

	public Set<Transition> getTransitionsFrom(final State from) {
		final Set<StatePair> pairsWithFrom = fromIndex.get(from);
		if (pairsWithFrom == null) {
			return Collections.emptySet();
		}
		final Set<Transition> result = new HashSet<Transition>();
		for (final StatePair statePair : pairsWithFrom) {
			if (transitions.containsKey(statePair)) {
				result.add(new Transition(statePair.s2) {
					
					
					@Override
					public boolean accepts(char ch) {
						return transitions.get(statePair).accepts(ch);
					}
	
					@Override
					boolean isWildCard() {
						return false;
					}
	
					@Override
					public Set<Character> getAcceptedChars() {
						if (isWildCard()) {
							return null;
						} else {
							return transitions.get(statePair).acceptedChars;
						}
					}
					
					@Override
					public String toString() {
						return "Character transition for "+getAcceptedChars()+" from "+statePair.s1+" to "+statePair.s2;
					}
				});
			} else {
				result.add(new Transition(statePair.s2) {
					
					
					@Override
					public boolean accepts(char ch) {
						return (wildCards.contains(statePair) 
								&& !concreteCharsFrom(from).contains(ch));
					}

					@Override
					boolean isWildCard() {
						return true;
					}

					@Override
					public Set<Character> getAcceptedChars() {
						if (isWildCard()) {
							return null;
						} else {
							return transitions.get(statePair).acceptedChars;
						}
					}
					
					@Override
					public String toString() {
						return "Wildcard transition excluding "+concreteCharsFrom(from)+" from "+statePair.s1+" to "+statePair.s2;
					}
				});

			}
		}
		return result ;
		
	}
	public int getStateId() {
		return ++stateCount;
	}
	

}
