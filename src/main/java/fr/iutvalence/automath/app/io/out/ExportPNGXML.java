package fr.iutvalence.automath.app.io.out;

import com.mxgraph.io.mxCodec;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.util.png.mxPngEncodeParam;
import com.mxgraph.util.png.mxPngImageEncoder;
import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;
import lombok.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.net.URLEncoder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExportPNGXML implements Exporter {

    @Getter
    public static final ExportPNGXML INSTANCE = new ExportPNGXML();

    @Setter
    public Color bg;
    @Setter
    public mxGraphComponent mxGraphComponent;

    /**
     * {@inheritDoc}
     */
    @Override
    public void exportAutomaton(FiniteStateAutomatonGraph graph, DataOutputStream out) throws Exception {
        // Creates the image for the PNG file
        BufferedImage image = mxCellRenderer.createBufferedImage(graph,
                null, 1, bg, mxGraphComponent.isAntiAlias(), null,
                mxGraphComponent.getCanvas());

        // Creates the URL-encoded XML data
        mxCodec codec = new mxCodec();
        String xml = URLEncoder.encode(
                mxXmlUtils.getXml(codec.encode(graph.getModel())), "UTF-8");
        mxPngEncodeParam param = mxPngEncodeParam
                .getDefaultEncodeParam(image);
        param.setCompressedText(new String[] { "mxGraphModel", xml });

        // Saves as a PNG file
        mxPngImageEncoder encoder = new mxPngImageEncoder(out, param);

        encoder.encode(image);
    }
}
