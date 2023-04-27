package edu.pitt.dbmi.causal.experiment.calibration;

/**
 *
 * Apr 27, 2023 12:35:52 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class GeneralValue {

    private final String label;
    private final double predictedValue;
    private final int observedValue;

    public GeneralValue(String label, double predictedValue, int observedValue) {
        this.label = label;
        this.predictedValue = predictedValue;
        this.observedValue = observedValue;
    }

    public String getLabel() {
        return label;
    }

    public double getPredictedValue() {
        return predictedValue;
    }

    public int getObservedValue() {
        return observedValue;
    }

}
