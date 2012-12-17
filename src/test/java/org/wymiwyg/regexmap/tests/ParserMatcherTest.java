package org.wymiwyg.regexmap.tests;

import static org.junit.Assert.*;

import java.util.Set;

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
	 * This should result in a DSA with  a loop-back on start for all but 'h' and 
	 * a transition for 'h' to a second state, with a transition back to the start state
	 * for all but n and a loop-back for 'h'.
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
		assertTrue("Double char not accepted", Matcher.match(startState, "hh"));
		assertTrue("Triple char not accepted", Matcher.match(startState, "hhh"));
		assertFalse("Accepted despite missing suffix", Matcher.match(startState, ""));
		assertFalse("Accepted despite wrong suffix", Matcher.match(startState, "shkjhs"));
		assertTrue("String with mixed chars not accepted", Matcher.match(startState, "iandshoonh"));
	}
	
	@Test
	public void testMatchDotStarComplex() {
		final State startState = Parser.parse(".*h.*o");
		assertTrue("Minimum not accepted", Matcher.match(startState, "ho"));
		assertTrue("Matching String not accepted", Matcher.match(startState, "hhhho"));
		assertTrue("Matching String not accepted", Matcher.match(startState, "dfgfdhhkjhkjho"));
		assertFalse("Accepted not matching", Matcher.match(startState, ""));
		assertFalse("Accepted not matching", Matcher.match(startState, "h"));
		assertFalse("Accepted not matching", Matcher.match(startState, "o"));
		assertFalse("Accepted not matching", Matcher.match(startState, "hos"));
		assertFalse("Accepted not matching", Matcher.match(startState, "sdfds dsafsdoo sd"));
	}
	
	@Test
	public void testParseOr() {
		final State startState = Parser.parse("h|o");
		assertNotNull("Result of parsing must not be null", startState);
		assertEquals("Wrong number of outgoing transitions on start state", 2, 
				startState.getOutgoingTransitions().size());
		for (Transition t : startState.getOutgoingTransitions()) {
			assertTrue("Second State is not an endstate", t.getTarget().isEndState());
		}
	}
	
	@Test
	public void testMatchOr() {
		final State startState = Parser.parse("hi|ho");
		if (!Matcher.match(startState, "ho")) {
			assertTrue("Matching String not accepted", Matcher.match(startState, "ho"));
		}
		if (!Matcher.match(startState, "hi")) {
			assertTrue("Matching String not accepted", Matcher.match(startState, "hi"));
		}
		assertFalse("Accepted not matching", Matcher.match(startState, ""));
		assertFalse("Accepted not matching", Matcher.match(startState, "h"));
		assertFalse("Accepted not matching", Matcher.match(startState, "o"));
		assertFalse("Accepted not matching", Matcher.match(startState, "hiho"));
		assertFalse("Accepted not matching", Matcher.match(startState, "hhitt"));
	}
	
	/**
	 * We expect the start state s1 to have a loop back for
	 * ./h, an n-loopback transition and an h-transition to s2, s2. to have a ./o transition
	 * back to to start and an o-transition to s3 and s3 to have
	 * a . transition to start and a h transition to s2, all nodes are end states
	 */
	@Test
	public void testParsePointlessOr() {
		State s1 = Parser.parse(".*|ho");
		verifyPointlessOr(s1);
	}
	/**
	 * same again
	 */
	@Test
	public void testParsePointlessOr2() {
		State s1 = Parser.parse("ho|.*");
		verifyPointlessOr(s1);
	}
	
	private void verifyPointlessOr(State s1) {
		assertNotNull("Result of parsing must not be null", s1);
		Set<Transition> s1Transitions = s1.getOutgoingTransitions();
		assertEquals("Wrong number of outgoing transitions", 2, s1Transitions.size());
		Transition loopBackTransition = null;
		Transition s1s2 = null;
		for (Transition transition : s1Transitions) {
			if (transition.getTarget() == s1) {
				loopBackTransition = transition;
			} else {
				s1s2 = transition;
			}
		}
		State s2 = s1s2.getTarget();
		assertNotNull("Transition not looping back", loopBackTransition);
		assertTrue("Transition not acception the right char", s1s2.accepts('h'));
		assertFalse("Transition char it shouldn't", loopBackTransition.accepts('h'));
		assertTrue("StartState must be an endstate", s1.isEndState());
		assertFalse("S2 must not be an endstate", s2.isEndState());
		Set<Transition> s2Transitions = s2.getOutgoingTransitions();
		//3 because there's a n-loopback transition 
		assertEquals("Wrong number of outgoing transitions", 3, s2Transitions.size());
		Transition s2s1 = null;
		Transition s2s3 = null;
		for (Transition transition : s2Transitions) {
			if (transition.getTarget() == s1) {
				s2s1 = transition;
			} else {
				if (transition.getTarget() != s2) {
					s2s3 = transition;
				}
			}
		}
		assertNotNull("Transition from s2 back to start missing", s2s1);
		assertTrue("Transition not accepting the right char", s2s3.accepts('o'));
		final State s3 = s2s3.getTarget();
		assertEquals("Wrong number of transitions on s3", 2, s3.getOutgoingTransitions().size());
	}
	
	@Test
	public void testPointlessOr() {
		State startState = Parser.parse(".*|ho");
		assertTrue("Matching String not accepted", Matcher.match(startState, "ho"));
		assertTrue("Matching String not accepted", Matcher.match(startState, "hofsdfgsd"));
		assertTrue("Matching String not accepted", Matcher.match(startState, ""));
		//and the other way
		startState = Parser.parse("ho|.*");
		assertTrue("Matching String not accepted", Matcher.match(startState, "ho"));
		//fails some times, not wlways!
		assertTrue("Matching String not accepted", Matcher.match(startState, "hofsdfgsd"));
		assertTrue("Matching String not accepted", Matcher.match(startState, ""));
	}
	
	@Test
	public void testOrAndDotStar() {
		State startState = Parser.parse(".*o|u");
		assertTrue("Matching String not accepted", Matcher.match(startState, "ho"));
		assertTrue("Matching String not accepted", Matcher.match(startState, "hofsdfgsdo"));
		assertTrue("Matching String not accepted", Matcher.match(startState, "u"));
		assertFalse("Accepted not matching", Matcher.match(startState, ""));
		assertFalse("Accepted not matching", Matcher.match(startState, "hdasdsaodasui"));

	}
	@Test
	public void testOrAndComplexExpression() {
		State startState = Parser.parse(".*o|.*u");
		assertTrue("Matching String not accepted", Matcher.match(startState, "ho"));
		assertTrue("Matching String not accepted", Matcher.match(startState, "hofsdfgsdu"));
		assertTrue("Matching String not accepted", Matcher.match(startState, "u"));
		assertFalse("Accepted not matching", Matcher.match(startState, ""));
		assertFalse("Accepted not matching", Matcher.match(startState, "hdasdsaodasui"));

	}
}
