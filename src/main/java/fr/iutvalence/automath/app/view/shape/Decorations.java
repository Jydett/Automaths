package fr.iutvalence.automath.app.view.shape;

import com.mxgraph.view.mxCellState;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public enum Decorations implements DecorativeShape {
    ARROW() {
        @Override
        public void drawDecoration(Graphics2D graphics, mxCellState state) {
            Rectangle temp = state.getRectangle();

            double dy =  (1-(Math.sqrt(2)/2))*temp.height/2;
            double dx =  (1-(Math.sqrt(2)/2))*temp.width/2;

            graphics.drawLine((int) (0+state.getX()), (int) (0+state.getY()), (int) (dx+state.getX()), (int) (dy+state.getY()));
            graphics.drawLine((int) (0+state.getX()), (int) (dy+state.getY()), (int) (dx+state.getX()), (int) (dy+state.getY()));
            graphics.drawLine((int) (dx+state.getX()), (int) (0+state.getY()), (int) (dx+state.getX()), (int) (dy+state.getY()));

        }
    },
    TARGET() {
        @Override
        public void drawDecoration(Graphics2D graphics, mxCellState state) {
            Rectangle rectangle = state.getRectangle();
            graphics.drawLine((int)rectangle.getCenterX(), rectangle.y, (int)rectangle.getCenterX(), rectangle.y + rectangle.height);
            graphics.drawLine(rectangle.x, (int) rectangle.getCenterY(), rectangle.x + rectangle.width, (int) rectangle.getCenterY());
        }
    }
}
