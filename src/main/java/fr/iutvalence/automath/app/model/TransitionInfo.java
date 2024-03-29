package fr.iutvalence.automath.app.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Contains the information of a <a target="_parent" href="http://jgraph.github.io/mxgraph/java/docs/com/mxgraph/view/mxGraph.html">{@link com.mxgraph.model.mxCell}</a>'s edge form.
 */
@NoArgsConstructor
@Getter
public class TransitionInfo implements CellInfo {

	/**
	 * The source of the transition
	 */
	private StateInfo source;
	/**
	 * The target of the transition
	 */
	private StateInfo destination;

	/**
	 * the label of the transition
	 */
	public String label;

	public TransitionInfo(String label, StateInfo stateInfo) {
		this.label = label;
		this.source = stateInfo;
		this.destination = null;
	}

	private TransitionInfo(String label, StateInfo destination, StateInfo source) {
		this.label = label;
		this.source = source;
		this.destination = destination;
	}

	@Override
	public String toString() {
		if(destination == null || source == null) return "";
		String src = source.getLabel().length() == 0 ? CellInfo.NO_NAME : source.getLabel();
		String dest = destination.getLabel().length() == 0 ? CellInfo.NO_NAME : destination.getLabel();

		return "Transition " + (label.length() == 0 ? CellInfo.NO_NAME : label) + "\n [" + src + "->" + dest + "]";
	}

	@Override
	public boolean isValid() {
		return destination != null && source != null;
	}

	/** Returns the union of the two strings, case insensitive.
    Takes O( (|S1| + |S2|) ^2 ) time. */
	public static String union(String s1, String s2){
	    String s = (s1 + s2).toLowerCase(); //start with entire contents of both strings
	    int i = 0;
	    while (i < s.length()) {
	        char c = s.charAt(i);
	        if (i != s.lastIndexOf(c)) { //If c occurs multiple times in s, remove first one
				s = s.substring(0, i) + s.substring(i + 1);
			} else  {
	        	i++; //otherwise move pointer forward
			}
	    }
	    return s;
	}

	@Override
	public CellInfo withLabel(String label) {
		return new TransitionInfo(label, destination, source);
	}

}
