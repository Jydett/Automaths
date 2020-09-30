package fr.iutvalence.automath.app.view.shape;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.shape.mxEllipseShape;
import com.mxgraph.view.mxCellState;

/**
 * Graphic construction of a final state initial
 */
public class BeginStateShape extends mxEllipseShape {

	/**
	 * Draw the graphic element
	 */
	public void paintShape(mxGraphics2DCanvas canvas, mxCellState state) {
		super.paintShape(canvas, state);
		Decorations.ARROW.drawDecoration(canvas.getGraphics(), state);
	}
}