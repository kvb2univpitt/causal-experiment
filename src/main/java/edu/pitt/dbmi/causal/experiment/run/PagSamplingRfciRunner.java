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
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.search.Rfci;
import edu.cmu.tetrad.search.utils.GraphSearchUtils;
import edu.cmu.tetrad.util.GraphSampling;
import edu.cmu.tetrad.util.ParamDescriptions;
import edu.cmu.tetrad.util.Parameters;
import edu.cmu.tetrad.util.Params;
import edu.pitt.dbmi.causal.experiment.calibration.GraphStatistics;
import edu.pitt.dbmi.causal.experiment.data.SimulatedData;
import edu.pitt.dbmi.causal.experiment.tetrad.Graphs;
import edu.pitt.dbmi.causal.experiment.util.FileIO;
import edu.pitt.dbmi.causal.experiment.util.GraphDetails;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * Mar 22, 2023 9:35:45 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class PagSamplingRfciRunner extends AbstractRunner {

    public PagSamplingRfciRunner(SimulatedData simulatedData, Parameters parameters) {
        super(simulatedData, parameters);
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
        int numRandomizedSearchModels = parameters.getInt(Params.NUM_RANDOMIZED_SEARCH_MODELS);
        while (graphs.size() < numRandomizedSearchModels) {
            System.out.printf("Starting search: %d%n", numOfSearchRuns + 1);
            Graph graph = runSearch(dataSet, parameters);
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

        String outputDir = dirOut.toString();
        GraphStatistics graphCalibration = new GraphStatistics(searchGraph, pagFromDagGraph);
        graphCalibration.saveGraphData(Paths.get(outputDir, "directed_edge_data.csv"));
        graphCalibration.saveStatistics(Paths.get(outputDir, "statistics.txt"));
        graphCalibration.saveCalibrationPlot(
                "PAG Sampling RFCI", "pag-sampling-rfci",
                1000, 1000,
                Paths.get(outputDir, "calibration.png"));

        GraphDetails.saveDetails(pagFromDagGraph, searchGraph, Paths.get(outputDir, "graph_details.txt"));

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

    protected void printParameters(Parameters parameters, PrintStream writer) {
        ParamDescriptions paramDescs = ParamDescriptions.getInstance();

        writer.println("PAG Sampling RFCI");
        writer.println("--------------------");
        writer.printf("%s: %s%n",
                paramDescs.get(Params.NUM_RANDOMIZED_SEARCH_MODELS).getShortDescription(),
                getParameterValue(parameters, Params.NUM_RANDOMIZED_SEARCH_MODELS));
        writer.println();

        writer.println("RFCI");
        writer.println("--------------------");
        writer.printf("%s: %s%n",
                paramDescs.get(Params.MAX_PATH_LENGTH).getShortDescription(),
                getParameterValue(parameters, Params.MAX_PATH_LENGTH));
        writer.printf("%s: %s%n",
                paramDescs.get(Params.DEPTH).getShortDescription(),
                getParameterValue(parameters, Params.DEPTH));
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
        Rfci rfci = new Rfci((new ProbabilisticTest()).getTest(dataModel, parameters));
        rfci.setDepth(parameters.getInt(Params.DEPTH));
        rfci.setMaxPathLength(parameters.getInt(Params.MAX_PATH_LENGTH));
        rfci.setVerbose(parameters.getBoolean(Params.VERBOSE));

        return rfci.search();
    }

}
