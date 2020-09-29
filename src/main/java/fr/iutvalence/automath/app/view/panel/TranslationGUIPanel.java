package fr.iutvalence.automath.app.view.panel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;
import fr.iutvalence.automath.app.view.panel.GUIPanel;

public abstract class TranslationGUIPanel extends GUIPanel {

	private static final long serialVersionUID = 1052680525362724789L;

	public TranslationGUIPanel(String mode) {
		super(mode);
	}
	
	public abstract void initializeTabbedMenu();
	
	public abstract void initializeStateSelectorPanel(mxGraph graph);

	@Override
	public void installListeners() {
		//Installs mouse wheel listener for zooming
		MouseWheelListener wheelTracker = e -> {
			if (e.getSource() instanceof mxGraphOutline || e.isControlDown()) {
				mouseWheelMovedEvent(e);
			}
		};
	
		//Handles mouse wheel events in the outline and graph component
		graphComponent.addMouseWheelListener(wheelTracker);
	
		mxGraph graph = graphComponent.getGraph();
		graph.addListener(mxEvent.CONNECT_CELL, new mxIEventListener() {
			int i = 0;
	
			@Override
			public void invoke(Object d, mxEventObject v) {
				i++;
				graph.getModel().addListener(mxEvent.END_UPDATE,
						(sender, evt) -> {
							if (i > 0) {
								i = 0;
								graph.getModel().endUpdate();
								defaultParallelEdgeLayout.execute(graph.getDefaultParent());
								graph.getModel().beginUpdate();
							}
						}
				);
			}
		});
	
		graph.addListener(mxEvent.CELLS_MOVED, (d, v) -> {
			graph.getModel().endUpdate();
			defaultParallelEdgeLayout.execute(graph.getDefaultParent());
			graph.getModel().beginUpdate();
		});
	
		/* Not necessary, used to prevend multiple edges from one state to another
		graph.addListener(mxEvent.CELL_CONNECTED, new mxIEventListener() {
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
							i = sourceEdgeCount;
						}
					}
				}
			}
		}); */
	
		graph.addListener(mxEvent.CELLS_ADDED, (o, evt) -> {
			Object[] cells = (Object[]) evt.getProperties().get("cells");
			if (cells.length == 1 && ((mxCell) cells[0]).isEdge()) {
				defaultParallelEdgeLayout.execute(graph.getDefaultParent());
			}
		});
	
		//Installs the popup menu in the graph component
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// Handles context menu on the Mac where the trigger is on mousepressed
				mouseReleased(e);
			}
			public void mouseReleased(MouseEvent e) {
				Object cell = graphComponent.getCellAt(e.getX(), e.getY());
				if (cell != null) {
					cellDescriptorPanel.setCell((mxICell) cell);
				}
				if (e.isPopupTrigger()) {
					showGraphPopupMenu(e);
				}
			}
			public void mouseExited(MouseEvent e) {
				positionBar.setText("");
			}
		});
		//Installs a mouse motion listener to display the mouse location
		graphComponent.getGraphControl().addMouseMotionListener(
			new MouseMotionListener() {
				public void mouseDragged(MouseEvent e) {
					mouseLocationChanged(e);
				}
	
				public void mouseMoved(MouseEvent e) {
					mouseDragged(e);
				}
			});
	}

}

