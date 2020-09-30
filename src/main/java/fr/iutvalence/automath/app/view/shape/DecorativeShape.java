package fr.iutvalence.automath.app.view.shape;

import com.mxgraph.view.mxCellState;

import java.awt.Graphics2D;

public interface DecorativeShape {
    void drawDecoration(Graphics2D graphics, mxCellState state);
}
