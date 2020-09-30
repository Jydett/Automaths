package fr.iutvalence.automath.app.view.shape;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.shape.mxDoubleEllipseShape;
import com.mxgraph.view.mxCellState;

/**
 * Graphic construction of a final state initial and recognized
 */
public class BeginEndingStateShape extends mxDoubleEllipseShape {

	/**
	 * Draw the graphic element
	 */
	public void paintShape(mxGraphics2DCanvas canvas, mxCellState state) {
		super.paintShape(canvas, state);
		Decorations.ARROW.drawDecoration(canvas.getGraphics(), state);
	}
}