package fr.iutvalence.automath.app.model;

/**
 * Contains the information of a <a target="_parent" href="http://jgraph.github.io/mxgraph/java/docs/com/mxgraph/view/mxGraph.html">{@link com.mxgraph.model.mxCell}</a>'s edge form.
 */
@SuppressWarnings("serial")
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
	public String name;
	
	public TransitionInfo(String name, StateInfo stateInfo) {
		this.name = name;
		this.source = stateInfo;
		this.destination = null;
	}
	
	@Override
	public String toString() {
		if(destination == null || source == null) return "";
		String src = source.getLabel().length()==0?CellInfo.NO_NAME:source.getLabel();
		String dest = destination.getLabel().length()==0?CellInfo.NO_NAME:destination.getLabel();

		return "Transition "+(name.length()==0?CellInfo.NO_NAME:name)+"\n ["+src+"->"+dest+"]";
	}
	
	@Override
	public String getLabel() {
		return name;
	}
	
	@Override
	public void setLabel(String s) {
		this.name = s;
	}

	public void setDestination(StateInfo stateInfoDestination) {
		this.destination = stateInfoDestination;
	}

	public void setSource(StateInfo stateInfo) {
		this.source = stateInfo;
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

}
