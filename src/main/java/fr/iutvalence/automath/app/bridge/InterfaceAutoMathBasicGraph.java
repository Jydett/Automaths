package fr.iutvalence.automath.app.bridge;

import java.util.Set;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;
import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;


public interface InterfaceAutoMathBasicGraph {

	void deleteAllElements();

	/**
	 * Minimise the graph (see <a href="https://en.wikipedia.org/wiki/DFA_minimization">Wikipedia</a>) given as a sets of <a target="_parent" href="http://jgraph.github.io/mxgraph/java/docs/com/mxgraph/view/mxGraph.html">{@link com.mxgraph.model.mxCell}</a>
	 */
	void minimize();

	/**
	 * Determinize the graph (see <a href="https://en.wikipedia.org/wiki/DFA_minimization">Wikipedia</a>) given as a sets of <a target="_parent" href="http://jgraph.github.io/mxgraph/java/docs/com/mxgraph/view/mxGraph.html">{@link com.mxgraph.model.mxCell}</a>
	 */
	void determinize();
	
	/**
	 * insert transition in the graph
	 * @param c The label of the transition
	 * @param source The soucre of the transition
	 * @param target The target of the transition
	 * @return The object implemented by <a target="_parent" href="http://jgraph.github.io/mxgraph/java/docs/com/mxgraph/view/mxGraph.html">{@link com.mxgraph.model.mxICell}</a>
	 */
	Object insertEdge(String c, Object source, Object target);
	
	/**
	 * Redefinition of method A in the Class {@link FiniteStateAutomatonGraph#insertState}, for the add a state in the graph
	 * @param parent The parent contains this state
	 * @param label The name of the state
	 * @param x The position on the abscissa
	 * @param y The position on the ordinate
	 * @param style The graph of state
	 * @param accepted 	<code>true</code> if the state is a state of arrival; 
     *                  <code>false</code> otherwise.
	 * @param initial	<code>true</code> if the state is a starting state; 
     *                  <code>false</code> otherwise.
	 * @return The object implemented by <a target="_parent" href="http://jgraph.github.io/mxgraph/java/docs/com/mxgraph/view/mxGraph.html">{@link com.mxgraph.model.mxICell}</a>
	 */
	Object insertState(Object parent, String label, double x, double y, String style, boolean accepted, boolean initial);

	/**
	 * Convert a regular expression to a graphical automaton
	 * @param text The regular expression
	 */
	void importerExpReg(String text);
	
	/**
	 * Get the list to contain all states
	 * @return The list of <a target="_parent" href="http://jgraph.github.io/mxgraph/java/docs/com/mxgraph/view/mxGraph.html">{@link com.mxgraph.model.mxCell}</a>
	 */
	Set<mxCell> getAllState();
	
	/**
	 * Get the list to contain all transition
	 * @return The list of <a target="_parent" href="http://jgraph.github.io/mxgraph/java/docs/com/mxgraph/view/mxGraph.html">{@link com.mxgraph.model.mxCell}</a>
	 */
	Set<mxCell> getAllTransition();

	mxIGraphModel getModel();
}