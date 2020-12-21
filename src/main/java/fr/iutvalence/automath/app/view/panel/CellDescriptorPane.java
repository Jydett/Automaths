package fr.iutvalence.automath.app.view.panel;

import java.awt.*;

import javax.swing.*;

import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;
import fr.iutvalence.automath.app.model.CellInfo;

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
	public CellDescriptorPane(mxGraph graph) {
		super();
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
		String cellData = cell.getValue().toString();
		if (((CellInfo) cell.getValue()).isValid()) {		
			textArea.setText(cellData);
			//acceptingCheckBox.setSelected(((StateInfo) cell.getValue()).isAccepting);
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