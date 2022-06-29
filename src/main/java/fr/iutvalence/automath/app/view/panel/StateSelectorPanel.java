package fr.iutvalence.automath.app.view.panel;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
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

	public StateSelectorPanel(mxGraph graph) {
		init(graph);
	}

	private void init(mxGraph graph) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.CENTER_ALIGNMENT);

		StateInfo info = new StateInfo(false, false, "");
		configureDraggableButton(state, "/img/state.png",
				defaultState(FiniteStateAutomatonGraph.STYLE_DEFAULT_STATE),
				info);
		addSelectedCellModifierOnClick(state, graph, info);
		add(state);

		StateInfo info1 = new StateInfo(false, true, "");
		configureDraggableButton(beginState, "/img/begin_state.png",
				defaultState(FiniteStateAutomatonGraph.STYLE_BEGIN_STATE),
				info1);
		addSelectedCellModifierOnClick(beginState, graph, info1);
		add(beginState);
	}

	protected void addSelectedCellModifierOnClick(JButton button, mxGraph graph, StateInfo info) {
		final boolean accepting = info.isAccepting();
		final boolean starting = info.isStarting();
		button.addActionListener(e -> this.updateSelectedVertexInfo(graph, accepting, starting));
	}

	private void updateSelectedVertexInfo(mxGraph graph, boolean accepting, boolean starting) {
		if (graph.getSelectionCount() > 0) {
			graph.getModel().beginUpdate();
			for (Object selectionCell : graph.getSelectionCells()) {
				if (selectionCell instanceof mxCell && ((mxCell) selectionCell).isVertex()) {
					StateInfo oldInfo = (StateInfo) ((mxCell) selectionCell).getValue();
					StateInfo newInfo = oldInfo.withValues(starting, accepting, oldInfo.getLabel());
					graph.getModel().setValue(selectionCell, newInfo);
					newInfo.refresh(((mxCell) selectionCell), graph);
				}
			}
			graph.getModel().endUpdate();
		}
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
