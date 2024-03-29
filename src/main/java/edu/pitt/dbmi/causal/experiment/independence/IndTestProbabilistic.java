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
import java.util.Set;

/**
 *
 * Apr 26, 2023 10:29:44 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class IndTestProbabilistic implements IndependenceTest {

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

    private static int nCount = 0;
    private static int count = 0;

    public IndTestProbabilistic(DataSet data, IndTestDSep indTestDSeperation, Set<GeneralValue> generalValues, Set<String> condProbLabels) {
        this(data);
        this.indTestDSeperation = indTestDSeperation;
        this.generalValues = generalValues;
        this.condProbLabels = condProbLabels;
    }

    //==========================CONSTRUCTORS=============================//
    /**
     * Initializes the test using a discrete data sets.
     */
    public IndTestProbabilistic(DataSet dataSet) {
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
        Node[] nodes = new Node[z.size()];
        for (int i = 0; i < z.size(); i++) {
            nodes[i] = z.get(i);
        }
        return checkIndependence(x, y, nodes);
    }

    @Override
    public IndependenceResult checkIndependence(Node x, Node y, Node... z) {
        IndependenceFact key = new IndependenceFact(x, y, z);

        List<Node> allVars = new ArrayList<>();
        allVars.add(x);
        allVars.add(y);
        Collections.addAll(allVars, z);

        List<Integer> rows = getRows(this.data, allVars, this.indices);
        if (rows.isEmpty()) {
            return new IndependenceResult(new IndependenceFact(x, y, z),
                    true, Double.NaN);
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

        double p = pInd;

        posterior = p;

        // reassign posterior probability
        if (p < 0.39) {
            p = 0;
        } else if (p >= 0.39 && p < 0.9) {
            p = 0.222222;
        } else if (p >= 0.9 && p < 0.92) {
            p = 0.500000;
        } else if (p >= 0.92 && p < 0.97) {
            p = 0.0;
        } else {
            p = 0.666667;
        }
//        if (p < 0.2) {
//            p = 0;
//        } else if (p >= 0.2 && p < 0.8) {
//            p = 0.115385;
//        } else if (p >= 0.8 && p < 0.97) {
//            p = 0.300000;
//        } else {
//            p = 0.643678;
//        }
        // if (p < 0.1) {
        //     p = 0;
        // } else if (p >= 0.1 && p < 0.9) {
        //    p = 0.050000;
        // } else if (p >= 0.9 && p < 0.97) {
        //    p = 0.153846;
        // } else {
        //    p = 0.699634;
        // }
        if (generalValues != null) {
            int observed = (int) indTestDSeperation.checkIndependence(x, y, z).getPValue();

            String condProbLabel = StringUtils.toString(x, y, z);
            generalValues.add(new GeneralValue(condProbLabel, p, observed));
//            if (!condProbLabels.contains(condProbLabel)) {
//                condProbLabels.add(condProbLabel);
//                generalValues.add(new GeneralValue(condProbLabel, p, observed));
//
////                if (p < 0.1) {
////                } else if (p >= 0.1 && p < 0.9) {
////                } else if (p >= 0.9 && p < 0.97) {
////                } else {
////                    nCount++;
////                    if ((int) indTestDSeperation.checkIndependence(x, y, z).getPValue() > 0) {
////                        count++;
////                    }
////                    System.out.printf("%d, %d => %f%n", count, nCount, ((float) count) / nCount);
////                }
//            }
        }

        boolean ind;
        if (threshold) {
            ind = (p >= cutoff);
        } else {
            ind = RandomUtil.getInstance().nextDouble() < p;
        }

        if (this.verbose) {
            if (ind) {
                TetradLogger.getInstance().forceLogMessage(
                        LogUtilsSearch.independenceFactMsg(x, y, Arrays.asList(z), p));
            }
        }

        return new IndependenceResult(new IndependenceFact(x, y, z), ind, p);
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
