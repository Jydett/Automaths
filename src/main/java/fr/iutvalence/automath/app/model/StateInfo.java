package fr.iutvalence.automath.app.model;

import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxResources;

/**
 * Contains the information of a <a target="_parent" href="http://jgraph.github.io/mxgraph/java/docs/com/mxgraph/view/mxGraph.html">{@link com.mxgraph.model.mxCell}</a>'s vertex form.
 */
public class StateInfo implements CellInfo {

	/**
	 * True if this state is accepting the word
	 */
	public boolean isAccepting;
	
	/**
	 * True if the state is a start of the automaton
	 */
	public boolean isStarting;
	
	/**
	 * The text on the model of the state
	 */
	public String name;
	
	/**
	 * Construct a new State info with various informations
	 * @param isAccepting whether this is an accepting state or not
	 * @param isStarting whether this is a starting state or not
	 * @param name the name displayed onto the state
	 */
	public StateInfo(boolean isAccepting, boolean isStarting, String name) {
		this.isAccepting = isAccepting;
		this.isStarting = isStarting;
		this.name = name;
	}
	
	/**
	 * Set the acceptance of this state like a builder
	 * @param isAccepting whether this is an accepting state or not
	 * @return the current state
	 */
	public StateInfo setAccepting(boolean isAccepting) {
		this.isAccepting = isAccepting;
		return this;
	}
	
	/**
	 * Set if this state start the automaton or not like a builder
	 * @param isStarting whether this is a starting state or not
	 * @return the current state
	 */
	public StateInfo setStarting(boolean isStarting) {
		this.isStarting = isStarting;
		return this;
	}

	/**
	 * Refresh the layout of the state
	 * @param c the cell linked to this {@link StateInfo}
	 */
	public void refresh(mxICell c) {
		if(isAccepting && isStarting) {
			c.setStyle(FiniteStateAutomatonGraph.styleFinalBeginState);
		}else if(isStarting){
			c.setStyle(FiniteStateAutomatonGraph.styleBeginState);
		}else if(isAccepting) {
			c.setStyle(FiniteStateAutomatonGraph.styleFinalState);
		}else {
			c.setStyle(FiniteStateAutomatonGraph.styleDefaultState);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isAccepting ? 1231 : 1237);
		result = prime * result + (isStarting ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return mxResources.get("State")+" "+(name.length()==0?CellInfo.NO_NAME:name)+((isAccepting|isStarting)?" :":"")+" \n    "+(isAccepting?mxResources.get("Accepting")+"\n    ":"")+(isStarting?mxResources.get("Starting")+"\n    ":"");
	}
	

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StateInfo other = (StateInfo) obj;
		if (isAccepting != other.isAccepting)
			return false;
		if (isStarting != other.isStarting)
			return false;
		if (name == null) {
			return other.name == null;
		} else return name.equals(other.name);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return name;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLabel(String s) {
		this.name = s;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid() {
		return true;
	}
	
}
