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
package edu.pitt.dbmi.lib.math.classification.calibration;

import edu.pitt.dbmi.lib.math.classification.data.ObservedPredictedValue;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;

/**
 *
 * Mar 6, 2023 2:20:24 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public abstract class AbstractHosmerLemeshow implements HosmerLemeshow {

    protected String summary;

    protected int numberOfPredictions;

    /**
     * Actual measured value. Value should be 0 or 1.
     */
    protected int[] observedValues;

    /**
     * Value predicted by classifier. Value should be between 0 and 1,
     * inclusive.
     */
    protected double[] predictedValues;

    /**
     * Contains the group number. The first interval is first group and so on...
     */
    protected int[] groups;

    /**
     * Contains the number of data in each group.
     */
    protected int[] numberOfDataPerGroup;

    /**
     * Contains the sum of actual positive outcomes within each group.
     */
    protected int[] positiveObservedSumPerGroup;

    /**
     * Contains the sum of all the predicted outcomes within each group.
     */
    protected double[] predictedSumPerGroup;

    /**
     * Contains the Hosmer-Lemeshow chi square statistic for each group.
     */
    protected double[] hlChi2PerGroup;

    /**
     * Contains the margin of errors for each group.
     */
    protected double[] marginOfErrorPerGroup;

    /**
     * Contains the average of observed positive outcome for each group
     * (interval);<br>
     * observed_avg = (# of positive observed outcome) / (# of data in the
     * interval)<br>
     * This is the values for the y-axis in the HL plot.
     */
    protected double[] hlObservedValues;

    /**
     * Contains the average of predicted outcomes for each group (interval)<br>
     * predicted_avg = (sum of the predicted outcomes) / (# of data in the
     * interval)<br>
     * This is the values for the x-axis in the HL plot.
     */
    protected double[] hlExpectedValues;

    protected int degreesOfFreedom;

    protected double pValue;

    /**
     * The measurement of the observed accuracy in each bin.
     */
    protected double expectedCalibrationError;

    /**
     * The highest gap over all bins.
     */
    protected double maxCalibrationError;

    /**
     * The average miscalibration where each bin gets weighted equally.
     */
    protected double averageCalibrationError;

    public AbstractHosmerLemeshow(List<ObservedPredictedValue> observedPredictedValues) {
        if (observedPredictedValues == null || observedPredictedValues.isEmpty()) {
            throw new IllegalArgumentException(
                    "A list of data containing both observed value and predicted value is required.");
        }

        // copy data to an array
        ObservedPredictedValue[] data = observedPredictedValues.toArray(ObservedPredictedValue[]::new);

        // sort in ascending order
        Arrays.sort(data);

        numberOfPredictions = data.length;

        // populate observed values and predicted values
        observedValues = new int[numberOfPredictions];
        predictedValues = new double[numberOfPredictions];
        for (int i = 0; i < numberOfPredictions; i++) {
            ObservedPredictedValue obsPredVal = data[i];
            observedValues[i] = obsPredVal.getObservedValue();
            predictedValues[i] = obsPredVal.getPredictedValue();
        }

        int totalNumOfGroups = computeTotalNumberOfGroups();
        groups = new int[totalNumOfGroups];
        numberOfDataPerGroup = new int[totalNumOfGroups];
        positiveObservedSumPerGroup = new int[totalNumOfGroups];
        predictedSumPerGroup = new double[totalNumOfGroups];
        hlChi2PerGroup = new double[totalNumOfGroups];
        marginOfErrorPerGroup = new double[totalNumOfGroups];
        hlObservedValues = new double[totalNumOfGroups];
        hlExpectedValues = new double[totalNumOfGroups];

        computePlotPoints();

        degreesOfFreedom = groups.length - 2;

        pValue = computePValue(degreesOfFreedom, hlChi2PerGroup);

        // calibration metrics
        expectedCalibrationError = computeExpectedCalibrationError(hlExpectedValues, hlObservedValues, numberOfDataPerGroup, numberOfPredictions);
        maxCalibrationError = computeMaxCalibrationError(hlExpectedValues, hlObservedValues, numberOfDataPerGroup);
        averageCalibrationError = computeAverageCalibrationError(hlExpectedValues, hlObservedValues, numberOfDataPerGroup);
    }

    protected abstract int computeTotalNumberOfGroups();

    protected abstract void computePlotPoints();

    protected double computePValue(int degreesOfFreedom, double[] hlChi2PerGroup) {
        double pvalue = -1.0;

        if (degreesOfFreedom > 0) {
            ChiSquaredDistribution distribution = new ChiSquaredDistribution(degreesOfFreedom);

            double hlTotal = 0;
            for (int i = 0; i < hlChi2PerGroup.length; i++) {
                hlTotal += hlChi2PerGroup[i];
            }

            if (!Double.isInfinite(hlTotal)) {
                pvalue = 1.0 - distribution.cumulativeProbability(hlTotal);
            }
        }

        return pvalue;
    }

    /**
     * The Expected Calibration Error (ECE) measures the observed accuracy in
     * each group (bin).
     *
     * @param hlExpectedValues
     * @param hlObservedValues
     * @param numberOfDataPerGroup
     * @param numberOfPredictions
     * @return
     */
    protected double computeExpectedCalibrationError(double[] hlExpectedValues, double[] hlObservedValues, int[] numberOfDataPerGroup, int numberOfPredictions) {
        double ece = 0;

        for (int i = 0; i < numberOfDataPerGroup.length; i++) {
            double yValue = hlObservedValues[i];  // the true fraction of positive instances in bin i (accuracy of the data items in the bin)
            double xValue = hlExpectedValues[i]; // the mean of the post-calibrated probabilities for the instances in bin i (average confidence)
            double iProb = ((double) numberOfDataPerGroup[i]) / numberOfPredictions;  // the empirical probability (fraction) of all instances that fall into bin i

            ece += iProb * Math.abs(yValue - xValue);
        }

        return ece;
    }

    /**
     * The Maximum Calibration Error (MCE) denotes the highest gap over all
     * groups (bins).
     *
     * @param hlExpectedValues
     * @param hlObservedValues
     * @param numberOfDataPerGroup
     * @return
     */
    protected double computeMaxCalibrationError(double[] hlExpectedValues, double[] hlObservedValues, int[] numberOfDataPerGroup) {
        double mce = 0;

        for (int i = 0; i < numberOfDataPerGroup.length; i++) {
            double yValue = hlObservedValues[i];  // the true fraction of positive instances in bin i (accuracy of the data items in the bin)
            double xValue = hlExpectedValues[i]; // the mean of the post-calibrated probabilities for the instances in bin i (average confidence)

            double diffSoreAccuracy = Math.abs(yValue - xValue);
            if (mce < diffSoreAccuracy) {
                mce = diffSoreAccuracy;
            }

        }

        return mce;
    }

    /**
     * Average Calibration Error (ACE) denotes the average miscalibration where
     * each bin gets weighted equally.
     *
     * @param hlExpectedValues
     * @param hlObservedValues
     * @param numberOfDataPerGroup
     * @return
     */
    protected double computeAverageCalibrationError(double[] hlExpectedValues, double[] hlObservedValues, int[] numberOfDataPerGroup) {
        double ace = 0;

        int numOfNonemptyBins = numberOfDataPerGroup.length;
        for (int i = 0; i < numberOfDataPerGroup.length; i++) {
            double yValue = hlObservedValues[i];  // the true fraction of positive instances in bin i (accuracy of the data items in the bin)
            double xValue = hlExpectedValues[i]; // the mean of the post-calibrated probabilities for the instances in bin i (average confidence)

            ace += Math.abs(yValue - xValue) / numOfNonemptyBins;
        }

        return ace;
    }

    @Override
    public String getSummary() {
        if (summary == null) {
            StringBuilder dataBuilder = new StringBuilder("========================================================================\n");
            dataBuilder.append(String.format("%-6s %-8s %-13s %-15s %-8s 95%s CI\n", "Group", "N", "Obs (%)", "Exp (%)", "HL", "%"));
            dataBuilder.append("========================================================================\n");
            int dataTotal = 0;
            int observedValueTotal = 0;
            double predictedValueTotal = 0;
            double hlTotal = 0;
            int numOfGroup = groups.length;
            for (int j = 0; j < numOfGroup; j++) {
                dataTotal += numberOfDataPerGroup[j];
                observedValueTotal += positiveObservedSumPerGroup[j];
                predictedValueTotal += predictedSumPerGroup[j];
                hlTotal += hlChi2PerGroup[j];
                dataBuilder.append(String.format("%-6d %-8d %-13s %-15s %-8.2f %s\n",
                        groups[j], numberOfDataPerGroup[j],
                        String.format("%d (%2.1f)", positiveObservedSumPerGroup[j], hlObservedValues[j] * 100),
                        String.format("%3.1f (%2.1f)", predictedSumPerGroup[j], hlExpectedValues[j] * 100),
                        hlChi2PerGroup[j],
                        String.format("(%1.4f, %1.4f)",
                                (marginOfErrorPerGroup[j] == 0) ? marginOfErrorPerGroup[j] - hlExpectedValues[j] : hlExpectedValues[j] - marginOfErrorPerGroup[j],
                                hlExpectedValues[j] + marginOfErrorPerGroup[j])));
            }

            dataBuilder.append("------------------------------------------------------------------------\n");
            double perObsTotal = ((double) observedValueTotal / dataTotal) * 100;
            double perExpTotal = ((double) predictedValueTotal / dataTotal) * 100;
            dataBuilder.append(String.format("%-6s %-8d %-13s %-15s %.2f\n",
                    "Total",
                    dataTotal,
                    String.format("%d (%-2.1f)", observedValueTotal, perObsTotal),
                    String.format("%-3.1f (%-2.1f)", predictedValueTotal, perExpTotal),
                    hlTotal));
            dataBuilder.append("\n");
            dataBuilder.append(String.format("Total Number of Groups: %d\n", numOfGroup));
            dataBuilder.append(String.format("Hosmer-Lemeshow Chi2(%d): %1.2f\n", numOfGroup, hlTotal));
            dataBuilder.append(String.format("Degree of Freedom: %d\n", degreesOfFreedom));
            dataBuilder.append(String.format("P-Value: %f\n", pValue));
            dataBuilder.append("\n");
            dataBuilder.append("Calibration Metrics\n");
            dataBuilder.append("------------------------------------\n");
            dataBuilder.append(String.format("Expected Calibration Error (ECE): %f\n", expectedCalibrationError));
            dataBuilder.append(String.format("Maximum Calibration Error (MCE): %f\n", maxCalibrationError));
            dataBuilder.append(String.format("Average Calibration Error (ACE) : %f\n", averageCalibrationError));
            dataBuilder.append("========================================================================");

            summary = dataBuilder.toString();
        }

        return summary;
    }

    @Override
    public String toString() {
        return getSummary();
    }

    @Override
    public int getNumberOfPredictions() {
        return numberOfPredictions;
    }

    @Override
    public int[] getObservedValues() {
        return observedValues;
    }

    @Override
    public double[] getPredictedValues() {
        return predictedValues;
    }

    @Override
    public int[] getGroups() {
        return groups;
    }

    @Override
    public int[] getNumberOfDataPerGroup() {
        return numberOfDataPerGroup;
    }

    @Override
    public int[] getPositiveObservedSumPerGroup() {
        return positiveObservedSumPerGroup;
    }

    @Override
    public double[] getPredictedSumPerGroup() {
        return predictedSumPerGroup;
    }

    @Override
    public double[] getHlChi2PerGroup() {
        return hlChi2PerGroup;
    }

    @Override
    public double[] getMarginOfErrorPerGroup() {
        return marginOfErrorPerGroup;
    }

    @Override
    public double[] getHlObservedValues() {
        return hlObservedValues;
    }

    @Override
    public double[] getHlExpectedValues() {
        return hlExpectedValues;
    }

    @Override
    public int getDegreesOfFreedom() {
        return degreesOfFreedom;
    }

    @Override
    public double getPValue() {
        return pValue;
    }

    @Override
    public double getExpectedCalibrationError() {
        return expectedCalibrationError;
    }

    @Override
    public double getMaxCalibrationError() {
        return maxCalibrationError;
    }

    @Override
    public double getAverageCalibrationError() {
        return averageCalibrationError;
    }

}
