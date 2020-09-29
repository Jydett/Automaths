package fr.iutvalence.automath.app.view.shape;

import java.awt.Rectangle;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.shape.mxEllipseShape;
import com.mxgraph.view.mxCellState;

/**
 * Graphic construction of a final state initial
 */
public class BeginStateShape extends mxEllipseShape
{

	/**
	 * Draw the graphic element
	 */
	public void paintShape(mxGraphics2DCanvas canvas, mxCellState state) {
		super.paintShape(canvas, state);
		
		Rectangle temp = state.getRectangle();
		
		double dy =  (1-(Math.sqrt(2)/2))*temp.height/2; 
		double dx =  (1-(Math.sqrt(2)/2))*temp.width/2; 
		
		canvas.getGraphics().drawLine((int) (0+state.getX()), (int) (0+state.getY()), (int) (dx+state.getX()), (int) (dy+state.getY()));
		canvas.getGraphics().drawLine((int) (0+state.getX()), (int) (dy+state.getY()), (int) (dx+state.getX()), (int) (dy+state.getY()));
		canvas.getGraphics().drawLine((int) (dx+state.getX()), (int) (0+state.getY()), (int) (dx+state.getX()), (int) (dy+state.getY()));
	}
}