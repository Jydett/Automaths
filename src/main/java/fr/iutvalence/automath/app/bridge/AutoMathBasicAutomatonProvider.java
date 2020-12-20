package fr.iutvalence.automath.app.bridge;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import fr.iutvalence.automath.app.model.StateInfo;
import fr.iutvalence.automath.app.model.TransitionInfo;
import org.snt.autorex.Autorex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * AutoMathBasicAutomatonProvider is the class that contains algorithmic actions on the automaton
 */
public class AutoMathBasicAutomatonProvider implements InterfaceAutomaton {

	@Override
	public Automaton minimize(Set<mxCell> state,Set<mxCell> transitions) {
		Automaton automate = getAutomate(state, transitions);
		automate.minimize();
		resetStateInitial(automate);
		return automate;
	}

	@Override
	public Automaton determinize(Set<mxCell> state,Set<mxCell> transitions) {
		Automaton automate = getAutomate(state, transitions);
		automate.determinize();
		resetStateInitial(automate);
		String language = language(automate.getStates());
		generateStateWell(language, automate.getStates());
		return automate;
	}

	@Override
	public String getRegex(Set<mxCell> state, Set<mxCell> transitions) {
		Automaton automate = getAutomate(state, transitions);

		return Autorex.getRegexFromAutomaton(automate).replace("(.{0})","").replace(".{0}","").replace("\\&", "");
	}

	public Automaton generateAutomateWithExpReg(String text){
		RegExp exp = new RegExp(text);
		Automaton automate = exp.toAutomaton();
		automate.setDeterministic(false);
		automate.minimize();
		automate.getInitialState().setInitial(true);
		return automate;
	}

	/**
	 * create {@link Automaton} with all states and all transitions
	 * @param state the list of states
	 * @param transitions the list of transition
	 * @return a {@link Automaton}
	 */
	private Automaton getAutomate(Set<mxCell> state, Set<mxCell> transitions) {
		State parent = new State();
		Map<Integer,State> stateMap = new HashMap<>();

		for (mxCell currentMxCellState : state) {
			State currentState = new State();
			StateInfo currentStateInfo = (StateInfo)currentMxCellState.getValue();
			currentState.setInitial(currentStateInfo.isStarting());
			currentState.setAccept(currentStateInfo.isAccepting());
			stateMap.put(currentMxCellState.hashCode(), currentState);
			if(currentState.isInitial()) {
				parent.addTransition(new Transition('&', currentState));
			}
		}

		for (mxCell currentTransitionMxCell : transitions) {
			mxICell source = currentTransitionMxCell.getSource();
			mxICell target = currentTransitionMxCell.getTarget();

			TransitionInfo currentTransitionInfo = (TransitionInfo) currentTransitionMxCell.getValue();
			char[] chars = currentTransitionInfo.getLabel().toCharArray();
			State stateSource = stateMap.get(source.hashCode());
			State stateTarget = stateMap.get(target.hashCode());
			for (char c : chars) {
				stateSource.addTransition(new Transition(c, stateTarget));
			}
		}
		Automaton automate = new Automaton();
		automate.setInitialState(parent);
		automate.setDeterministic(false);
		return automate;
	}

	private void resetStateInitial(Automaton automate){
		Set<State> listOfState = automate.getStates();
		for(State currentStateOfAutomate : listOfState) {
			currentStateOfAutomate.setInitial(false);
		}
		Set<Transition> transitionOfInitialState = automate.getInitialState().getTransitions();
		for(Transition currentInitialTransition : transitionOfInitialState) {
			currentInitialTransition.getDest().setInitial(true);
		}
	}

	@Override
	public String getString(Transition currentTransitionOfAutomate) {
		char max = currentTransitionOfAutomate.getMax();
		char min = currentTransitionOfAutomate.getMin();
		if (min == 0 && max == 65535) {
			return "*";
		}
		StringBuilder finale = new StringBuilder();
		if (min < max) {
			for (int character = min; character <= max; character++) {
				finale.append((char) character);
			}
		} else {
			finale = new StringBuilder("" + min);
		}
		return finale.toString();
	}


	private String language(Set<State> states){
		StringBuilder language = new StringBuilder();
    	Set<Character> lang = new HashSet<>();
    	for (State state : states){
	    	for (Transition transition : state.getTransitions()) {
	    		for (char c : getString(transition).toCharArray()) {
					lang.add(c);
				}
	    	}
    	}
    	lang.remove('&');
    	for (Character c : lang) {
    		language.append(c);
		}
		return language.toString();
    }

	/**
	 * create a state well
	 * @param language the language recognized by the automaton
	 * @param states the list of all states of the automaton
	 */
	private void generateStateWell(String language, Set<State> states){
		State well = new State();
		for (State state : states) {
			Set<Character> lang = new HashSet<>();
			for (Transition transition : state.getTransitions()) {
	    		for (char c : getString(transition).toCharArray()) {
					lang.add(c);
					System.out.println(toString());
				}
	    	}
			Set<Character> char_rest = containeChar(language, lang);
    		for (char c : char_rest) {
				state.addTransition(new Transition(c, well));
			}
		}
		for (char c : language.toCharArray()) {
			well.addTransition(new Transition(c, well));
		}
	}

	private Set<Character> containeChar(String language, Set<Character> liste_char){
		Set<Character> rest = new HashSet<>();
		for (Character c : language.toCharArray()) {
			rest.add(c);
		}
		for (Character c : liste_char){
			rest.remove(c);
		}
		return rest;
	}
	
	public boolean isMealy(Set<mxCell> states, Set<mxCell> transitions) {
		Automaton automate = getAutomate(states, transitions);
		Set<State> s = automate.getStates();
		for (State currentState : s) {
			Set<Transition> t = currentState.getTransitions();
			for (Transition currentTransition : t) {
				//new TransitionInfo(, stateInfo)
			}
		}
		return true;
	}
}
