package fr.iutvalence.automath.app.io.out;

import com.mxgraph.view.mxGraph;
import fr.iutvalence.automath.app.model.FiniteStateAutomatonGraph;

import java.io.DataOutputStream;

@FunctionalInterface
public interface Exporter {
    /**
     * Convert the {@link mxGraph} to save it in the given {@link DataOutputStream}.
     * The format of the serialization process in implementation dependant.
     * @param graph to save
     * @param out the stream where the graph will be saved
     */
    void exportAutomaton(FiniteStateAutomatonGraph graph, DataOutputStream out) throws Exception;

}
