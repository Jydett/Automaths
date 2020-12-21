package fr.iutvalence.automath.app.bridge;

import java.util.Set;

import com.mxgraph.model.mxCell;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.Transition;

public interface IAutomatonOperator {

	/**
	 * Minimise the graph (see <a href="https://en.wikipedia.org/wiki/DFA_minimization">Wikipedia</a>) given as a sets of <a target="_parent" href="http://jgraph.github.io/mxgraph/java/docs/com/mxgraph/view/mxGraph.html">{@link com.mxgraph.model.mxCell}</a>
	 * @param states all the states the automaton has.
	 * @param transitions all the transitions the automaton has.
	 * @return The minimised <a target="_parent" href="http://www.brics.dk/automaton/doc/dk/brics/automaton/Automaton.html">{@link dk.brics.automaton.Automaton}</a>.
	 */
	Automaton minimize(Set<mxCell> states, Set<mxCell> transitions);

	/**
	 * Minimise the graph (see <a href="https://en.wikipedia.org/wiki/DFA_minimization">Wikipedia</a>) given as a sets of <a target="_parent" href="http://jgraph.github.io/mxgraph/java/docs/com/mxgraph/view/mxGraph.html">{@link com.mxgraph.model.mxCell}</a>
	 * @param states all the states the automaton has.
	 * @param transitions all the transitions the automaton has.
	 * @return The minimised <a target="_parent" href="http://www.brics.dk/automaton/doc/dk/brics/automaton/Automaton.html">{@link dk.brics.automaton.Automaton}</a>.
	 */
	Automaton determinize(Set<mxCell> states, Set<mxCell> transitions);

	String getRegex(Set<mxCell> state, Set<mxCell> transitions);

	/**
	 * Generated the automaton from a string
	 * @param text The regular expression as a string of characters
	 * @return The new automate <a target="_parent" href="http://www.brics.dk/automaton/doc/dk/brics/automaton/Automaton.html">{@link dk.brics.automaton.Automaton}</a>
	 */
	Automaton generateAutomateWithExpReg(String text);

	/**
	 * Returns all characters recognize by a given <a target="_parent" href="http://www.brics.dk/automaton/doc/dk/brics/automaton/Transition.html">{@link dk.brics.automaton.Transition}</a>
	 * @param currentTransitionOfAutomate The transition 
	 * @return a concatenation of all the characters recognize by the given transition
	 */
	String getString(Transition currentTransitionOfAutomate);
}