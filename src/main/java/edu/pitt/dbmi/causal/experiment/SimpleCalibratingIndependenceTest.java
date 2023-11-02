package edu.pitt.dbmi.causal.experiment;

import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.util.Parameters;
import edu.cmu.tetrad.util.Params;
import edu.pitt.dbmi.causal.experiment.data.SimulatedData;
import edu.pitt.dbmi.causal.experiment.run.PagSamplingRfciCalibratingIndependenceRunner;
import edu.pitt.dbmi.causal.experiment.run.PagSamplingRfciRunner;
import edu.pitt.dbmi.causal.experiment.tetrad.Graphs;
import edu.pitt.dbmi.causal.experiment.util.FileIO;
import edu.pitt.dbmi.causal.experiment.util.ResourceLoader;
import edu.pitt.dbmi.causal.experiment.util.SimulatedDataFactory;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * Oct 4, 2023 1:37:33 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class SimpleCalibratingIndependenceTest {

    private static long SEED = 1696442314180L;

    private static void run(Path graphFile, Path dirout) throws Exception {
        Path experimentFolder = Paths.get(dirout.toString(), "experiments");
        FileIO.createNewDirectory(experimentFolder);

        Graph graph = ResourceLoader.loadGraph(graphFile);
        int numOfCases = 10000;
        int avgDegree = 3;
        Path dataFolder = FileIO.createSubdirectory(experimentFolder, "data");
        SimulatedData simData = SimulatedDataFactory.createBayesNetSimulationData(graph, numOfCases, avgDegree, SEED, dataFolder);

        Path graphFolder = FileIO.createSubdirectory(experimentFolder, "graphs");
        Graphs.saveSourceGraphs(graphFolder, simData);

        Path runFolder = FileIO.createSubdirectory(experimentFolder, "run");

        // run pag-sampling-rfci
        PagSamplingRfciRunner pagSamplingRfciRunner = new PagSamplingRfciCalibratingIndependenceRunner(simData, getParameters());
        pagSamplingRfciRunner.run(runFolder);
    }

    private static Parameters getParameters() throws Exception {
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
        double priorEquivalentSampleSize = 10;
        parameters.set(Params.PRIOR_EQUIVALENT_SAMPLE_SIZE, priorEquivalentSampleSize);

        return parameters;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("Simple Calibrating Independence Test");
        System.out.println("================================================================================");
        try {
            run(Paths.get(args[0]), Paths.get(args[1]));
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
        }
        System.out.println("================================================================================");
    }

}
