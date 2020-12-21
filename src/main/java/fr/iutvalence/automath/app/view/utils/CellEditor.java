package fr.iutvalence.automath.app.view.utils;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxCellEditor;
import com.mxgraph.swing.view.mxICellEditor;
import com.mxgraph.view.mxCellState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.EventObject;

public class CellEditor implements mxICellEditor {
	/**
	 *
	 */
	private static final String CANCEL_EDITING = "cancel-editing";

	private final JTextField feild;
	/**
	 *
	 */
	protected int minimumWidth = mxCellEditor.DEFAULT_MIN_WIDTH;

	/**
	 *
	 */
	protected int minimumHeight = mxCellEditor.DEFAULT_MIN_HEIGHT;
	/**
	 * Defines the minimum scale to be used for the editor. Set this to
	 * 0 if the font size in the editor
	 */
	protected double minimumEditorScale = mxCellEditor.DEFAULT_MINIMUM_EDITOR_SCALE;

	/**
	 *
	 */
	private static final String SUBMIT_TEXT = "submit-text";
	private final mxGraphComponent graphComponent;
	/**
	 *
	 */
	transient Object textEnterActionMapKey;

	/**
	 *
	 */
	transient KeyStroke escapeKeystroke = KeyStroke.getKeyStroke("ESCAPE");

	/**
	 *
	 */
	transient KeyStroke enterKeystroke = KeyStroke.getKeyStroke("ENTER");

	/**
	 *
	 */
	protected AbstractAction cancelEditingAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			stopEditing(true);
		}
	};
	private Object editingCell;
	/**
	 *
	 */
	protected transient EventObject trigger;
	/**
	 *
	 */
	protected AbstractAction textSubmitAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			stopEditing(false);
		}
	};

	public CellEditor(mxGraphComponent graphComponent) {
		this.graphComponent = graphComponent;
		feild = new JTextField();
		feild.setOpaque(false);
		feild.setHorizontalAlignment(JTextField.CENTER);
		feild.setBorder(BorderFactory.createEmptyBorder());
		feild.getActionMap().put(CANCEL_EDITING, cancelEditingAction);
		feild.getActionMap().put(SUBMIT_TEXT, textSubmitAction);

		textEnterActionMapKey = feild.getInputMap().get(enterKeystroke);
	}

	@Override
	public Object getEditingCell()
	{
		return editingCell;
	}

	@Override
	public void startEditing(Object cell, EventObject evt) {
		if (editingCell != null) {
			stopEditing(true);
		}

		mxCellState state = graphComponent.getGraph().getView().getState(cell);
		if (state != null) {
			editingCell = cell;
			trigger = evt;
			
			double scale = Math.max(minimumEditorScale, graphComponent
					.getGraph().getView().getScale());

			feild.setBounds(getEditorBounds(state, scale));
			feild.setVisible(true);
			String value = getInitialValue(state, evt);

			feild.setText(value);
			graphComponent.getGraphControl().add(feild, 0);

			if (isHideLabel(state)) {
				graphComponent.redraw(state);
			}

			feild.revalidate();
			feild.requestFocusInWindow();
			feild.selectAll();

			configureActionMaps();
		}
	}

	protected void configureActionMaps() {
		InputMap textInputMap = feild.getInputMap();

		// Adds handling for the escape key to cancel editing
		textInputMap.put(escapeKeystroke, cancelEditingAction);

		// Adds handling for shift-enter and redirects enter to stop editing
		if (graphComponent.isEnterStopsCellEditing()) {
			//textInputMap.put(shiftEnterKeystroke, textEnterActionMapKey);

			textInputMap.put(enterKeystroke, SUBMIT_TEXT);
		} else {
			textInputMap.put(enterKeystroke, textEnterActionMapKey);
		}
	}

	protected boolean isHideLabel(mxCellState state) {
		return true;
	}

	@Override
	public void stopEditing(boolean cancel) {
		if (editingCell != null) {
			feild.transferFocusUpCycle();
			Object cell = editingCell;
			editingCell = null;

			if (!cancel) {
				EventObject trig = trigger;
				trigger = null;
				graphComponent.labelChanged(cell, getCurrentValue(), trig);
			} else {
				mxCellState state = graphComponent.getGraph().getView()
						.getState(cell);
				graphComponent.redraw(state);
			}
			feild.setVisible(false);
			if (feild.getParent() != null) {
				feild.getParent().remove(feild);
			}

			graphComponent.requestFocusInWindow();
		}
	}

	/**
	 * Returns the current editing value.
	 */
	public String getCurrentValue() {
		return feild.getText();
	}

	/**
	 * Returns the bounds to be used for the editor.
	 */
	public Rectangle getEditorBounds(mxCellState state, double scale) {
		Rectangle bounds;
		if (useLabelBounds(state)) {
			bounds = state.getLabelBounds().getRectangle();
			//bounds.height += 10;
			bounds.width = (int) Math.max(bounds.getWidth(),
					Math.round(minimumWidth * scale));
			bounds.height = (int) Math.max(bounds.getHeight(),
					Math.round(minimumHeight * scale));
			bounds.setLocation((int)state.getLabelBounds().getCenterX() - bounds.width/2, (int) state.getLabelBounds().getCenterY() - bounds.height/2);
		} else {
			bounds = state.getRectangle();
			bounds.width = (int) Math.max(bounds.getWidth(),
					Math.round(minimumWidth * scale));
			bounds.height = (int) Math.max(bounds.getHeight(),
					Math.round(minimumHeight * scale));
			bounds.setLocation((int)state.getCenterX() - bounds.width/2, (int) state.getCenterY() - bounds.height/2);
		}

		return bounds;
	}

	/**
	 * Gets the initial editing value for the given cell.
	 */
	protected String getInitialValue(mxCellState state, EventObject trigger) {
		return graphComponent.getEditingValue(state.getCell(), trigger);
	}

	/**
	 * Returns true if the label bounds of the state should be used for the
	 * editor.
	 */
	protected boolean useLabelBounds(mxCellState state)
	{
		mxIGraphModel model = state.getView().getGraph().getModel();
		mxGeometry geometry = model.getGeometry(state.getCell());

		return ((geometry != null && geometry.getOffset() != null
				&& !geometry.isRelative() && (geometry.getOffset().getX() != 0 || geometry
				.getOffset().getY() != 0)) || model.isEdge(state.getCell()));
	}
}