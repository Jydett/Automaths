package img;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.util.mxCellRenderer;
import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph.STYLE_BEGIN_STATE;
import static fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph.STYLE_DEFAULT_STATE;
import static fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph.STYLE_FINAL_BEGIN_STATE;
import static fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph.STYLE_FINAL_STATE;

public class GenerateImages {

    public static final FiniteStateAutomatonGraph FINITE_STATE_AUTOMATON_GRAPH = new FiniteStateAutomatonGraph(null);

    public static void main(String[] args) throws Exception {
//        generateIcons();
        testTextAlign();
    }

    private static void testTextAlign() throws Exception {
//        generateImage(true, false, 1, "jté", "./test/test.png");
        generateImage(true, false, 1, "TEST", "./test/TESTT.png");
        generateImage(true, false, 1, "test\ntest", "./test/test2.png");
    }

    private static void generateIcons() throws Exception {
        int scale = 2;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                boolean accept = i % 2 == 0;
                boolean initial = j % 2 == 0;
                String imgName = (initial ? "begin_" : "") + (accept ? "final_" : "") + "state.png";
                generateImage(accept, initial, scale, "", "./test/states/" + imgName);
            }
        }
    }

    private static void generateImage(boolean accept, boolean initial, int scale, String label, String outputName) throws Exception {
        BufferedImage bufferedImage = new BufferedImage(51 * scale, 51 * scale, BufferedImage.TYPE_INT_ARGB);
        String style;
        if (accept && initial) {
            style = STYLE_FINAL_BEGIN_STATE;
        } else if (accept) {
            style = STYLE_FINAL_STATE;
        } else if (initial) {
            style = STYLE_BEGIN_STATE;
        } else {
            style = STYLE_DEFAULT_STATE;
        }
        FINITE_STATE_AUTOMATON_GRAPH.getModel().beginUpdate();
        Object cell = FINITE_STATE_AUTOMATON_GRAPH.insertState(null, label, 0, 0, style, accept, initial);
        FINITE_STATE_AUTOMATON_GRAPH.getModel().endUpdate();
        mxGraphics2DCanvas canvas = (mxGraphics2DCanvas) mxCellRenderer
            .drawCells(FINITE_STATE_AUTOMATON_GRAPH, null, scale, null, new mxCellRenderer.CanvasFactory() {
                public mxICanvas createCanvas(int width, int height) {
                    return new mxGraphics2DCanvas(bufferedImage.createGraphics());
                }
            });
        ImageIO.write(bufferedImage, "png", new File(outputName));
        FINITE_STATE_AUTOMATON_GRAPH.getModel().beginUpdate();
        FINITE_STATE_AUTOMATON_GRAPH.removeCells(new Object[]{cell});
        FINITE_STATE_AUTOMATON_GRAPH.getModel().endUpdate();
    }
}
