package fr.iutvalence.automath.app.model;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import fr.iutvalence.automath.app.exceptions.GraphIsEmptyException;
import fr.iutvalence.automath.app.exceptions.NextCallOnNonStartedSimulationException;
import fr.iutvalence.automath.app.exceptions.NoInitialStateException;
import fr.iutvalence.automath.app.exceptions.WordIsEmptyException;
import fr.iutvalence.automath.app.view.panel.GUIPanel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * SimulationProvider is the class that contains the algorithm to simulate the automaton
 */
public class SimulationProvider {

	private final GUIPanel editor;

	/**
	 * The graph of the application
	 */
	private final mxGraph graph;

	/**
	 * Number of loop made in the simulation
	 */
	private int loopNumber = 0;

	/**
	 * The word has tested
	 */
	private String word;

	/**
	 * The states in which we went
	 */
	private ArrayList<mxCell> lastStates;

	/**
	 * The simulation is launching
	 */
	private boolean isStarted = false;

	/**
	 * The simulation is on a state
	 */
	private boolean isStatePhase;

	/**
	 * On the END of the simulation
	 */
	private boolean hasEnd = false;

	/**
	 * List of red colored states
	 */
	private final ArrayList<mxCell> red = new ArrayList<>();
	/**
	 * List of green colored states
	 */
	private final ArrayList<mxCell> green = new ArrayList<>();
	
	/**
	 * A constructor of SimulationProvider, with the parameter graph
	 * @param editor The panel of application
	 */
	public SimulationProvider(GUIPanel editor) {
		this.editor = editor;
		this.graph = editor.getGraphComponent().getGraph();
	}
	
	/**
	 * Informed if the simulation is on a state or transition
	 * @return 	<code>true</code> if the simulation is on a state; 
     *			<code>false</code> otherwise.
	 */
	public boolean isStatePhase() {
		return isStatePhase;
	}

	/**
	 * Tests the recognition of the automaton at once
	 * @param word The word has tested
	 * @return The instance of the object
	 * @throws WordIsEmptyException if the word provide in empty
	 * @throws GraphIsEmptyException if the graph is empty
	 * @throws NoInitialStateException if the graph doesn't have any initial state
	 */
	public SimulationState toTheEnd(String word)throws WordIsEmptyException, GraphIsEmptyException, NoInitialStateException{
		if (!isStarted) {
			start(word);
		}
		SimulationState s = SimulationState.END;
		try {
			s = next();
			while(s == SimulationState.RUNNING) {
				s = next();
			}
		} catch (NextCallOnNonStartedSimulationException e) {
			// impossible since we started the simulation above
		}
		return s;
	}
	
	/**
	 * Start the simulation of the automaton
	 * @param word The word has tested
	 * @throws WordIsEmptyException if the word provide in empty
	 * @throws GraphIsEmptyException if the graph is empty
	 * @throws NoInitialStateException if the graph doesn't have any initial state
	 */
	public void start(String word) throws WordIsEmptyException, GraphIsEmptyException, NoInitialStateException {
		if (word.isEmpty()) {
			throw new WordIsEmptyException();
		}
		lastStates = new ArrayList<>();
		char c = 0x2202;
		this.word = word.replaceAll(String.valueOf(c),"");
		Object[] cells = graph.getChildCells(graph.getDefaultParent());
		if (cells.length == 0) {
			throw new GraphIsEmptyException();
		}
		boolean hasInitial = false;
		for (Object cellO : cells) {
			mxCell cell = (mxCell) cellO;
			if(cell.isVertex()) {
				if(((StateInfo) graph.getModel().getValue(cell)).isStarting()) {
					lastStates.add(cell);
					hasInitial = true;
				}
			}
		}
		if (!hasInitial) {
			throw new NoInitialStateException();
		}
		
		color(lastStates.toArray(),"red");
		this.isStarted = true;
		this.isStatePhase = true;
	}
	
