package edu.pitt.dbmi.causal.experiment.run;

import edu.cmu.tetrad.data.DataModel;
import edu.cmu.tetrad.graph.Edge;
import edu.cmu.tetrad.graph.EdgeListGraph;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.graph.Node;
import edu.cmu.tetrad.search.IndTestDSep;
import edu.cmu.tetrad.search.Rfci;
import edu.cmu.tetrad.util.Parameters;
import edu.cmu.tetrad.util.Params;
import edu.pitt.dbmi.causal.experiment.data.SimulatedData;
import edu.pitt.dbmi.causal.experiment.independence.wrapper.ProbabilisticTest;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * Apr 26, 2023 2:17:26 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class PagSamplingRfciIndependenceRunner extends PagSamplingRfciRunner {

    public PagSamplingRfciIndependenceRunner(SimulatedData simulatedData, Parameters parameters) {
        super(simulatedData, parameters);
    }

    @Override
    protected Graph runSearch(DataModel dataModel, Parameters parameters) {
        Graph trueGraph = createGraph(dataModel, simulatedData.getPagFromDagGraph());
        IndTestDSep indTestDSeperation = new IndTestDSep(trueGraph, true);

        Rfci rfci = new Rfci((new ProbabilisticTest()).getTest(dataModel, parameters, indTestDSeperation));
        rfci.setDepth(parameters.getInt(Params.DEPTH));
        rfci.setMaxPathLength(parameters.getInt(Params.MAX_PATH_LENGTH));
        rfci.setVerbose(parameters.getBoolean(Params.VERBOSE));

        return rfci.search();
    }

    protected Graph createGraph(DataModel dataModel, Graph originalGraph) {
        Map<String, Node> nodes = dataModel.getVariables().stream()
                .collect(Collectors.toMap(Node::getName, Function.identity()));

        Graph graph = new EdgeListGraph(dataModel.getVariables());
        originalGraph.getEdges().forEach(edge -> {
            graph.addEdge(new Edge(
                    nodes.get(edge.getNode1().getName()),
                    nodes.get(edge.getNode2().getName()),
                    edge.getEndpoint1(),
                    edge.getEndpoint2()));
        });

        return graph;
    }

}
