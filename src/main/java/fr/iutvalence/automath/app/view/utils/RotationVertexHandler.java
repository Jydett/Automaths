package fr.iutvalence.automath.app.view.utils;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.handler.mxVertexHandler;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import fr.iutvalence.automath.app.model.StateInfo;
import fr.iutvalence.automath.app.view.shape.Decorations;

import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class RotationVertexHandler extends mxVertexHandler {
    public static Cursor[] CURSORS = new Cursor[] {
        new Cursor(Cursor.NW_RESIZE_CURSOR), new Cursor(Cursor.N_RESIZE_CURSOR), new Cursor(Cursor.NE_RESIZE_CURSOR),
        new Cursor(Cursor.W_RESIZE_CURSOR),                                           new Cursor(Cursor.E_RESIZE_CURSOR),
        new Cursor(Cursor.SW_RESIZE_CURSOR), new Cursor(Cursor.S_RESIZE_CURSOR), new Cursor(Cursor.SE_RESIZE_CURSOR),
        new Cursor(Cursor.CROSSHAIR_CURSOR), new Cursor(Cursor.MOVE_CURSOR)
    };

    private double initialAngleRad;
    private double currentAngleRad;
    private static final double _5PI4 = 5 * Math.PI / 4;

    public RotationVertexHandler(mxGraphComponent graphComponent, mxCellState state) {
        super(graphComponent, state);
        graphComponent.addListener("afterPaint", (sender, evt) -> {
            Graphics g = (Graphics)evt.getProperty("g");
            RotationVertexHandler.this.paintArrow(g);
        });
    }

    @Override
    protected Rectangle[] createHandles() {
        Rectangle[] h;
        if (((StateInfo) ((mxCell) state.getCell()).getValue()).isStarting()) {
            Rectangle bounds = this.getState().getRectangle();
            int half = mxConstants.HANDLE_SIZE / 2;
            int top = bounds.y - half;
            int s = mxConstants.HANDLE_SIZE;
            int right = bounds.x + bounds.width - half;
            int offset;
            if (this.graphComponent.getGraph().isCellResizable(this.getState().getCell())) {
                int left = bounds.x - half;
                int w2 = bounds.x + bounds.width / 2 - half;
                int h2 = bounds.y + bounds.height / 2 - half;
                int bottom = bounds.y + bounds.height - half;
                h = new Rectangle[10];
                h[0] = new Rectangle(left, top, s, s);
                h[1] = new Rectangle(w2, top, s, s);
                h[2] = new Rectangle(right, top, s, s);
                h[3] = new Rectangle(left, h2, s, s);
                h[4] = new Rectangle(right, h2, s, s);
                h[5] = new Rectangle(left, bottom, s, s);
                h[6] = new Rectangle(w2, bottom, s, s);
                h[7] = new Rectangle(right, bottom, s, s);
                offset = (int) (mxConstants.HANDLE_SIZE * 1.5);
            } else {
                h = new Rectangle[2];
                offset = mxConstants.HANDLE_SIZE;
            }

            //rotation point
            h[h.length - 2] = new Rectangle(right + offset, top - offset, s, s);
        } else {
            h = new Rectangle[1];
        }
        insertLabelHandle(h);
        return h;
    }

    private void insertLabelHandle(Rectangle[] h) {
        int s = mxConstants.LABEL_HANDLE_SIZE;
        mxRectangle bounds = this.state.getLabelBounds();
        h[h.length - 1] = new Rectangle((int) (bounds.getX() + bounds.getWidth() / 2.0D - (double) s), (int) (bounds.getY() + bounds.getHeight() / 2.0D - (double) s), 2 * s, 2 * s);
    }

    @Override
    protected Cursor getCursor(MouseEvent e, int index) {
        if (isRotation()) return CURSORS[8];
        return index >= 0 && index <= CURSORS.length ? CURSORS[index] : null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!e.isConsumed() && this.first != null) {
            if (isRotation()) {
                mxRectangle dirty = mxUtils.getBoundingBox(this.state, this.currentAngleRad * mxConstants.DEG_PER_RAD);
                Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), this.graphComponent.getGraphControl());
                mxRectangle boundingBox = this.state.getBoundingBox();
                double x = boundingBox.getCenterX();
                double y = boundingBox.getCenterY();

                this.currentAngleRad = Math.atan2(y - pt.getY(), x - pt.getX()) + this.initialAngleRad + _5PI4;
                dirty.add(mxUtils.getBoundingBox(this.state, this.currentAngleRad * mxConstants.DEG_PER_RAD));
                dirty.grow(5);
                this.graphComponent.getGraphControl().repaint(dirty.getRectangle());
                e.consume();
            } else {
                super.mouseDragged(e);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isRotation()) {
            double deg = 0.0D;
            Object cell = null;
            if (this.state != null) {
                cell = this.state.getCell();
            }

            deg += this.currentAngleRad * mxConstants.DEG_PER_RAD;
            boolean willExecute = cell != null && this.first != null;
            this.reset();
            if (this.graphComponent.isEnabled() && !e.isConsumed() && willExecute) {
                this.graphComponent.getGraph().setCellStyles(mxConstants.STYLE_ROTATION, String.valueOf(deg), new Object[]{cell});
                this.graphComponent.getGraphControl().repaint();
                e.consume();
            }
        } else {
            super.mouseReleased(e);
        }
    }

    @Override
    public void reset() {
        super.reset();
        mxRectangle dirty = null;
        if (this.state != null && this.first != null) {
            dirty = mxUtils.getBoundingBox(this.state, this.currentAngleRad * mxConstants.DEG_PER_RAD);
            dirty.grow(1.0D);
        }
        this.currentAngleRad = 0.0D;
        if (dirty != null) {
            this.graphComponent.getGraphControl().repaint(dirty.getRectangle());
        }
    }

    public void paintArrow(Graphics g) {
        if (this.state != null && this.first != null) {
            Graphics2D graphics2D = (Graphics2D) g;
            if (this.currentAngleRad != 0.0D) {
                graphics2D.rotate(this.currentAngleRad, this.state.getCenterX(), this.state.getCenterY());
            }

            mxUtils.setAntiAlias(graphics2D, true, false);
            g.setColor(Color.RED);
            Decorations.ARROW.drawDecoration(graphics2D, this.state);
        }
    }

    @Override
    public void start(MouseEvent e, int index) {
        super.start(e, index);
        this.initialAngleRad = mxUtils.getDouble(this.state.getStyle(), mxConstants.STYLE_ROTATION) * mxConstants.RAD_PER_DEG;
        this.currentAngleRad = this.initialAngleRad;
    }

    private boolean isRotation() {
        return index == this.getHandleCount() - 2;
    }
}