	/**
	 * Go to the next letter in the simulation
	 * @return The instance of the object
	 * @throws NextCallOnNonStartedSimulationException if this method is called when the simulation isn't started
	 */
	public SimulationState next() throws NextCallOnNonStartedSimulationException {//FIXME BUG  si word = no_state_found ou END ou reconnu
	    if (loopNumber > word.length()) {
	        return SimulationState.END;
	    }
		boolean exist = false;
		SimulationState res = SimulationState.RUNNING;
	    ArrayList<mxCell> futureStates = new ArrayList<>();
	    if (loopNumber < word.length()) {
	        if (!isStarted) throw new NextCallOnNonStartedSimulationException();
	        resetColor(lastStates.toArray());
	        if (isStatePhase) {
	            char c = word.charAt(loopNumber);
	            for (mxCell cell: lastStates) {
	                int ec = cell.getEdgeCount();
	                for (int x = 0; x < ec; x++) {
	                    mxCell e = (mxCell) cell.getEdgeAt(x);
	                    if (e.getSource().equals(cell)) {
		                    if (e.isEdge()) {
		                        TransitionInfo ti = (TransitionInfo) e.getValue();
								final String label = ti.getLabel();
								if (label.indexOf(c) != -1 || label.equals("*")) {
		                            futureStates.add(e);
		                            exist = true;
		                        }
		                    }
		                }
		            }
	            }
	            if (! exist) {
	            	return SimulationState.NO_STATE_FOUND;
				}
	            loopNumber++;
	        } else {
	            for (mxCell cell: lastStates) {
	                if (cell.isEdge()) {
	                    futureStates.add((mxCell) cell.getTarget());
	                }
	            }
	        }
	    } else if (loopNumber == word.length()) {
	        resetColor(lastStates.toArray());
	    	for (mxCell cell : lastStates) {
                if (cell.isEdge()) {
                    futureStates.add((mxCell) cell.getTarget());
                }
            }
	    	for (mxCell finale : futureStates) {
	    		lastStates.clear();
	    		StateInfo si = (StateInfo) finale.getValue();
	    		if (si.isAccepting()) {
	    			res = SimulationState.ACCEPTED;
	    			green.add(finale);
	    			hasEnd = true;
	    		} else {
	    			red.add(finale);
	    		}
	    	}
	    	if (hasEnd) {
	    		color(red.toArray(),"red");
	    		color(green.toArray(),"green");
	    		return res;
	    	}
	    	loopNumber++;
	    }
		Set<mxCell> set = new HashSet<>(futureStates);
	    lastStates.clear();
	    lastStates.addAll(set);
	    color(lastStates.toArray(),"red");
	    isStatePhase = !isStatePhase;
		return res;
	}

	/**
	 * Reset the simulator
	 */
	public void reset() {
		hasEnd = false;
		resetColor(lastStates.toArray());
		resetColor(green.toArray());
		resetColor(red.toArray());
		editor.getUndoManager().setUndoAllowed(true);
		red.clear();
		green.clear();
		lastStates.clear();
		isStarted = false;
		loopNumber = 0;
	}
	
	/**
	 * Put a color on a set of mxCell
	 * @param array Object list
	 * @param color The color
	 */
	private void color(Object[] array, String color) {
		graph.getModel().beginUpdate();
		graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, color,array);
		graph.setCellStyles(mxConstants.STYLE_FONTCOLOR, color,array);
		graph.getModel().endUpdate();
	}
	
	/**
	 * Reset a color on a set of mxCell
	 * @param array Object list
	 */
	private void resetColor(Object[] array) {
		graph.getModel().beginUpdate();
		graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "black",array);
		graph.setCellStyles(mxConstants.STYLE_FONTCOLOR, "black",array);
		graph.getModel().endUpdate();
	}
	
	/**
	 * Returns the number of loop made
	 * @return the number of loop made
	 */
	public int getLoopNumber() {
		return loopNumber;
	}
	
	/**
	 * the state types that the simulation can take
	 * <ul>
	 * <li>RUNNING : the simulation is RUNNING</li>
	 * <li>ACCEPTED : the automaton is recognized</li>
	 * <li>NO_STATE_FOUND : the automaton is not recognized</li>
	 * <li>END : the simulation is finish</li>
	 * </ul>
	 */
	public enum SimulationState {
		RUNNING, ACCEPTED, NO_STATE_FOUND, END
	}
	
}
