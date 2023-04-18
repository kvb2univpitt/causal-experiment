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
package edu.pitt.dbmi.causal.experiment;

import edu.cmu.tetrad.util.Parameters;
import edu.cmu.tetrad.util.Params;
import edu.pitt.dbmi.causal.experiment.data.SimulatedData;
import edu.pitt.dbmi.causal.experiment.run.PagSamplingRfciRunner;
import edu.pitt.dbmi.causal.experiment.run.RficGSquareBootstrapRunner;
import edu.pitt.dbmi.causal.experiment.run.RficProbabilisticBootstrapRunner;
import edu.pitt.dbmi.causal.experiment.tetrad.Graphs;
import edu.pitt.dbmi.causal.experiment.util.FileIO;
import edu.pitt.dbmi.causal.experiment.util.SimulatedDataFactory;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * Mar 21, 2023 11:01:56 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class SimulatedDataExperimentApp {

    public static long[] SEEDS = {
        11584893960885L,
        5584533057817L,
        3909440345377L,
        11946365959146L,
        7148459185827L,
        6172257769725L,
        12399190681453L,
        4889282875623L,
        12434943985933L,
        3870659553831L
    };

    private static void run(Path dirout) throws Exception {
        // clean
        Path experimentFolder = Paths.get(dirout.toString(), "experiments");
        FileIO.createNewDirectory(experimentFolder);
        for (int i = 0; i < SEEDS.length; i++) {
            Path iExperimentFolder = FileIO.createSubdirectory(experimentFolder, String.format("experiment_%d", i + 1));

            int numOfVariables = 20;
            int numOfCases = 1000;
            int avgDegree = 3;
            Path dataFolder = FileIO.createSubdirectory(iExperimentFolder, "data");
            SimulatedData simData = SimulatedDataFactory.createBayesNetSimulationData(numOfVariables, numOfCases, avgDegree, SEEDS[i], dataFolder);

            Path graphFolder = FileIO.createSubdirectory(iExperimentFolder, "graphs");
            Graphs.saveSourceGraphs(graphFolder, simData);

            Path runFolder = FileIO.createSubdirectory(iExperimentFolder, "runs");

            // run pag-sampling-rfci
            PagSamplingRfciRunner pagSamplingRfciRunner = new PagSamplingRfciRunner(simData, getPagSamplingRfciParameters());
            pagSamplingRfciRunner.run(runFolder);

            // run rfci with probabilistic test via bootstrapping
            RficProbabilisticBootstrapRunner rficProbabilisticBootstrapRunner = new RficProbabilisticBootstrapRunner(simData, getRficProbabilisticBootstrapParameters());
            rficProbabilisticBootstrapRunner.run(runFolder);

            // run rfci with g2 test via bootstrapping
            RficGSquareBootstrapRunner rficGSquareBootstrapRunner = new RficGSquareBootstrapRunner(simData, getRficGSquareBootstrapParameters());
            rficGSquareBootstrapRunner.run(runFolder);
        }
    }

    private static Parameters getRficGSquareBootstrapParameters() {
        Parameters parameters = new Parameters();

        // rfci
        int maxPathLength = -1;
        int depth = -1;
        boolean verbose = false;
        parameters.set(Params.MAX_PATH_LENGTH, maxPathLength);
        parameters.set(Params.DEPTH, depth);
        parameters.set(Params.VERBOSE, verbose);

        // g square test of independence
        double alpha = 0.05;
        parameters.set(Params.ALPHA, alpha);

        // bootstrapping
        long seed = 1673588774198L;
        int numberOfResampling = 99;
        boolean addOriginalDataset = true;
        boolean resamplingWithReplacement = true;
        parameters.set(Params.SEED, seed);
        parameters.set(Params.NUMBER_RESAMPLING, numberOfResampling);
        parameters.set(Params.ADD_ORIGINAL_DATASET, addOriginalDataset);
        parameters.set(Params.RESAMPLING_WITH_REPLACEMENT, resamplingWithReplacement);

        return parameters;
    }

    private static Parameters getRficProbabilisticBootstrapParameters() throws Exception {
        Parameters parameters = new Parameters();

        // rfci
        int maxPathLength = -1;
        int depth = -1;
        boolean verbose = false;
        parameters.set(Params.MAX_PATH_LENGTH, maxPathLength);
        parameters.set(Params.DEPTH, depth);
        parameters.set(Params.VERBOSE, verbose);

        // probabilistic test of independence
        double cutoffIndTest = 0.5;
        double priorEquivalentSampleSize = 10;
        boolean noRandomlyDeterminedIndependence = true;
        parameters.set(Params.CUTOFF_IND_TEST, cutoffIndTest);
        parameters.set(Params.PRIOR_EQUIVALENT_SAMPLE_SIZE, priorEquivalentSampleSize);
        parameters.set(Params.NO_RANDOMLY_DETERMINED_INDEPENDENCE, noRandomlyDeterminedIndependence);

        // bootstrapping
        long seed = 1673588774198L;
        int numberOfResampling = 99;
        boolean addOriginalDataset = true;
        boolean resamplingWithReplacement = true;
        parameters.set(Params.SEED, seed);
        parameters.set(Params.NUMBER_RESAMPLING, numberOfResampling);
        parameters.set(Params.ADD_ORIGINAL_DATASET, addOriginalDataset);
        parameters.set(Params.RESAMPLING_WITH_REPLACEMENT, resamplingWithReplacement);

        return parameters;
    }

    private static Parameters getPagSamplingRfciParameters() throws Exception {
        Parameters parameters = new Parameters();

        // pag sampling
        int numRandomizedSearchModels = 100;
        parameters.set(Params.NUM_RANDOMIZED_SEARCH_MODELS, numRandomizedSearchModels);

        // rfci
        int maxPathLength = -1;
        int depth = -1;
        boolean verbose = false;
        parameters.set(Params.MAX_PATH_LENGTH, maxPathLength);
        parameters.set(Params.DEPTH, depth);
        parameters.set(Params.VERBOSE, verbose);

        // probabilistic test of independence
        double cutoffIndTest = 0.5;
        double priorEquivalentSampleSize = 10;
        boolean noRandomlyDeterminedIndependence = false;
        parameters.set(Params.CUTOFF_IND_TEST, cutoffIndTest);
        parameters.set(Params.PRIOR_EQUIVALENT_SAMPLE_SIZE, priorEquivalentSampleSize);
        parameters.set(Params.NO_RANDOMLY_DETERMINED_INDEPENDENCE, noRandomlyDeterminedIndependence);

        return parameters;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("Simulated Data Experiments");
        System.out.println("================================================================================");
        try {
            run(Paths.get(args[0]));
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
        }
        System.out.println("================================================================================");
    }

}
