package fr.iutvalence.automath.app.model;

import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;
import fr.iutvalence.automath.app.view.utils.AutomathsEvents;
import fr.iutvalence.automath.app.view.utils.StyleUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Contains the information of a <a target="_parent" href="http://jgraph.github.io/mxgraph/java/docs/com/mxgraph/view/mxGraph.html">{@link com.mxgraph.model.mxCell}</a>'s vertex form.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StateInfo implements CellInfo {

	/**
	 * True if this state is accepting the word
	 */
	private boolean accepting;

	/**
	 * True if the state is a start of the automaton
	 */
	private boolean starting;

	/**
	 * The text on the model of the state
	 */
	private String label;

	/**
	 * Refresh the layout of the state
	 * @param c the cell linked to this {@link StateInfo}
	 */
	public void refresh(mxICell c, mxGraph graph) {
		Map<String, String> cellStyle = StyleUtils.parseStyle(c.getStyle());
		String rotation = cellStyle.get(mxConstants.STYLE_ROTATION);
		String finalStyle;
		if (accepting && starting) {
			finalStyle = FiniteStateAutomatonGraph.STYLE_FINAL_BEGIN_STATE;
		} else if(starting) {
			finalStyle = FiniteStateAutomatonGraph.STYLE_BEGIN_STATE;
		} else if(accepting) {
			finalStyle = FiniteStateAutomatonGraph.STYLE_FINAL_STATE;
		} else {
			finalStyle = FiniteStateAutomatonGraph.STYLE_DEFAULT_STATE;
		}
		if (rotation != null) {
			finalStyle = finalStyle + ";" + mxConstants.STYLE_ROTATION + "=" + rotation;
		}
		c.setStyle(finalStyle);
		if (graph.isCellSelected(c)) {
			((mxEventSource) graph.getModel()).fireEvent(new mxEventObject(AutomathsEvents.SELECTED_INFO_UPDATED, "cell", c));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return mxResources.get("State") + " " +
				(label.length() == 0 ? CellInfo.NO_NAME : label) +
				((accepting | starting) ? " :" : "" ) +
				" \n    " + (accepting ? mxResources.get("Accepting") + "\n    " : "") +
				(starting ? mxResources.get("Starting") + "\n    " : "");
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public CellInfo withLabel(String label) {
		return new StateInfo(accepting, starting, label);
	}

	public StateInfo withValues(boolean starting, boolean accepting, String label) {
		return new StateInfo(accepting, starting, label);
	}

	public StateInfo withStarting(boolean starting) {
		return new StateInfo(accepting, starting, label);
	}

	public StateInfo withAccepting(boolean accepting) {
		return new StateInfo(accepting, starting, label);
	}
}
