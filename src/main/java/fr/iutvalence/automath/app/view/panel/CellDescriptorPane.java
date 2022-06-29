package fr.iutvalence.automath.app.view.panel;

import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventSource;
import fr.iutvalence.automath.app.model.CellInfo;
import fr.iutvalence.automath.app.view.utils.AutomathsEvents;

import javax.swing.*;
import java.awt.*;

/**
 * The text inside the cell/state
 */
public class CellDescriptorPane extends JPanel {

	private static final long serialVersionUID = 2100710623417222423L;
	private mxICell cell;
	private final JTextArea textArea;
	//private JCheckBox acceptingCheckBox;
	/**
	 * Build the description's cell with font, ...
	 */
	public CellDescriptorPane(GUIPanel guiPanel) {
		super();
		mxGraphComponent graphComponent = guiPanel.getGraphComponent();
		graphComponent.getGraph().getModel().addListener(AutomathsEvents.SELECTED_INFO_UPDATED, (sender, evt) -> refresh());
		graphComponent.addListener(mxEvent.LABEL_CHANGED, (sender, evt) -> refresh());
		mxEventSource.mxIEventListener possiblyDeletedEvent = (sender, evt) -> {
			if (! graphComponent.getGraph().getModel().contains(cell)) {
				cell = null;
			}
			refresh();
		};
		guiPanel.getUndoManager().addListener(mxEvent.UNDO, possiblyDeletedEvent);
		guiPanel.getUndoManager().addListener(mxEvent.REDO, possiblyDeletedEvent);
		setLayout(new BorderLayout());
		textArea = new JTextArea();
		textArea.setFont(new Font("Arial", Font.PLAIN, 11));
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		add(textArea, BorderLayout.CENTER);
		/*
		acceptingCheckBox = new JCheckBox();
		acceptingCheckBox.addActionListener(e -> {
			if(cell != null) {
				((StateInfo) cell.getValue()).setAccepting(((AbstractButton) e.getSource()).getModel().isSelected());
				((StateInfo) cell.getValue()).refresh(cell);
				graph.getView().clear(cell, false, false);
				graph.getView().validate();
				graph.repaint();
			}
		});
		add(acceptingCheckBox, BorderLayout.NORTH);
		*/
	}

	/**
	 * refresh the text
	 */
	public void refresh() {
		if (cell == null) {
			clear();
		} else {
			String cellData = cell.getValue().toString();
			if (((CellInfo) cell.getValue()).isValid()) {
				textArea.setText(cellData);
				//acceptingCheckBox.setSelected(((StateInfo) cell.getValue()).isAccepting);
			}
		}
	}
	/**
	 * Clear the text if user enter nothing
	 */
	public void clear() {
		textArea.setText("");
		//acceptingCheckBox.setEnabled(false);
	}

	public void setCell(mxICell cell) {
		this.cell = cell;
		this.refresh();
		//acceptingCheckBox.setEnabled(true);
	}
}
