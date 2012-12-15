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

}
