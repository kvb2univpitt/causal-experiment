package edu.pitt.dbmi.causal.experiment.independence.wrapper;

import edu.cmu.tetrad.algcomparison.independence.IndependenceWrapper;
import edu.cmu.tetrad.data.DataModel;
import edu.cmu.tetrad.data.DataType;
import edu.cmu.tetrad.data.SimpleDataLoader;
import edu.cmu.tetrad.search.test.IndTestDSep;
import edu.cmu.tetrad.search.test.IndependenceTest;
import edu.cmu.tetrad.util.Parameters;
import edu.cmu.tetrad.util.Params;
import edu.pitt.dbmi.causal.experiment.calibration.GeneralValue;
import edu.pitt.dbmi.causal.experiment.independence.CalibratingIndTestProbabilistic;
import edu.pitt.dbmi.causal.experiment.independence.IndTestProbabilistic;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * Oct 4, 2023 4:10:35 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class CalibratingIndTestProbabilisticTest implements IndependenceWrapper {

    private static final long serialVersionUID = 23L;

    public IndependenceTest getTest(DataModel dataSet, Parameters parameters, IndTestDSep indTestDSeperation, Set<GeneralValue> generalValues, Set<String> condProbLabels, List<String> debugOutputs) {
        CalibratingIndTestProbabilistic test = new CalibratingIndTestProbabilistic(SimpleDataLoader.getDiscreteDataSet(dataSet), indTestDSeperation, generalValues, condProbLabels, debugOutputs);
        test.setThreshold(parameters.getBoolean(Params.NO_RANDOMLY_DETERMINED_INDEPENDENCE));
        test.setCutoff(parameters.getDouble(Params.CUTOFF_IND_TEST));
        test.setPriorEquivalentSampleSize(parameters.getDouble(Params.PRIOR_EQUIVALENT_SAMPLE_SIZE));

        return test;
    }

    @Override
    public IndependenceTest getTest(DataModel dataSet, Parameters parameters) {
        IndTestProbabilistic test = new IndTestProbabilistic(SimpleDataLoader.getDiscreteDataSet(dataSet));
        test.setThreshold(parameters.getBoolean(Params.NO_RANDOMLY_DETERMINED_INDEPENDENCE));
        test.setCutoff(parameters.getDouble(Params.CUTOFF_IND_TEST));
        test.setPriorEquivalentSampleSize(parameters.getDouble(Params.PRIOR_EQUIVALENT_SAMPLE_SIZE));

        return test;
    }

    @Override
    public String getDescription() {
        return "Probabilistic Conditional Independence Test";
    }

    @Override
    public DataType getDataType() {
        return DataType.Discrete;
    }

    @Override
    public List<String> getParameters() {
        List<String> parameters = new ArrayList<>();
        parameters.add(Params.NO_RANDOMLY_DETERMINED_INDEPENDENCE);
        parameters.add(Params.CUTOFF_IND_TEST);
        parameters.add(Params.PRIOR_EQUIVALENT_SAMPLE_SIZE);
        return parameters;
    }

}
