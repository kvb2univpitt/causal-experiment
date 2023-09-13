package edu.pitt.dbmi.causal.experiment.calibration;

import java.util.Objects;

/**
 *
 * Apr 27, 2023 12:35:52 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class GeneralValue implements Comparable<GeneralValue> {

    private final String label;
    private final double predictedValue;
    private final int observedValue;

    public GeneralValue(String label, double predictedValue, int observedValue) {
        this.label = label;
        this.predictedValue = predictedValue;
        this.observedValue = observedValue;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.label);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final GeneralValue other = (GeneralValue) obj;
        return Objects.equals(this.label, other.label);
    }

    @Override
    public int compareTo(GeneralValue o) {
        if (this.predictedValue > o.predictedValue) {
            return 1;
        } else if (this.predictedValue < o.predictedValue) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return String.format("%s: %d, %f", label, observedValue, predictedValue);
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
