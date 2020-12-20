package fr.iutvalence.automath.app.view.utils;

import java.util.HashMap;
import java.util.Map;

public final class StyleUtils {

    public static Map<String, String> parseStyle(String style) {
        Map<String, String> res = new HashMap<>();
        for (String attr : style.split(";")) {
            String[] pair = attr.split("=");
            if (pair.length == 2) {
                res.put(pair[0], pair[1]);
            }
        }
        return res;
    }
}
