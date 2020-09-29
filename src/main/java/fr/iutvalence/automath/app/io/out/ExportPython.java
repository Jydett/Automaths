package fr.iutvalence.automath.app.io.out;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;

import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;
import fr.iutvalence.automath.app.model.StateInfo;
import fr.iutvalence.automath.app.model.TransitionInfo;

/**
 * Contains the logic to convert the graphical automaton to a Python file
 * <p>
 * The format that takes the dictionary of the automaton as follows:<br>
 * <code>automate=({'language'}, {state list,}, {begin state,}, {accepting state,}, { (source: 'label'):{target},():{}})</code>,
 * there are also a function to test the automaton : isRecognized(word), to test the recognition of a word by the automaton
 * </p>
 */
public class ExportPython {

	/**
	 * The list to contain all states of automaton
	 */
	private Set<mxCell> states;

	/**
	 * The list to contain all transition of automaton
	 */
	private Set<mxCell> transitions;
	
	/**
	 * A constructor of ExportPython, with the parameter graph
	 * @param automaton The graph of application 
	 */
	public ExportPython(FiniteStateAutomatonGraph automaton){
		this.states = automaton.getAllState();
		this.transitions = automaton.getAllTransition();
	}
	
	/**
	 * Convert the graph to python script and save the result to an (.py) file with the specified path in parameter
	 * @param file The path to saving the file
	 */
	public void exportAutomate(String file){
		if (file.equals("cancel")) {
			return;
		}
		StringBuilder py = new StringBuilder();
		StringBuilder language = new StringBuilder();
		Set<Character> lang = new HashSet<>();
		StringBuilder list_of_state = new StringBuilder();
		StringBuilder list_of_state_begin = new StringBuilder();
		StringBuilder list_of_state_end = new StringBuilder();
		StringBuilder list_of_transition = new StringBuilder();
		
		Map<mxCell,Integer> stateMap = new HashMap<>();		
		
		py.append("automate=(");
		
		int id = 0;
		list_of_state.append("{");
		list_of_state_begin.append("{");
		list_of_state_end.append("{");
		Iterator<mxCell> it = states.iterator();
		while (it.hasNext()) {
			mxCell state = it.next();
			stateMap.put(state, id);
			
			if (((StateInfo) state.getValue()).isAccepting) {
				if (list_of_state_end.length() != 1) {
					list_of_state_end.append(",");
				}
				list_of_state_end.append(id);
			}
			if (((StateInfo) state.getValue()).isStarting) {
				if (list_of_state_begin.length() != 1) {
					list_of_state_begin.append(",");
				}
				list_of_state_begin.append(id);
			}
			list_of_state.append(id);
			if (it.hasNext()) {
				list_of_state.append(",");
			}
			id++;
		}
		list_of_state.append("}");
		list_of_state_begin.append("}");
		list_of_state_end.append("}");
		
		list_of_transition.append("{");
		for (mxCell transition : transitions) {
			char[] transitionValue = ((TransitionInfo) transition.getValue()).getLabel().toCharArray();
			mxICell source = transition.getSource();
			mxICell target = transition.getTarget();
			for (char aTransitionValue : transitionValue) {
				if (list_of_transition.length() != 1) {
					list_of_transition.append(",");
				}
				list_of_transition.append("(")
					.append(stateMap.get(source))
					.append(",'")
					.append(aTransitionValue)
					.append("'):{")
					.append(stateMap.get(target))
					.append("}");
				lang.add(aTransitionValue);
			}
		}
		list_of_transition.append("}");
		
		language.append("{");
		for (Character state : lang) {
			if (language.length() != 1) {
				language.append(",");
			}
			language.append("'").append(state).append("'");
		}
		language.append("}");
		
		py.append(language).
		append(",").append(list_of_state).
		append(",").append(list_of_state_begin).
		append(",").append(list_of_state_end).
		append(",").append(list_of_transition).
		append(")").
		append("\n\ndef isRecognized(mot):\r\n" +
				"\tA, S, D, R, t = automate\r\n" + 
				"\tEC = list(D)[0];\r\n" + 
				"\tfor i in mot:\r\n" + 
				"\t\tEC = list(t[(EC,i)])[0]\r\n" + 
				"\treturn EC in R");
        try (PrintWriter out = new PrintWriter(file, "UTF-8")) {
            out.write(py.toString());
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}