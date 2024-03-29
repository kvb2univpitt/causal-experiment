package edu.pitt.dbmi.causal.experiment.independence;

import edu.cmu.tetrad.data.DataModel;
import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.data.DiscreteVariable;
import edu.cmu.tetrad.graph.IndependenceFact;
import edu.cmu.tetrad.graph.Node;
import edu.cmu.tetrad.search.test.IndTestDSep;
import edu.cmu.tetrad.search.test.IndependenceResult;
import edu.cmu.tetrad.search.test.IndependenceTest;
import edu.cmu.tetrad.search.utils.LogUtilsSearch;
import edu.cmu.tetrad.util.RandomUtil;
import edu.cmu.tetrad.util.TetradLogger;
import edu.pitt.dbmi.algo.bayesian.constraint.inference.BCInference;
import edu.pitt.dbmi.causal.experiment.calibration.GeneralValue;
import edu.pitt.dbmi.causal.experiment.util.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 *
 * Oct 4, 2023 4:12:37 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class CalibratingIndTestProbabilistic implements IndependenceTest {

    /**
     * Calculates probabilities of independence for conditional independence
     * facts.
     */
    private boolean threshold;

    /**
     * The data set for which conditional independence judgments are requested.
     */
    private final DataSet data;

    /**
     * The nodes of the data set.
     */
    private final List<Node> nodes;

    /**
     * Indices of the nodes.
     */
    private final Map<Node, Integer> indices;

    /**
     * A map from independence facts to their probabilities of independence.
     */
    private final Map<IndependenceFact, Double> H;
    private double posterior;
    private boolean verbose;

    private double cutoff = 0.5;
    private double priorEquivalentSampleSize = 10;

    private final BCInference bci;

    private IndTestDSep indTestDSeperation;
    private Set<GeneralValue> generalValues;
    private Set<String> condProbLabels;
    private List<String> debugOutputs;
    private Map<String, Boolean> coinFlipCache = new HashMap<>();

    /**
     * Variables for Greg's algorithm.
     */
    private int m = 3;
    private int n = 42;
    private long seed = 1697166082542L;
    private Random rand = new Random(seed);

    public CalibratingIndTestProbabilistic(DataSet data, IndTestDSep indTestDSeperation, Set<GeneralValue> generalValues, Set<String> condProbLabels, List<String> debugOutputs) {
        this(data);
        this.indTestDSeperation = indTestDSeperation;
        this.generalValues = generalValues;
        this.condProbLabels = condProbLabels;
        this.debugOutputs = debugOutputs;
    }

    //==========================CONSTRUCTORS=============================//
    /**
     * Initializes the test using a discrete data sets.
     */
    public CalibratingIndTestProbabilistic(DataSet dataSet) {
        if (!dataSet.isDiscrete()) {
            throw new IllegalArgumentException("Not a discrete data set.");

        }

        this.nodes = dataSet.getVariables();

        this.indices = new HashMap<>();

        for (int i = 0; i < this.nodes.size(); i++) {
            this.indices.put(this.nodes.get(i), i);
        }

        this.data = dataSet;
        this.H = new HashMap<>();

        int[] _cols = new int[this.nodes.size()];
        for (int i = 0; i < _cols.length; i++) {
            _cols[i] = this.indices.get(this.nodes.get(i));
        }

        int[] _rows = new int[dataSet.getNumRows()];
        for (int i = 0; i < dataSet.getNumRows(); i++) {
            _rows[i] = i;
        }

        DataSet _data = this.data.subsetRowsColumns(_rows, _cols);

        List<Node> nodes = _data.getVariables();

        for (int i = 0; i < nodes.size(); i++) {
            this.indices.put(nodes.get(i), i);
        }

        this.bci = setup(_data);
    }

    private BCInference setup(DataSet dataSet) {
        int[] nodeDimensions = new int[dataSet.getNumColumns() + 2];

        for (int j = 0; j < dataSet.getNumColumns(); j++) {
            DiscreteVariable variable = (DiscreteVariable) (dataSet.getVariable(j));
            int numCategories = variable.getNumCategories();
            nodeDimensions[j + 1] = numCategories;
        }

        int[][] cases = new int[dataSet.getNumRows() + 1][dataSet.getNumColumns() + 2];

        for (int i = 0; i < dataSet.getNumRows(); i++) {
            for (int j = 0; j < dataSet.getNumColumns(); j++) {
                cases[i + 1][j + 1] = dataSet.getInt(i, j) + 1;
            }
        }

        BCInference bci = new BCInference(cases, nodeDimensions);
        bci.setPriorEqivalentSampleSize(this.priorEquivalentSampleSize);
        return bci;
    }

    @Override
    public IndependenceTest indTestSubset(List<Node> vars) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IndependenceResult checkIndependence(Node x, Node y, List<Node> z) {
        Node[] nodes = z.stream().toArray(Node[]::new);

        return checkIndependence(x, y, nodes);
    }

    @Override
    public IndependenceResult checkIndependence(Node x, Node y, Node... z) {
        StringBuilder logInfo = new StringBuilder();

        // print the variables of the independence test
        String condProbLabel = StringUtils.toString(x, y, z);
        String sortedCondProbLabel = StringUtils.toStringSorted(x, y, z);

        logInfo.append(String.format("\"%s\",", condProbLabel));

        // BC inference
        double bciIndependent = independentBCInference(x, y, z);

        // compute test-of-independence score
        IndependenceResult independenceResult = indTestDSeperation.checkIndependence(x, y, z); // independence from d-separation
        int observed = (int) independenceResult.getPValue(); // independence from BC inference
        double p = independent(independenceResult, bciIndependent, logInfo);
//        double p = independent1(independenceResult);

//        // coin flip without caching
//        boolean ind = RandomUtil.getInstance().nextDouble() < p;
//        logInfo.append(String.format(",%s", ind));
        // coin flip with caching
        boolean ind;
        if (coinFlipCache.containsKey(sortedCondProbLabel)) {
            ind = coinFlipCache.get(sortedCondProbLabel);
        } else {
            ind = RandomUtil.getInstance().nextDouble() < p;
            coinFlipCache.put(sortedCondProbLabel, ind);
        }
        logInfo.append(String.format(",%s", ind));

        if (generalValues != null) {
            if (!condProbLabels.contains(condProbLabel)) {
                condProbLabels.add(condProbLabel);
                generalValues.add(new GeneralValue(condProbLabel, p, observed));
            }
        }

        if (this.verbose) {
            if (ind) {
                TetradLogger.getInstance().forceLogMessage(
                        LogUtilsSearch.independenceFactMsg(x, y, Arrays.asList(z), p));
            }
        }

        debugOutputs.add(logInfo.toString());

        return new IndependenceResult(new IndependenceFact(x, y, z), ind, p);
    }

    private boolean getCoinFlipFromCache(String sortedCondProbLabel, double p) {
        boolean ind;

        if (coinFlipCache.containsKey(sortedCondProbLabel)) {
            ind = coinFlipCache.get(sortedCondProbLabel);
        } else {
            ind = RandomUtil.getInstance().nextDouble() < p;
            coinFlipCache.put(sortedCondProbLabel, ind);
        }

        return ind;
    }

    private double independentBCInference(Node x, Node y, Node... z) {
        IndependenceFact key = new IndependenceFact(x, y, z);

        List<Node> allVars = new ArrayList<>();
        allVars.add(x);
        allVars.add(y);
        Collections.addAll(allVars, z);

        List<Integer> rows = getRows(this.data, allVars, this.indices);
        if (rows.isEmpty()) {
//            return new IndependenceResult(new IndependenceFact(x, y, z), true, Double.NaN);
            // is independent
            return 0.99;
        }

        BCInference bci;
        Map<Node, Integer> indices;

        if (rows.size() == this.data.getNumRows()) {
            bci = this.bci;
            indices = this.indices;
        } else {

            int[] _cols = new int[allVars.size()];
            for (int i = 0; i < _cols.length; i++) {
                _cols[i] = this.indices.get(allVars.get(i));
            }

            int[] _rows = new int[rows.size()];
            for (int i = 0; i < rows.size(); i++) {
                _rows[i] = rows.get(i);
            }

            DataSet _data = this.data.subsetRowsColumns(_rows, _cols);

            List<Node> nodes = _data.getVariables();

            indices = new HashMap<>();

            for (int i = 0; i < nodes.size(); i++) {
                indices.put(nodes.get(i), i);
            }

            bci = setup(_data);
        }

        double pInd;
        if (!this.H.containsKey(key)) {
            pInd = probConstraint(bci, BCInference.OP.independent, x, y, z, indices);
            H.put(key, pInd);
        } else {
            pInd = H.get(key);
        }

        return pInd;
    }

    /**
     * This function is designed to return the probabilities 0.7 and 0.0, which
     * are both calibrated.
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    private double independent(IndependenceResult independenceResult, double bciIndependent, StringBuilder logInfo) {
        double returnValue = 0;

        double q = 0.7;
        double c = (double) m / n; //chosen to start as 3/42 = 0.07, which is an estimate of the fraction Independent
        double b = ((1 - q) / q) * (c / (1 - c));

        if (b >= 1.0) {
            System.err.println("Halt because Q needs to be changed.");
            System.exit(-1);
        }

        double r = rand.nextDouble();
        n++;
        if (independenceResult.isIndependent()) {
            m++;
            returnValue = q;
        } else {
            if (r > b) {
                returnValue = 0;
            } else {
                returnValue = q;
            }
        }
        // c,b,m,n,r,return value, is independent
        logInfo.append(String.format("%f,%f,%f,%d,%d,%f,%f,%s",
                bciIndependent,
                c, b, m, n, r, returnValue,
                independenceResult.isIndependent()));

        return returnValue;
    }

//    private double independent(Node x, Node y, Node... z) {
//        double q = 0.7;
//        double c = (double) m / n; //chosen to start as 3/42 = 0.07, which is an estimate of the fraction Independent
//        double b = ((1 - q) / q) * (c / (1 - c));
//
//        if (b >= 1.0) {
//            System.err.println("Halt because Q needs to be changed.");
//            System.exit(-1);
//        }
//
//        double r = rand.nextDouble();
//        n++;
//        if (isIndependent(x, y, z)) {
//            m++;
//            return q;
//        } else {
//            if (r > b) {
//                return 0;
//            } else {
//                return q;
//            }
//        }
//    }
    private double independent1(IndependenceResult independenceResult, StringBuilder logInfo) {
        boolean isIndependent = independenceResult.isIndependent();
        double calProbInd = 0.9;
        double p;
        if (isIndependent) {
            p = calProbInd;
        } else {
            p = 1 - calProbInd;
        }

        return p;
    }

    public double probConstraint(BCInference bci, BCInference.OP op, Node x, Node y, Node[] z, Map<Node, Integer> indices) {

        int _x = indices.get(x) + 1;
        int _y = indices.get(y) + 1;

        int[] _z = new int[z.length + 1];
        _z[0] = z.length;
        for (int i = 0; i < z.length; i++) {
            _z[i + 1] = indices.get(z[i]) + 1;
        }

        return bci.probConstraint(op, _x, _y, _z);
    }

    @Override
    public List<Node> getVariables() {
        return this.nodes;
    }

    @Override
    public Node getVariable(String name) {
        for (Node node : this.nodes) {
            if (name.equals(node.getName())) {
                return node;
            }
        }

        return null;
    }

    @Override
    public boolean determines(List<Node> z, Node y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getAlpha() {
        throw new UnsupportedOperationException("The Probabiistic Test doesn't use an alpha parameter");
    }

    @Override
    public void setAlpha(double alpha) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataModel getData() {
        return this.data;
    }

    @Override
    public double getScore() {
        return this.posterior;
    }

    public Map<IndependenceFact, Double> getH() {
        return new HashMap<>(this.H);
    }

    public double getPosterior() {
        return this.posterior;
    }

    @Override
    public boolean isVerbose() {
        return this.verbose;
    }

    @Override
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setThreshold(boolean noRandomizedGeneratingConstraints) {
        this.threshold = noRandomizedGeneratingConstraints;
    }

    public void setCutoff(double cutoff) {
        this.cutoff = cutoff;
    }

    public void setPriorEquivalentSampleSize(double priorEquivalentSampleSize) {
        this.priorEquivalentSampleSize = priorEquivalentSampleSize;
    }

    private List<Integer> getRows(DataSet dataSet, List<Node> allVars, Map<Node, Integer> nodesHash) {
        List<Integer> rows = new ArrayList<>();

        K:
        for (int k = 0; k < dataSet.getNumRows(); k++) {
            for (Node node : allVars) {
                if (dataSet.getInt(k, nodesHash.get(node)) == -99) {
                    continue K;
                }
            }

            rows.add(k);
        }

        return rows;
    }

}
