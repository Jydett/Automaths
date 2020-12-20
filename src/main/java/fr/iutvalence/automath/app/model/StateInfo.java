package fr.iutvalence.automath.app.model;

import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxResources;
import fr.iutvalence.automath.app.view.utils.StyleUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Contains the information of a <a target="_parent" href="http://jgraph.github.io/mxgraph/java/docs/com/mxgraph/view/mxGraph.html">{@link com.mxgraph.model.mxCell}</a>'s vertex form.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
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
	public void refresh(mxICell c) {
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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return mxResources.get("State")+" "+(label.length()==0?CellInfo.NO_NAME:label)+((accepting | starting)?" :":"")+" \n    "+(accepting ?mxResources.get("Accepting")+"\n    ":"")+(starting ?mxResources.get("Starting")+"\n    ":"");
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid() {
		return true;
	}
	
}
