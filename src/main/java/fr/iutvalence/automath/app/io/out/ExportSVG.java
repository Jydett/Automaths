package fr.iutvalence.automath.app.io.out;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.util.mxXmlUtils;
import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;

import java.io.DataOutputStream;

public class ExportSVG implements Exporter {

    /**
     * {@inheritDoc}
     */
    @Override
    public void exportAutomaton(FiniteStateAutomatonGraph graph, DataOutputStream out) throws Exception {
        mxSvgCanvas canvas = (mxSvgCanvas) mxCellRenderer
                .drawCells(graph, null, 1, null,
                        new mxCellRenderer.CanvasFactory() {
                            public mxICanvas createCanvas(int width, int height) {
                                mxSvgCanvas canvas = new mxSvgCanvas(
                                        mxDomUtils.createSvgDocument(width, height));
                                canvas.setEmbedded(true);
                                return canvas;
                            }
                        });
        out.writeUTF(mxXmlUtils.getXml(canvas.getDocument()));
    }
}
