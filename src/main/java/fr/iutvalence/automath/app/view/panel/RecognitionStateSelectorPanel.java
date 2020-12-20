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

public class RecognitionStateSelectorPanel extends StateSelectorPanel {

	private static final long serialVersionUID = -153937820319654186L;
	private JButton finalState = new JButton();
	private JButton finalBeginState = new JButton();
	
	public RecognitionStateSelectorPanel(mxGraph graph) {
		super(graph);
		init(graph);
	}
	
	private void init(mxGraph graph) {
		finalState.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent me) {
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			public void mouseExited(MouseEvent me) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		finalState.setContentAreaFilled(false);
		finalState.setFocusable(false);
		finalState.setIcon(new ImageIcon(new ImageIcon(GUIPanel.class.getResource("/img/final_state.png")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
		finalState.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(finalState);
		
		finalBeginState.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent me) {
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			public void mouseExited(MouseEvent me) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		finalBeginState.setContentAreaFilled(false);
		finalBeginState.setFocusable(false);
		finalBeginState.setIcon(new ImageIcon(new ImageIcon(GUIPanel.class.getResource("/img/" + "begin_final_state" + ".png")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
		finalBeginState.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(finalBeginState);
		
		mxCell finalCell = new mxCell("", new mxGeometry(0, 0, 50, 50),FiniteStateAutomatonGraph.STYLE_FINAL_STATE);
		mxRectangle boundsf = (mxGeometry) finalCell.getGeometry().clone();
		final mxGraphTransferable tf = new mxGraphTransferable(new Object[] { finalCell }, boundsf);
		
		graph.getModel().setValue(finalCell, new StateInfo(true, false, ""));
		
		mxCell finalBeginCell = new mxCell("", new mxGeometry(0, 0, 50, 50),FiniteStateAutomatonGraph.STYLE_FINAL_BEGIN_STATE);
		mxRectangle boundsfb = (mxGeometry) finalBeginCell.getGeometry().clone();
		final mxGraphTransferable tfb = new mxGraphTransferable(new Object[] { finalBeginCell }, boundsfb);
		
		graph.getModel().setValue(finalBeginCell, new StateInfo(true, true, ""));
		
		DragGestureListener addFinalStateDraggerListener = e -> e.startDrag(null, mxSwingConstants.EMPTY_IMAGE, new Point(),tf, null);
		
		DragSource dragSourceFinal = new DragSource();
		dragSourceFinal.createDefaultDragGestureRecognizer(finalState,DnDConstants.ACTION_COPY, addFinalStateDraggerListener);
		
		DragGestureListener addFinalBeginStateDraggerListener = e -> e.startDrag(null, mxSwingConstants.EMPTY_IMAGE, new Point(),tfb, null);
		
		DragSource dragSourceFinalBegin = new DragSource();
		dragSourceFinalBegin.createDefaultDragGestureRecognizer(finalBeginState,DnDConstants.ACTION_COPY, addFinalBeginStateDraggerListener);
		
		finalCell.setVertex(true);
		finalBeginCell.setVertex(true);
	}
	
}
