/*
 * Copyright (C) 2023 University of Pittsburgh.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package edu.pitt.dbmi.causal.experiment.run;

import edu.cmu.tetrad.algcomparison.independence.ProbabilisticTest;
import edu.cmu.tetrad.data.DataModel;
import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.graph.Edge;
import edu.cmu.tetrad.graph.EdgeListGraph;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.graph.Node;
import edu.cmu.tetrad.search.Fci;
import edu.cmu.tetrad.search.test.IndTestDSep;
import edu.cmu.tetrad.search.utils.GraphSearchUtils;
import edu.cmu.tetrad.util.GraphSampling;
import edu.cmu.tetrad.util.ParamDescriptions;
import edu.cmu.tetrad.util.Parameters;
import edu.cmu.tetrad.util.Params;
import edu.pitt.dbmi.causal.experiment.calibration.EdgeValue;
import edu.pitt.dbmi.causal.experiment.calibration.GeneralValue;
import edu.pitt.dbmi.causal.experiment.calibration.GeneralValueStatistics;
import edu.pitt.dbmi.causal.experiment.calibration.GraphData;
import edu.pitt.dbmi.causal.experiment.calibration.GraphStatistics;
import edu.pitt.dbmi.causal.experiment.data.SimulatedData;
import edu.pitt.dbmi.causal.experiment.independence.wrapper.CalibratingIndTestProbabilisticTest;
import static edu.pitt.dbmi.causal.experiment.run.AbstractRunner.DATETIME_FORMATTER;
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
 * Nov 1, 2023 4:09:10 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class PagSamplingFciCalibratingIndependenceRunner extends AbstractRunner {

    public PagSamplingFciCalibratingIndependenceRunner(SimulatedData simulatedData, Parameters parameters) {
        super(simulatedData, parameters);
    }

    protected Graph runSearch(DataModel dataModel, Parameters parameters, Set<GeneralValue> generalValues, Set<String> condProbLabels, List<String> debugOutputs) {
        Graph trueGraph = createGraph(dataModel, simulatedData.getPagFromDagGraph());
        IndTestDSep indTestDSeperation = new IndTestDSep(trueGraph, true);

        Fci fci = new Fci((new CalibratingIndTestProbabilisticTest()).getTest(dataModel, parameters, indTestDSeperation, generalValues, condProbLabels, debugOutputs));
        fci.setDepth(parameters.getInt(Params.DEPTH));
        fci.setHeuristic(parameters.getInt(Params.FAS_HEURISTIC));
        fci.setStable(parameters.getBoolean(Params.STABLE_FAS));
        fci.setMaxPathLength(parameters.getInt(Params.MAX_PATH_LENGTH));
        fci.setPossibleDsepSearchDone(parameters.getBoolean(Params.POSSIBLE_DSEP_DONE));
        fci.setDoDiscriminatingPathRule(parameters.getBoolean(Params.DO_DISCRIMINATING_PATH_RULE));
        fci.setVerbose(parameters.getBoolean(Params.VERBOSE));

        return fci.search();
    }

    @Override
    public void run(Path parentOutDir) throws Exception {
        Graph pagFromDagGraph = simulatedData.getPagFromDagGraph();
        DataSet dataSet = simulatedData.getDataSet();
        Path dirOut = FileIO.createSubdirectory(parentOutDir, "pag_sampling_fci");

        final LocalDateTime startDateTime = LocalDateTime.now();
        final long startTime = System.nanoTime();

        int numOfSearchRuns = 0;
        List<Graph> graphs = new LinkedList<>();
        Set<GeneralValue> generalValues = new HashSet<>();
        Set<String> condProbLabels = new HashSet<>();
        List<String> debugOutputs = new LinkedList<>();
        int numRandomizedSearchModels = parameters.getInt(Params.NUM_RANDOMIZED_SEARCH_MODELS);
        while (graphs.size() < numRandomizedSearchModels) {
            System.out.printf("Starting search: %d%n", numOfSearchRuns + 1);
            Graph graph = runSearch(dataSet, parameters, generalValues, condProbLabels, debugOutputs);
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
                "PAG Sampling FCI: Tail-Arrow", "ta_pag-sampling-fci",
                1000, 1000,
                Paths.get(edgeTypeOutputDir, "ta_calibration.png"));
        graphStats.saveROCPlot("PAG Sampling FCI: Tail-Arrow", "ta_pag-sampling-fci",
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
                "PAG Sampling FCI: Circle-Arrow", "pag-sampling-fci",
                1000, 1000,
                Paths.get(edgeTypeOutputDir, "ca_calibration.png"));
        graphStats.saveROCPlot("PAG Sampling FCI: Circle-Arrow", "ca_pag-sampling-fci",
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
                "PAG Sampling FCI: Circle-Circle", "pag-sampling-fci",
                1000, 1000,
                Paths.get(edgeTypeOutputDir, "cc_calibration.png"));
        graphStats.saveROCPlot("PAG Sampling FCI: Circle-Circle", "cc_pag-sampling-fci",
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
                "PAG Sampling FCI: Arrow-Arrow", "pag-sampling-fci",
                1000, 1000,
                Paths.get(edgeTypeOutputDir, "aa_calibration.png"));
        graphStats.saveROCPlot("PAG Sampling FCI: Arrow-Arrow", "aa_pag-sampling-fci",
                1000, 1000, Paths.get(edgeTypeOutputDir, "aa_roc.png"));

        String outputDir = dirOut.toString();

        GeneralValueStatistics genValStats = new GeneralValueStatistics(generalValues);
        genValStats.saveData(Paths.get(outputDir, "independence_test_data.csv"));
        genValStats.saveStatistics(Paths.get(outputDir, "independence_test_stats.txt"));
        genValStats.saveCalibrationPlot(
                "PAG Sampling FCI: Probabilistic Test", "probabilistic",
                1000, 1000,
                Paths.get(outputDir, "independence_test_calibration.png"));
        genValStats.saveROCPlot(
                "PAG Sampling FCI: Probabilistic Test", "probabilistic",
                1000, 1000,
                Paths.get(outputDir, "independence_test_roc.png"));

        GraphDetails.saveDetails(pagFromDagGraph, searchGraph, Paths.get(outputDir, "graph_details.txt"));
        Graphs.saveGraph(searchGraph, Paths.get(outputDir, "graph.txt"));
        Graphs.exportAsPngImage(searchGraph, 1000, 1000, Paths.get(outputDir, "graph.png"));

        // write out details
        try (PrintStream writer = new PrintStream(Paths.get(outputDir, "run_details.txt").toFile())) {
            writer.println("PAG Sampling FCI");
            writer.println("================================================================================");
            writer.println("Algorithm: PAG Sampling FCI");
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

        if (!debugOutputs.isEmpty()) {
            try (PrintStream writer = new PrintStream(Paths.get(outputDir, "debug_output.csv").toFile())) {
                writer.println("test of independence,bc inference,c,b,m,n,r,return value,is independent (d-sep),is independent (coin flip)");
                debugOutputs.forEach(writer::println);
            }
        }
    }

    protected void printParameters(Parameters parameters, PrintStream writer) {
        ParamDescriptions paramDescs = ParamDescriptions.getInstance();

        writer.println("PAG Sampling FCI");
        writer.println("--------------------");
        writer.printf("%s: %s%n",
                paramDescs.get(Params.NUM_RANDOMIZED_SEARCH_MODELS).getShortDescription(),
                getParameterValue(parameters, Params.NUM_RANDOMIZED_SEARCH_MODELS));
        writer.println();

        writer.println("FCI");
        writer.println("--------------------");
        writer.printf("%s: %s%n",
                paramDescs.get(Params.DEPTH).getShortDescription(),
                getParameterValue(parameters, Params.DEPTH));
        writer.printf("%s: %s%n",
                paramDescs.get(Params.FAS_HEURISTIC).getShortDescription(),
                getParameterValue(parameters, Params.FAS_HEURISTIC));
        writer.printf("%s: %s%n",
                paramDescs.get(Params.STABLE_FAS).getShortDescription(),
                getParameterValue(parameters, Params.STABLE_FAS));
        writer.printf("%s: %s%n",
                paramDescs.get(Params.MAX_PATH_LENGTH).getShortDescription(),
                getParameterValue(parameters, Params.MAX_PATH_LENGTH));
        writer.printf("%s: %s%n",
                paramDescs.get(Params.POSSIBLE_DSEP_DONE).getShortDescription(),
                getParameterValue(parameters, Params.POSSIBLE_DSEP_DONE));
        writer.printf("%s: %s%n",
                paramDescs.get(Params.DO_DISCRIMINATING_PATH_RULE).getShortDescription(),
                getParameterValue(parameters, Params.DO_DISCRIMINATING_PATH_RULE));
        writer.printf("%s: %s%n",
                paramDescs.get(Params.COMPLETE_RULE_SET_USED).getShortDescription(),
                getParameterValue(parameters, Params.COMPLETE_RULE_SET_USED));
        writer.printf("%s: %s%n",
                paramDescs.get(Params.TIME_LAG).getShortDescription(),
                getParameterValue(parameters, Params.TIME_LAG));
        writer.printf("%s: %s%n",
                paramDescs.get(Params.VERBOSE).getShortDescription(),
                getParameterValue(parameters, Params.VERBOSE));
        writer.println();

        writer.println("Probabilistic Test");
        writer.println("--------------------");
//        writer.printf("%s: %s%n",
//                paramDescs.get(Params.CUTOFF_IND_TEST).getShortDescription(),
//                getParameterValue(parameters, Params.CUTOFF_IND_TEST));
        writer.printf("%s: %s%n",
                paramDescs.get(Params.PRIOR_EQUIVALENT_SAMPLE_SIZE).getShortDescription(),
                getParameterValue(parameters, Params.PRIOR_EQUIVALENT_SAMPLE_SIZE));
//        writer.printf("%s: %s%n",
//                paramDescs.get(Params.NO_RANDOMLY_DETERMINED_INDEPENDENCE).getShortDescription(),
//                getParameterValue(parameters, Params.NO_RANDOMLY_DETERMINED_INDEPENDENCE));
    }

    protected Graph runSearch(DataModel dataModel, Parameters parameters) {
        Fci fci = new Fci((new ProbabilisticTest()).getTest(dataModel, parameters));
        fci.setDepth(parameters.getInt(Params.DEPTH));
        fci.setHeuristic(parameters.getInt(Params.FAS_HEURISTIC));
        fci.setStable(parameters.getBoolean(Params.STABLE_FAS));
        fci.setMaxPathLength(parameters.getInt(Params.MAX_PATH_LENGTH));
        fci.setPossibleDsepSearchDone(parameters.getBoolean(Params.POSSIBLE_DSEP_DONE));
        fci.setDoDiscriminatingPathRule(parameters.getBoolean(Params.DO_DISCRIMINATING_PATH_RULE));
        fci.setVerbose(parameters.getBoolean(Params.VERBOSE));

        return fci.search();
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
