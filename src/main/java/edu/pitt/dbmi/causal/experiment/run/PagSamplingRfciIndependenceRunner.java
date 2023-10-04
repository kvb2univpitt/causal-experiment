package edu.pitt.dbmi.causal.experiment.run;

import edu.cmu.tetrad.data.DataModel;
import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.graph.Edge;
import edu.cmu.tetrad.graph.EdgeListGraph;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.graph.Node;
import edu.cmu.tetrad.search.Rfci;
import edu.cmu.tetrad.search.test.IndTestDSep;
import edu.cmu.tetrad.search.utils.GraphSearchUtils;
import edu.cmu.tetrad.util.GraphSampling;
import edu.cmu.tetrad.util.Parameters;
import edu.cmu.tetrad.util.Params;
import edu.pitt.dbmi.causal.experiment.calibration.EdgeValue;
import edu.pitt.dbmi.causal.experiment.calibration.GeneralValue;
import edu.pitt.dbmi.causal.experiment.calibration.GeneralValueStatistics;
import edu.pitt.dbmi.causal.experiment.calibration.GraphData;
import edu.pitt.dbmi.causal.experiment.calibration.GraphStatistics;
import edu.pitt.dbmi.causal.experiment.data.SimulatedData;
import edu.pitt.dbmi.causal.experiment.independence.wrapper.ProbabilisticTest;
import edu.pitt.dbmi.causal.experiment.tetrad.Graphs;
import edu.pitt.dbmi.causal.experiment.util.FileIO;
import edu.pitt.dbmi.causal.experiment.util.GraphDetails;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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

    protected Graph runSearch(DataModel dataModel, Parameters parameters, Set<GeneralValue> generalValues, Set<String> condProbLabels) {
        Graph trueGraph = createGraph(dataModel, simulatedData.getPagFromDagGraph());
        IndTestDSep indTestDSeperation = new IndTestDSep(trueGraph, true);

        Rfci rfci = new Rfci((new ProbabilisticTest()).getTest(dataModel, parameters, indTestDSeperation, generalValues, condProbLabels));
        rfci.setDepth(parameters.getInt(Params.DEPTH));
        rfci.setMaxPathLength(parameters.getInt(Params.MAX_PATH_LENGTH));
        rfci.setVerbose(parameters.getBoolean(Params.VERBOSE));

        return rfci.search();
    }

    @Override
    public void run(Path parentOutDir) throws Exception {
        Graph pagFromDagGraph = simulatedData.getPagFromDagGraph();
        DataSet dataSet = simulatedData.getDataSet();
        Path dirOut = FileIO.createSubdirectory(parentOutDir, "pag_sampling_rfci");

        final LocalDateTime startDateTime = LocalDateTime.now();
        final long startTime = System.nanoTime();

        int numOfSearchRuns = 0;
        List<Graph> graphs = new LinkedList<>();
        Set<GeneralValue> generalValues = new HashSet<>();
        Set<String> condProbLabels = new HashSet<>();
        int numRandomizedSearchModels = parameters.getInt(Params.NUM_RANDOMIZED_SEARCH_MODELS);
        while (graphs.size() < numRandomizedSearchModels) {
            System.out.printf("Starting search: %d%n", numOfSearchRuns + 1);
            Graph graph = runSearch(dataSet, parameters, generalValues, condProbLabels);
            if (GraphSearchUtils.isLegalPag(graph).isLegalPag()) {
                System.out.println("Search returns legal PAG.");
                graphs.add(graph);
            } else {
                System.out.println("Search does not return legal PAG.");
            }
            numOfSearchRuns++;
        }

        final long endTime = System.nanoTime();
        final LocalDateTime endDateTime = LocalDateTime.now();
        final long duration = endTime - startTime;

        Graph searchGraph = GraphSampling.createGraphWithHighProbabilityEdges(graphs);

        // tail-arrow (directed)
        Path edgeTypeDir = FileIO.createSubdirectory(dirOut, "tail_arrow");
        String edgeTypeOutputDir = edgeTypeDir.toString();
        Set<EdgeValue> graphData = GraphData.examineDirectEdge(searchGraph, pagFromDagGraph);
        Set<EdgeValue> edgeData = GraphData.examineEdges(searchGraph, pagFromDagGraph);
        GraphStatistics graphStats = new GraphStatistics(graphData, edgeData);
        graphStats.saveGraphData(Paths.get(edgeTypeOutputDir, "ta_edge_data.csv"));
        graphStats.saveStatistics(Paths.get(edgeTypeOutputDir, "ta_statistics.txt"));
        graphStats.saveCalibrationPlot(
                "PAG Sampling RFCI: Tail-Arrow", "ta_pag-sampling-rfci",
                1000, 1000,
                Paths.get(edgeTypeOutputDir, "ta_calibration.png"));
        graphStats.saveROCPlot("PAG Sampling RFCI: Tail-Arrow", "ta_pag-sampling-rfci",
                1000, 1000, Paths.get(edgeTypeOutputDir, "ta_roc.png"));

        // circle-arrow
        edgeTypeDir = FileIO.createSubdirectory(dirOut, "circle_arrow");
        edgeTypeOutputDir = edgeTypeDir.toString();
        graphData = GraphData.examineCircleArrowEdge(searchGraph, pagFromDagGraph);
        edgeData = GraphData.examineEdges(searchGraph, pagFromDagGraph);
        graphStats = new GraphStatistics(graphData, edgeData);
        graphStats.saveGraphData(Paths.get(edgeTypeOutputDir, "ca_edge_data.csv"));
        graphStats.saveStatistics(Paths.get(edgeTypeOutputDir, "ca_statistics.txt"));
        graphStats.saveCalibrationPlot(
                "PAG Sampling RFCI: Circle-Arrow", "pag-sampling-rfci",
                1000, 1000,
                Paths.get(edgeTypeOutputDir, "ca_calibration.png"));
        graphStats.saveROCPlot("PAG Sampling RFCI: Circle-Arrow", "ca_pag-sampling-rfci",
                1000, 1000, Paths.get(edgeTypeOutputDir, "ca_roc.png"));

        // circle-circle
        edgeTypeDir = FileIO.createSubdirectory(dirOut, "circle_circle");
        edgeTypeOutputDir = edgeTypeDir.toString();
        graphData = GraphData.examineCircleCircleEdge(searchGraph, pagFromDagGraph);
        edgeData = GraphData.examineEdges(searchGraph, pagFromDagGraph);
        graphStats = new GraphStatistics(graphData, edgeData);
        graphStats.saveGraphData(Paths.get(edgeTypeOutputDir, "cc_edge_data.csv"));
        graphStats.saveStatistics(Paths.get(edgeTypeOutputDir, "cc_statistics.txt"));
        graphStats.saveCalibrationPlot(
                "PAG Sampling RFCI: Circle-Circle", "pag-sampling-rfci",
                1000, 1000,
                Paths.get(edgeTypeOutputDir, "cc_calibration.png"));
        graphStats.saveROCPlot("PAG Sampling RFCI: Circle-Circle", "cc_pag-sampling-rfci",
                1000, 1000, Paths.get(edgeTypeOutputDir, "cc_roc.png"));

        // arrow-arrow
        edgeTypeDir = FileIO.createSubdirectory(dirOut, "arrow_arrow");
        edgeTypeOutputDir = edgeTypeDir.toString();
        graphData = GraphData.examineArrowArrowEdge(searchGraph, pagFromDagGraph);
        edgeData = GraphData.examineEdges(searchGraph, pagFromDagGraph);
        graphStats = new GraphStatistics(graphData, edgeData);
        graphStats.saveGraphData(Paths.get(edgeTypeOutputDir, "aa_edge_data.csv"));
        graphStats.saveStatistics(Paths.get(edgeTypeOutputDir, "aa_statistics.txt"));
        graphStats.saveCalibrationPlot(
                "PAG Sampling RFCI: Arrow-Arrow", "pag-sampling-rfci",
                1000, 1000,
                Paths.get(edgeTypeOutputDir, "aa_calibration.png"));
        graphStats.saveROCPlot("PAG Sampling RFCI: Arrow-Arrow", "aa_pag-sampling-rfci",
                1000, 1000, Paths.get(edgeTypeOutputDir, "aa_roc.png"));

        String outputDir = dirOut.toString();

        GeneralValueStatistics genValStats = new GeneralValueStatistics(generalValues);
        genValStats.saveData(Paths.get(outputDir, "independence_test_data.csv"));
        genValStats.saveStatistics(Paths.get(outputDir, "independence_test_stats.txt"));
        genValStats.saveCalibrationPlot(
                "PAG Sampling RFCI: Probabilistic Test", "probabilistic",
                1000, 1000,
                Paths.get(outputDir, "independence_test_calibration.png"));
        genValStats.saveROCPlot(
                "PAG Sampling RFCI: Probabilistic Test", "probabilistic",
                1000, 1000,
                Paths.get(outputDir, "independence_test_roc.png"));

        GraphDetails.saveDetails(pagFromDagGraph, searchGraph, Paths.get(outputDir, "graph_details.txt"));
//        GraphDetails.saveHistogramProbOfCorrectEdgeType(pagFromDagGraph, searchGraph, Paths.get(outputDir, "correct_edgetype_histogram.png"));
//
        Graphs.saveGraph(searchGraph, Paths.get(outputDir, "graph.txt"));
        Graphs.exportAsPngImage(searchGraph, 1000, 1000, Paths.get(outputDir, "graph.png"));
        // write out details
        try (PrintStream writer = new PrintStream(Paths.get(outputDir, "run_details.txt").toFile())) {
            writer.println("PAG Sampling RFCI");
            writer.println("================================================================================");
            writer.println("Algorithm: PAG Sampling RFCI");
            writer.println();

            writer.println("Parameters");
            writer.println("========================================");
            printParameters(parameters, writer);
            writer.println();

            writer.println("Dataset");
            writer.println("========================================");
            writer.printf("Variables: %d%n", dataSet.getNumColumns());
            writer.printf("Cases: %d%n", dataSet.getNumRows());
            writer.println();

            writer.println("Search Run Details");
            writer.println("========================================");
            writer.println("Run Time");
            writer.println("--------------------");
            writer.printf("Search start: %s%n", startDateTime.format(DATETIME_FORMATTER));
            writer.printf("Search end: %s%n", endDateTime.format(DATETIME_FORMATTER));
            writer.printf("Duration: %,d seconds%n", TimeUnit.NANOSECONDS.toSeconds(duration));
            writer.println();
            writer.println("Search Counts");
            writer.println("--------------------");
            writer.printf("Number of searches: %d%n", numOfSearchRuns);
            writer.println();
            writer.println("PAG Counts");
            writer.println("--------------------");
            writer.printf("Number of valid PAGs: %d%n", graphs.size());
            writer.printf("Number of invalid PAGs: %d%n", numOfSearchRuns - graphs.size());
            writer.println();

            writer.println("High-Edge-Probability Graph");
            writer.println("========================================");
            writer.println(searchGraph.toString().replaceAll(" - ", " ... ").trim());
        }
    }

    @Override
    protected Graph runSearch(DataModel dataModel, Parameters parameters) {
        return new EdgeListGraph();
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
