package org.wymiwyg.regexmap.tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.wymiwyg.regegexmap.Matcher;
import org.wymiwyg.regegexmap.Parser;
import org.wymiwyg.regegexmap.State;
import org.wymiwyg.regegexmap.Transition;

public class ParserMatcherTest {

	@Test
	public void testParseSimple() {
		final State startState = Parser.parse("hi");
		assertNotNull("Result of parsing must not be null", startState);
		assertEquals("Wrong number of outgoing transitions", 1, startState.getOutgoingTransitions().size());
		final Transition transition = startState.getOutgoingTransitions().iterator().next();
		assertTrue("Character not accepted", transition.accepts('h'));	
		final State secondState = transition.getTarget();
		assertFalse("Expected State must not be an EndSate", secondState.isEndState());
		assertEquals("Wrong number of outgoing transitions", 1, secondState.getOutgoingTransitions().size());
		final Transition secondTransition = secondState.getOutgoingTransitions().iterator().next();
		assertTrue("Character not accepted", secondTransition.accepts('i'));
		assertTrue("Expected must be an EndState", secondTransition.getTarget() .isEndState());
	}
	
	@Test
	public void testMatchSimple() {
		final State startState = Parser.parse("hi");
		assertTrue("Exact String not accepted", Matcher.match(startState, "hi"));
		assertFalse("Too long string accepted", Matcher.match(startState, "hiandsoon"));
		assertFalse("Too short string accepted", Matcher.match(startState, "h"));
		assertFalse("Wrong string accepted", Matcher.match(startState, "hhi"));
		assertFalse("Wrong string accepted", Matcher.match(startState, "hu"));
	}
	
	@Test
	public void testParseDot() {
		final State startState = Parser.parse(".");
		assertNotNull("Result of parsing must not be null", startState);
		assertEquals("Wrong number of outgoing transitions", 1, startState.getOutgoingTransitions().size());
		final Transition transition = startState.getOutgoingTransitions().iterator().next();
		assertTrue("Character not accepted", transition.accepts('h'));	
		assertTrue("Character not accepted", transition.accepts('f'));
		assertTrue("Character not accepted", transition.accepts('.'));
		final State secondState = transition.getTarget();
		assertTrue("Expected State must be an EndSate", secondState.isEndState());
	}
	
	@Test
	public void testMatchDot() {
		final State startState = Parser.parse(".");
		assertTrue("Char not accepted", Matcher.match(startState, "m"));
		assertTrue("Char not accepted", Matcher.match(startState, "\\"));
		assertTrue("Char not accepted", Matcher.match(startState, "."));
		assertFalse("Too long string accepted", Matcher.match(startState, "hiandsoon"));
		assertFalse("Too short string accepted", Matcher.match(startState, ""));
	}
	
	@Test
	public void testParseStar() {
		final State startState = Parser.parse("h*");
		assertNotNull("Result of parsing must not be null", startState);
		assertEquals("Wrong number of outgoing transitions", 1, startState.getOutgoingTransitions().size());
		assertTrue("StartState is not an endstate", startState.isEndState());
		final Transition transition = startState.getOutgoingTransitions().iterator().next();
		assertTrue("Character not accepted", transition.accepts('h'));	
		final State targetState = transition.getTarget();
		assertEquals("Transition is not pointing back to StartState", startState, targetState);
	}
	
	@Test
	public void testMatchStar() {
		final State startState = Parser.parse("h*");
		assertTrue("Single not accepted", Matcher.match(startState, "h"));
		assertTrue("Repeaded char not accepted", Matcher.match(startState, "hhhh"));
		assertTrue("No char not accepted", Matcher.match(startState, ""));
		assertFalse("String with wrong chars accepted", Matcher.match(startState, "hiandsoon"));
	}
	
	@Test
	public void testMatchDotStar() {
		final State startState = Parser.parse(".*");
		assertTrue("Single not accepted", Matcher.match(startState, "h"));
		assertTrue("Repeaded char not accepted", Matcher.match(startState, "hhhh"));
		assertTrue("No char not accepted", Matcher.match(startState, ""));
		assertTrue("String with mixed chars not accepted", Matcher.match(startState, "hiandsoon"));
	}
	
	@Test
	public void testMatchDotStarPrefix() {
		final State startState = Parser.parse("h.*");
		assertTrue("Single not accepted", Matcher.match(startState, "h"));
		assertTrue("Repeaded char not accepted", Matcher.match(startState, "hhhh"));
		assertFalse("Accepted despite missing prefix", Matcher.match(startState, ""));
		assertFalse("Accepted despite wrong prefix", Matcher.match(startState, "shkjh"));
		assertTrue("String with mixed chars not accepted", Matcher.match(startState, "hiandsoon"));
	}

	@Test
	public void testMatchStarSuffix() {
		final State startState = Parser.parse("m*h");
		assertTrue("Single not accepted", Matcher.match(startState, "h"));
		assertTrue("Repeaded char not accepted", Matcher.match(startState, "mmmmh"));
		assertFalse("Accepted despite missing suffix", Matcher.match(startState, ""));
		assertFalse("Accepted despite missing suffix", Matcher.match(startState, "mmmm"));
		assertFalse("Accepted despite wrong chars", Matcher.match(startState, "shkjhm"));
	}
	
	/**
	 * This should result in a DSA with  a loop-back on start for all but n and 
	 * a transition for n to a second state, with a transition back to the start state
	 * for all but n and a loop-back for n.
	 */
	@Test
	public void testParseDotStarSuffix() {
		final State startState = Parser.parse(".*h");
		assertNotNull("Result of parsing must not be null", startState);
		assertEquals("Wrong number of outgoing transitions on start state", 2, 
				startState.getOutgoingTransitions().size());
		State otherState = null;
		boolean loopBackTransitionPresent = false;
		for (Transition t : startState.getOutgoingTransitions()) {
			final State target = t.getTarget();
			if (target == startState) {
				loopBackTransitionPresent = true;
			} else {
				otherState = target;
			}
		}
		assertTrue("No loop back transition on start state", loopBackTransitionPresent);
		assertTrue("Second State is not an endstate", otherState.isEndState());
		assertEquals("Wrong number of outgoing transitions on second state", 2, 
				otherState.getOutgoingTransitions().size());
	}
	
	@Test
	public void testMatchDotStarSuffix() {
		final State startState = Parser.parse(".*h");
		assertTrue("Single not accepted", Matcher.match(startState, "h"));
		assertTrue("Repeaded char not accepted", Matcher.match(startState, "hhhh"));
		assertFalse("Accepted despite missing suffix", Matcher.match(startState, ""));
		assertFalse("Accepted despite wrong suffix", Matcher.match(startState, "shkjhs"));
		assertTrue("String with mixed chars not accepted", Matcher.match(startState, "iandshoonh"));
	}
}
