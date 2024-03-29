package edu.pitt.dbmi.causal.experiment;

import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.util.Parameters;
import edu.cmu.tetrad.util.Params;
import edu.pitt.dbmi.causal.experiment.data.SimulatedData;
import edu.pitt.dbmi.causal.experiment.run.PagSamplingFciCalibratingIndependenceRunner;
import edu.pitt.dbmi.causal.experiment.tetrad.Graphs;
import edu.pitt.dbmi.causal.experiment.util.FileIO;
import edu.pitt.dbmi.causal.experiment.util.ResourceLoader;
import edu.pitt.dbmi.causal.experiment.util.SimulatedDataFactory;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * Apr 26, 2023 11:08:30 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class CalibrationIndTestProbabilistic {

    public static long[] SEEDS = {
        12449569371213L
    };

    private static void run(Path dirout) throws Exception {
        Path experimentFolder = Paths.get(dirout.toString(), "experiments");
        FileIO.createNewDirectory(experimentFolder);
        for (int i = 0; i < SEEDS.length; i++) {
            Path iExperimentFolder = FileIO.createSubdirectory(experimentFolder, String.format("experiment_%d", i + 1));

            // simulate data from graph file
            Path graphFile = Paths.get(getGraphFile());
            Graph graph = ResourceLoader.loadGraph(graphFile);
            int numOfCases = 10000;
            int avgDegree = 3;
            Path dataFolder = FileIO.createSubdirectory(experimentFolder, "data");
            SimulatedData simData = SimulatedDataFactory.createBayesNetSimulationData(graph, numOfCases, avgDegree, SEEDS[i], dataFolder);
//            // simuldate data
//            int numOfVariables = 20;
//            int numOfCases = 10000;
//            int avgDegree = 3;
//            Path dataFolder = FileIO.createSubdirectory(iExperimentFolder, "data");
//            SimulatedData simData = SimulatedDataFactory.createBayesNetSimulationData(numOfVariables, numOfCases, avgDegree, SEEDS[i], dataFolder);

            Path graphFolder = FileIO.createSubdirectory(iExperimentFolder, "graphs");
            Graphs.saveSourceGraphs(graphFolder, simData);

            Path runFolder = FileIO.createSubdirectory(iExperimentFolder, "runs");

            // run pag-sampling-rfci
//            PagSamplingRfciRunner runner = new PagSamplingRfciIndependenceRunner(simData, getParameters());
//            PagSamplingRfciRunner runner = new PagSamplingRfciCalibratingIndependenceRunner(simData, getRfciPagSamplingParameters());
            PagSamplingFciCalibratingIndependenceRunner runner = new PagSamplingFciCalibratingIndependenceRunner(simData, getFciParameters());
            runner.run(runFolder);
        }
    }

    private static Parameters getFciParameters() throws Exception {
        Parameters parameters = new Parameters();

        // pag sampling
        int numRandomizedSearchModels = 100;
        parameters.set(Params.NUM_RANDOMIZED_SEARCH_MODELS, numRandomizedSearchModels);

        // fci
        int depth = -1;
        int fasHeuristic = 1;
        boolean stableFAS = true;
        int maxPathLength = -1;
        boolean possibleDsepDone = true;
        boolean doDiscriminatingPathRule = true;
        boolean completeRuleSetUsed = true;
        int timeLag = 0;
        boolean verbose = false;
        parameters.set(Params.DEPTH, depth);
        parameters.set(Params.FAS_HEURISTIC, fasHeuristic);
        parameters.set(Params.STABLE_FAS, stableFAS);
        parameters.set(Params.MAX_PATH_LENGTH, maxPathLength);
        parameters.set(Params.POSSIBLE_DSEP_DONE, possibleDsepDone);
        parameters.set(Params.DO_DISCRIMINATING_PATH_RULE, doDiscriminatingPathRule);
        parameters.set(Params.COMPLETE_RULE_SET_USED, completeRuleSetUsed);
        parameters.set(Params.TIME_LAG, timeLag);
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

    private static Parameters getRfciPagSamplingParameters() throws Exception {
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
        System.out.println("Calibration Independence Test Experiments");
        System.out.println("================================================================================");
        try {
            run(Paths.get(args[0]));
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
        }
        System.out.println("================================================================================");
    }

    private static String getGraphFile() {
        return CalibrationIndTestProbabilistic.class
                .getResource("/graph/graph_vwxy_y-structure.txt").getFile();
    }

}
