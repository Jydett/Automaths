package fr.iutvalence.automath.app.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.shape.mxBasicShape;
import com.mxgraph.shape.mxDoubleEllipseShape;
import com.mxgraph.shape.mxEllipseShape;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import fr.iutvalence.automath.app.bridge.InterfaceAutoMathBasicGraph;
import fr.iutvalence.automath.app.bridge.InterfaceAutomaton;
import fr.iutvalence.automath.app.view.shape.BeginEndingStateShape;
import fr.iutvalence.automath.app.view.shape.BeginStateShape;

/**
 * A custom <a target="_parent" href="http://jgraph.github.io/mxgraph/java/docs/com/mxgraph/view/mxGraph.html">{@link com.mxgraph.view.mxGraph}</a> that represent a finite state automaton
 * 
 * The vertex on this graph are called states, the edges are called transitions.
 * The standard states are displayed as a back circle, the accepting states are displayed as a double black circle and the
 * starting state have a little arrow facing them in the top-left side.
 * Vertex value are cell info {@link CellInfo}.
 * 
 * The state on the graph are not resizable, and a transition need a state to be attached to.
 * 
 * Multi-transitions between the same two states are prohibited and therefore merged.
 * Two opposite transitions on the same two states are rearranged to deter said transitions from overlapping.
 * 
 * This graph can be minimised and determinized.
 */
public class FiniteStateAutomatonGraph extends mxGraph implements InterfaceAutoMathBasicGraph {

	public static final String styleState = mxConstants.STYLE_FILLCOLOR+"=white;"+mxConstants.STYLE_FONTCOLOR+"=black;"+mxConstants.STYLE_STROKECOLOR+"=black;"+mxConstants.STYLE_FONTSTYLE+"=1";
	public static final String styleBeginState = styleState+";"+mxConstants.STYLE_PERIMETER+"="+mxConstants.PERIMETER_ELLIPSE+";"+mxConstants.STYLE_SHAPE+"=beginStateShape;"+mxConstants.STYLE_WHITE_SPACE+"=warp";
	public static final String styleDefaultState = styleState+";"+mxConstants.STYLE_PERIMETER+"="+mxConstants.PERIMETER_ELLIPSE+";"+mxConstants.STYLE_SHAPE+"=stateShape;"+mxConstants.STYLE_WHITE_SPACE+"=warp";
	public static final String styleFinalState = styleState+";"+mxConstants.STYLE_PERIMETER+"="+mxConstants.PERIMETER_ELLIPSE+";"+mxConstants.STYLE_SHAPE+"=endingStateShape;"+mxConstants.STYLE_WHITE_SPACE+"=warp";
	public static final String styleTransition = mxConstants.STYLE_ROUNDED+"=1;"+mxConstants.STYLE_STARTARROW + "=" + mxConstants.NONE+";"+mxConstants.STYLE_FONTCOLOR+"=black;"+mxConstants.STYLE_STROKECOLOR+"=black";
	public static final String styleFinalBeginState = styleState+";"+mxConstants.STYLE_PERIMETER+"="+mxConstants.PERIMETER_ELLIPSE+";"+mxConstants.STYLE_SHAPE+"=beginEndingStateShape;"+mxConstants.STYLE_WHITE_SPACE+"=warp";
	
	/**
	 * The radius of a state
	 */
	private static final int STATE_RADIUS = 50;

	/**
	 * Register a shape for a simple state
	 * @param shape the shape that all simple states will use
	 */
	private static void registerStateShape(mxBasicShape shape) {
		registerShape("stateShape",shape);
	}

	/**
	 * Register a shape for a starting state
	 * @param shape the shape that all starting states will use
	 */
	private static void registerBeginStateShape(mxBasicShape shape) {
		registerShape("beginStateShape", shape);
	}

	/**
	 * Register a shape for a accepting state
	 * @param shape the shape that all accepting states will use
	 */
	private static void registerFinalStateShape(mxBasicShape shape) {
		registerShape("endingStateShape", shape);
	}

	/**
	 * Register a shape for a accepting state
	 * @param shape the shape that all accepting states will use
	 */
	private static void registerBeginFinalStateShape(mxBasicShape shape) {
		registerShape("beginEndingStateShape",shape);
	}

	/**
	 * Register a shape with the name of the object that will use it
	 * @param name the name of the object that will receive the shape
	 * @param shape the shape to use
	 */
	private static void registerShape(String name,mxBasicShape shape) {
		mxGraphics2DCanvas.putShape(name, shape);
	}
	
