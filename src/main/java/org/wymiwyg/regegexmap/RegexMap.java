package org.wymiwyg.regegexmap;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RegexMap<T> extends AbstractMap<String, T> {

	private Set<String> keySet = new HashSet<String>();

	private TransitionManager tm = new TransitionManager();
	
	private State startState = new State(tm);
	private Map<State, T> exitStateValueMap = new HashMap<State, T>(); 
	
	public int size() {
		return keySet.size();
	}


	public boolean containsKey(Object key) {
		return keySet.contains(key);
	}

	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T get(Object key) {
		State endState = Matcher.matchToState(startState, key.toString());
		if (endState == null) {
			return null;
		} else {
			return exitStateValueMap.get(endState);
		}
	}

	public T put(String key, T value) {
		keySet.add(key);
		final Set<State> exitStates = Parser.parseInto(tm, startState, key);
		for (State state : exitStates) {
			exitStateValueMap.put(state, value);
		}
		return null;
	}


	@Override
	public Set<java.util.Map.Entry<String, T>> entrySet() {
		throw new UnsupportedOperationException();
	}

	

	

}
