package edu.pitt.dbmi.causal.experiment;

import edu.cmu.tetrad.util.Parameters;
import edu.cmu.tetrad.util.Params;
import edu.pitt.dbmi.causal.experiment.data.SimulatedData;
import edu.pitt.dbmi.causal.experiment.run.PagSamplingRfciRunner;
import edu.pitt.dbmi.causal.experiment.run.RficChiSquareBootstrapRunner;
import edu.pitt.dbmi.causal.experiment.run.RficProbabilisticBootstrapRunner;
import edu.pitt.dbmi.causal.experiment.tetrad.Graphs;
import edu.pitt.dbmi.causal.experiment.util.FileIO;
import edu.pitt.dbmi.causal.experiment.util.SimulatedDataFactory;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * Mar 31, 2023 12:08:23 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class SingleSimulatedDataExperimentApp {

    public static long[] SEEDS = {
        1681411802094L
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

            // run rfci with chi2 test via bootstrapping
            RficChiSquareBootstrapRunner rficGSquareBootstrapRunner = new RficChiSquareBootstrapRunner(simData, getRficChiSquareBootstrapParameters());
            rficGSquareBootstrapRunner.run(runFolder);
        }
    }

    private static Parameters getRficChiSquareBootstrapParameters() {
        Parameters parameters = new Parameters();

        // rfci
        int maxPathLength = -1;
        int depth = 3;
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
        System.out.println("Single Simulated Data Experiments");
        System.out.println("================================================================================");
        try {
            run(Paths.get(args[0]));
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
        }
        System.out.println("================================================================================");
    }

}
