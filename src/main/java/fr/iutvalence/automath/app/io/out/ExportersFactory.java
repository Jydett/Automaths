package fr.iutvalence.automath.app.io.out;

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxGdCodec;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxXmlUtils;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public final class ExportersFactory {

    public static Exporter getForExtension(String ext) {
        return SupportedExtensions.exportersByExt.get(ext);
    }

    public static SupportedExtensions[] supportedExt() {
        return SupportedExtensions.VALUES;
    }

    public static SupportedExtensions getDefaultExt() {
        return SupportedExtensions.PNG_XML;
    }


    @Getter
    public enum SupportedExtensions {
        PDF("pdf", new ExportPDF()),
        PYTHON("py", "Python script", new ExportPython()),
        SVG("svg", new ExportSVG()),
        HTML("html", (g, o) -> {
            o.writeUTF(
            mxXmlUtils.getXml(mxCellRenderer
                    .createHtmlDocument(g, null, 1, null, null)
                    .getDocumentElement()));
        }),
        XML("xml", new ExportXML()),
        XME("xme", "Draw.io", (g, o) -> {
            o.writeUTF(mxXmlUtils.getXml(new mxCodec().encode(g.getModel())));
        }),
        TXT("txt", (g, o) -> {
            o.writeUTF(mxGdCodec.encode(g));
        }),
        VLM("vml", (g, o) -> {
            o.writeUTF(
            mxXmlUtils.getXml(mxCellRenderer
                    .createVmlDocument(g, null, 1, null, null)
                    .getDocumentElement()));
        }),

        //PNG with embedded graph data that can be read by Authomaths
        PNG_XML("png", ExportPNGXML.getINSTANCE())
        ;

        final String ext;
        final String formatFriendlyName;
        final Exporter instance;

        SupportedExtensions(String ext, String formatFriendlyName, Exporter instance) {
            this.ext = ext;
            this.formatFriendlyName = formatFriendlyName;
            this.instance = instance;
        }

        SupportedExtensions(String ext, Exporter instance) {
            this.ext = ext;
            this.formatFriendlyName = ext.toUpperCase();
            this.instance = instance;
        }

        public static final Map<String, Exporter> exportersByExt;

        public static final SupportedExtensions[] VALUES = values();

        static {
            exportersByExt = new HashMap<>();
            for (SupportedExtensions value : values()) {
                exportersByExt.put(value.ext, value.instance);
            }
        }
    }
}