	/**
	 * The basic initialisation of a CustomGraph
	 */
	private static void registerStateShapes() {
		FiniteStateAutomatonGraph.registerBeginStateShape(new BeginStateShape());
		FiniteStateAutomatonGraph.registerStateShape(new mxEllipseShape());
		FiniteStateAutomatonGraph.registerFinalStateShape(new mxDoubleEllipseShape());
		FiniteStateAutomatonGraph.registerBeginFinalStateShape(new BeginEndingStateShape());
	}
	
	/**
	 * The automato//TODO
	 */
	private InterfaceAutomaton automaton;

	public FiniteStateAutomatonGraph(InterfaceAutomaton automaton) {
		super();
		this.automaton = automaton;
		
		registerStateShapes();

		super.setAllowDanglingEdges(false);
		super.setAllowLoops(true);
		super.setGridEnabled(false);
		super.setAutoOrigin(true);
		super.setSplitEnabled(false);
		super.setCellsResizable(false);
		super.setResetEdgesOnConnect(false);
		super.setMultigraph(true);

		mxStylesheet edgeStyle = new mxStylesheet();
		Map<String, Object> edge = new HashMap<>();
		edge.put(mxConstants.STYLE_ROUNDED, true);
		edge.put(mxConstants.STYLE_ORTHOGONAL, false);
		edge.put(mxConstants.STYLE_EDGE, "EntityRelation");
		edge.put(mxConstants.STYLE_STARTARROW, mxConstants.NONE);
		edge.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
		edge.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		edge.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
		edge.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		//edge.put(mxConstants.STYLE_FONTSTYLE, 1);// bold
		//edge.put(mxConstants.STYLE_FONTSIZE,"22");
		edgeStyle.setDefaultEdgeStyle(edge);
		super.setStylesheet(edgeStyle);
		
		registerListeners();
		new mxParallelEdgeLayout(this).execute(this.getDefaultParent());
	}

	private void registerListeners() {
		addListener(mxEvent.REMOVE_CELLS, (arg0, arg1) -> {
			Object[] cells = (Object[]) arg1.getProperty("cells");
			if (cells.length == 0) return;
			for(Object aCell : cells) {
				mxCell cell = (mxCell) aCell;
				if(cell.isEdge()) {
					mxCell pere = (mxCell) cell.getSource();
					int count = pere.getEdgeCount();
					for (int x=0; x< count;x++) {
						mxCell unFils = (mxCell) pere.getEdgeAt(x);
						if (unFils.getSource().equals(cell.getTarget())) {
							mxGeometry geo = unFils.getGeometry();
							try {
								getModel().beginUpdate();
								List<mxPoint> points = geo.getPoints();
								if (points != null && !points.isEmpty()) {
									geo = (mxGeometry) geo.clone();
									geo.setPoints(null);
									getModel().setGeometry(unFils, geo);
									x=count;
								}
							} finally {
								getModel().endUpdate();
							}
						}
					}
				}
			}
		});
	}

	public Object insertState(Object parent, String label,
			double x, double y, String style,boolean accepted,boolean initial) {
		return this.insertVertex(parent, null,label, x, y, STATE_RADIUS, STATE_RADIUS,style,accepted,initial);
	}

	public Object insertVertex(Object parent, String id, Object value,
			double x, double y, double width, double height, String style,boolean accepted,boolean initial) {
		return insertVertex(parent, id, value, x, y, width, height, style,
				false,accepted,initial);
	}
	public Object insertVertex(Object parent, String id, Object value,
			double x, double y, double width, double height, String style,
			boolean relative,boolean accepted,boolean initial) {
		Object vertex = createVertex(parent, id, value, x, y, width, height,
				style, relative, accepted, initial);
		return addCell(vertex, parent);
	}

	public Object createVertex(Object parent, String id, Object value,double x, double y, double width, 
			double height, String style,boolean relative,boolean accepted,boolean initial) {
		Object valueInfo = new StateInfo(accepted, initial,value.toString());
		return super.createVertex(parent, id,valueInfo,x,y,  width, height, style,relative);
	}
	public Object createVertex(Object parent, String id, Object value,double x, double y, double width, 
			double height, String style,boolean relative) {
		return createVertex(parent,id,value,x,y,width,height,style,relative,false,false);
	}

	public Object createEdge(Object parent, String id, Object value,
			Object source, Object target, String style) {
		Object v = new TransitionInfo(value.toString(),((StateInfo)((mxCell)source).getValue()));
		return super.createEdge(parent,id,v,source, target,style);
	}

	public String convertValueToString(Object cell) {
		if (cell instanceof mxCell) {
			Object value = ((mxCell) cell).getValue();
			if (value instanceof CellInfo)
				return ((CellInfo)value).getLabel();
		}
		return super.convertValueToString(cell);
	}

