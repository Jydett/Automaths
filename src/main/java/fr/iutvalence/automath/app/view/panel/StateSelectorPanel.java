package fr.iutvalence.automath.app.view.panel;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;
import fr.iutvalence.automath.app.model.StateInfo;

public class StateSelectorPanel extends JPanel {

	private static final long serialVersionUID = 1971112299974853933L;

	private JButton state = new JButton();
	private JButton beginState = new JButton();
	
	public StateSelectorPanel(mxGraph graph) {
		init(graph);
	}
	
	private void init(mxGraph graph) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.CENTER_ALIGNMENT);
		
		state.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent me) {
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			public void mouseExited(MouseEvent me) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		state.setContentAreaFilled(false);
		state.setFocusable(false);
		state.setIcon(new ImageIcon(new ImageIcon(GUIPanel.class.getResource("/img/state.png")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
		state.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(state);
		
		beginState.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent me) {
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			public void mouseExited(MouseEvent me) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		beginState.setContentAreaFilled(false);
		beginState.setFocusable(false);
		beginState.setIcon(new ImageIcon(new ImageIcon(GUIPanel.class.getResource("/img/begin_state.png")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
		beginState.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(beginState);
		
		mxCell cell = new mxCell("", new mxGeometry(0, 0, 50, 50),FiniteStateAutomatonGraph.styleDefaultState);
		mxRectangle bounds = (mxGeometry) cell.getGeometry().clone();
		
		graph.getModel().setValue(cell,(Object) new StateInfo(false, false, ""));
		
		final mxGraphTransferable t = new mxGraphTransferable(new Object[] { cell }, bounds);
		
		mxCell beginCell = new mxCell("", new mxGeometry(0, 0, 50, 50),FiniteStateAutomatonGraph.styleBeginState);
		mxRectangle boundsb = (mxGeometry) beginCell.getGeometry().clone();
		final mxGraphTransferable tb = new mxGraphTransferable(new Object[] { beginCell }, boundsb);
		
		graph.getModel().setValue(beginCell,(Object) new StateInfo(false, true, ""));
		
		DragGestureListener addStateDraggerListener = e -> e.startDrag(null, mxSwingConstants.EMPTY_IMAGE, new Point(), t, null);
		
		DragSource dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(state, DnDConstants.ACTION_COPY, addStateDraggerListener);
		
		DragGestureListener addBeginStateDraggerListener = e -> e.startDrag(null, mxSwingConstants.EMPTY_IMAGE, new Point(),tb, null);
		
		DragSource dragSourceBegin = new DragSource();
		dragSourceBegin.createDefaultDragGestureRecognizer(beginState, DnDConstants.ACTION_COPY, addBeginStateDraggerListener);
		
		cell.setVertex(true);
		beginCell.setVertex(true);
	}
	
}
