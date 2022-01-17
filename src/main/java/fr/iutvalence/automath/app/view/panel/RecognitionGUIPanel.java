package fr.iutvalence.automath.app.view.panel;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;

public abstract class RecognitionGUIPanel extends GUIPanel {
	
	private static final long serialVersionUID = -7750129522526343679L;

	public RecognitionGUIPanel(String mode) {
		super(mode);
	}
	
	public abstract void initializeTabbedMenu();
	
	public abstract void initializeStateSelectorPanel(mxGraph graph);

	private final mxCell[] nextFocus = new mxCell[1];

	@Override
	public void installListeners() {
		super.installListeners();

		/* Used to prevent multiple edges from one state to another */
		mxGraph graph = graphComponent.getGraph();

		graph.addListener(mxEvent.CELL_CONNECTED, new mxEventSource.mxIEventListener() {
			@Override
			public void invoke(Object sender, mxEventObject evt) {
				Boolean source = (Boolean) evt.getProperty("source");
				boolean previous = evt.getProperties().containsKey("previous");
				if (!source || previous) {
					addTransitionAndCollapse((mxCell) evt.getProperties().get("edge"));
				}
			}

			private void addTransitionAndCollapse(mxCell edge) {
				mxICell source = edge.getSource();
				int sourceEdgeCount = source.getEdgeCount();
				String edgeTargetId = edge.getTarget().getId();
				for (int i = 0; i < sourceEdgeCount; i++) {
					mxCell currentSourceEdge = (mxCell) source.getEdgeAt(i);
					if (! edge.getId().equals(currentSourceEdge.getId())) {
						if (currentSourceEdge.getTarget().getId().equals(edgeTargetId) &&
								currentSourceEdge.getSource().getId().equals(source.getId())) {
							graph.getModel().remove(edge);
							// En fait meme si on enleve comme ca les traitements sont fait
							// par d'autre listeners dans jgraphX
							// il n'y a pas moyen d'annuler proprement l'event
							// donc on ne peut pas par exemple changer la selection :/
							// on doit donc passer par cette solution dégeu d'un deuxième listener de mxEvent.CONNECT
							// sur le connectionHandler
							nextFocus[0] = currentSourceEdge;
							i = sourceEdgeCount;
						}
					}
				}
			}
		});

		// voir commentaire plus haut
		graphComponent.getConnectionHandler().addListener(mxEvent.CONNECT, (sender, evt) -> {
			if (nextFocus[0] != null) {
				graph.setSelectionCell(nextFocus[0]);
				nextFocus[0] = null;
			}
		});
	}

}
