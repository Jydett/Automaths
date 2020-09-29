package fr.iutvalence.automath.app.model;

import com.mxgraph.util.mxResources;

import java.io.Serializable;

/**
 * Contains the information of a <a target="_parent" href="http://jgraph.github.io/mxgraph/java/docs/com/mxgraph/view/mxGraph.html">{@link com.mxgraph.model.mxCell}</a>.
 */
public interface CellInfo extends Serializable {
	/**
	 * The default name of a cell
	 */
	String NO_NAME = mxResources.get("NoName");
	
	/**
	 * Returns the text on the cell
	 * @return Returns the text on the cell
	 */
	String getLabel();
	
	/**
	 * Sets the text on the cell
	 * @param text text to be displayed on the cell
	 */
	void setLabel(String text);

	/**
	 * Returns if the cell is valid
	 * A cell is not valid if its source or destination is null
	 * @return true if this cell is valid
	 */
	boolean isValid();
}