	public void cellLabelChanged(Object cell, Object newValue,boolean autoSize) {
		if (cell instanceof mxCell) {
			((CellInfo) ((mxCell) cell).getValue()).setLabel(newValue.toString());
			super.cellLabelChanged(cell, ((mxCell) cell).getValue(), false);
		}
	}

	
	public void deleteAllElements() {
		removeCells(getChildVertices(getDefaultParent()));
	}
	
	public Set<mxCell> getAllState() {
		Object[] listOfCell = getChildCells(getDefaultParent());
		Set<mxCell> listOfState = new HashSet<>();
		for(Object currentCell : listOfCell) {
			if (((mxCell)currentCell).isVertex()) {
				listOfState.add(((mxCell) currentCell));
			}
		}
		return listOfState;
	}
	
	public Set<mxCell> getAllTransition() {
		Object[] listOfCell = getChildCells(getDefaultParent());
		Set<mxCell> transitions = new HashSet<>();
		for(Object currentCell : listOfCell) {
			if (((mxCell)currentCell).isEdge()) {
				transitions.add(((mxCell) currentCell));
			}
		}
		return transitions;
	}
	
	public Object insertEdge(String c, Object source, Object target) {
		return insertEdge(getDefaultParent(), null,
				c, source, target, FiniteStateAutomatonGraph.styleTransition);
	}
	
	public void minimize() {
		generateGraphWithAutomaton(automaton.minimize(getAllState(), getAllTransition()),true);
	}
	
	public void determinize() {
		generateGraphWithAutomaton(automaton.determinize(getAllState(), getAllTransition()),true);
	}
	
	public void importerExpReg(String text) {
		generateGraphWithAutomaton(automaton.generateAutomateWithExpReg(text),false);
	}
	
	private void generateGraphWithAutomaton(Automaton automate, boolean delParent) {
		removeCells(getChildVertices(getDefaultParent()));
		getModel().beginUpdate();
		Set<State> listOfState = automate.getStates();

		Map<Integer,Object> cellMap = new HashMap<>();
		int i = 0;
		for (State currentStateOfAutomate : listOfState) {
			String style;
			if(currentStateOfAutomate.isAccept() && currentStateOfAutomate.isInitial()) {
				style = FiniteStateAutomatonGraph.styleFinalBeginState;
			} else if (currentStateOfAutomate.isAccept()) {
				style = FiniteStateAutomatonGraph.styleFinalState;
			} else if (currentStateOfAutomate.isInitial()) {
				style = FiniteStateAutomatonGraph.styleBeginState;
			} else {
				style = FiniteStateAutomatonGraph.styleDefaultState;
			}
			Object cell = insertState(getDefaultParent(),Integer.toString(i),
					0d,0d,style,currentStateOfAutomate.isAccept(),currentStateOfAutomate.isInitial());
			cellMap.put(currentStateOfAutomate.hashCode(), cell);
			i++;
		}
		
		for (State currentStateOfAutomate : listOfState) {
			Object source = cellMap.get(currentStateOfAutomate.hashCode());
			Set<Transition> listOfTransition = currentStateOfAutomate.getTransitions();
			for (Transition currentTransitionOfAutomate : listOfTransition) {

				String c = automaton.getString(currentTransitionOfAutomate);
				Object target = cellMap.get(currentTransitionOfAutomate.getDest().hashCode());
				insertEdge(getDefaultParent(), null, 
						c, source, target, FiniteStateAutomatonGraph.styleTransition);
			}
		}
		if (delParent) {
			removeCells(new Object[] {cellMap.get(automate.getInitialState().hashCode())});
		}
		getModel().endUpdate();
	}

	public InterfaceAutomaton getAutomaton() {
		return automaton;
	}

	public Object addEdge(String c, Object source, Object target) {
		getModel().beginUpdate();
		Object obj = insertEdge(getDefaultParent(), null, c, source, target, styleTransition);
		getModel().endUpdate();
		return obj;
	}
	
	public Object addState(String id, double x, double y, boolean accept, boolean initial) {
		String style;
		if (accept && initial) {
			style = styleFinalBeginState;
		} else if (accept) {
			style = styleFinalState;
		} else if (initial) {
			style = styleBeginState;
		} else {
			style = styleDefaultState;
		}
		getModel().beginUpdate();
		Object obj = addState(getDefaultParent(), id, x, y, style, accept, initial);
		getModel().endUpdate();
		return obj;
	}
	
	 public Object addState(Object parent, String label,
 			double x, double y, String style,boolean accepted,boolean initial) {
     	return this.insertVertex(parent, null,label, x, y, STATE_RADIUS, STATE_RADIUS,style,accepted,initial);
     }
}


