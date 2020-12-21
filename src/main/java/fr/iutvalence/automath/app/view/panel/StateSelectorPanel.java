package fr.iutvalence.automath.app.view.panel;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxRectangle;
import fr.iutvalence.automath.app.model.CellInfo;
import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;
import fr.iutvalence.automath.app.model.StateInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StateSelectorPanel extends JPanel {

	private static final long serialVersionUID = 1971112299974853933L;

	private final JButton state = new JButton();
	private final JButton beginState = new JButton();
	
	public StateSelectorPanel() {
		init();
	}
	
	private void init() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.CENTER_ALIGNMENT);

		configureDraggableButton(state, "/img/state.png",
				defaultState(FiniteStateAutomatonGraph.STYLE_DEFAULT_STATE),
				new StateInfo(false, false, ""));
		add(state);

		configureDraggableButton(beginState, "/img/begin_state.png",
				defaultState(FiniteStateAutomatonGraph.STYLE_BEGIN_STATE),
				new StateInfo(false, true, ""));
		add(beginState);
	}

	protected mxCell defaultState(String style) {
		mxCell res = new mxCell("", new mxGeometry(0, 0, 50, 50), style);
		res.setVertex(true);
		return res;
	}

	protected void configureDraggableButton(JButton button, String iconPath, mxCell toCopy, CellInfo info) {
		button.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent me) {
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			public void mouseExited(MouseEvent me) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setContentAreaFilled(false);
		button.setFocusable(false);
		button.setIcon(new ImageIcon(new ImageIcon(StateSelectorPanel.class.getResource(iconPath))
				.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));

		mxRectangle bounds = (mxGeometry) toCopy.getGeometry().clone();
		toCopy.setValue(info);

		final mxGraphTransferable t = new mxGraphTransferable(new Object[] { toCopy }, bounds);

		new DragSource().createDefaultDragGestureRecognizer(button, DnDConstants.ACTION_COPY, e ->
			e.startDrag(null, mxSwingConstants.EMPTY_IMAGE, new Point(), t, null));
	}

}
