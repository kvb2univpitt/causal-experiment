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
package edu.pitt.dbmi.lib.math.classification.roc;

import edu.pitt.dbmi.lib.math.classification.data.ObservedPredictedValue;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * ROC Curve using empirical method.
 *
 * Mar 27, 2012 8:49:05 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class ROCCurve implements ROC {

    /**
     * Sensitivity, the y-values on the ROC plot.
     */
    private double[] truePositiveRates;

    /**
     * 1 - Specificity, the x-values on the ROC plot.
     */
    private double[] falsePositiveRates;

    private double[] positivePredictedValues;

    private double[] negativePredictedValues;

    private ConfusionMatrix[] confusionMatrices;

    private int numberOfPositives;

    private int numberOfNegatives;

    private double areaUnderRocCurve;

    public ROCCurve(List<ObservedPredictedValue> observedPredictedValues) {
        if (observedPredictedValues == null || observedPredictedValues.isEmpty()) {
            throw new IllegalArgumentException(
                    "A list of data containing both observed value and predicted value is required.");
        }

        // copy data to an array
        ObservedPredictedValue[] data = observedPredictedValues.toArray(ObservedPredictedValue[]::new);

        // sort in descending order
        Arrays.sort(data, Collections.reverseOrder());

        this.numberOfPositives = (int) Arrays.stream(data)
                .filter(obsPredVal -> obsPredVal.getObservedValue() == 1)
                .count();
        this.numberOfNegatives = (int) Arrays.stream(data)
                .filter(obsPredVal -> obsPredVal.getObservedValue() == 0)
                .count();

        // seperate the values for the positive and negative outcomes
        positivePredictedValues = Arrays.stream(data)
                .filter(obsPredVal -> obsPredVal.getObservedValue() == 1)
                .mapToDouble(obsPredVal -> obsPredVal.getPredictedValue())
                .toArray();
        negativePredictedValues = Arrays.stream(data)
                .filter(obsPredVal -> obsPredVal.getObservedValue() == 0)
                .mapToDouble(obsPredVal -> obsPredVal.getPredictedValue())
                .toArray();

        this.confusionMatrices = computeConfusionMatrices(data, numberOfPositives, numberOfNegatives);

        this.areaUnderRocCurve = computeAreaUnderRocCurve(confusionMatrices);

        this.truePositiveRates = Arrays.stream(confusionMatrices)
                .mapToDouble(confusionMaxtrice -> confusionMaxtrice.getTruePositiveRate())
                .toArray();
        this.falsePositiveRates = Arrays.stream(confusionMatrices)
                .mapToDouble(confusionMaxtrice -> confusionMaxtrice.getFalsePositiveRate())
                .toArray();
    }

    private double computeAreaUnderRocCurve(ConfusionMatrix[] confusionMatrices) {
        double areaUnderCurve = 0;

        double x1, x2, y1, y2;
        x1 = y1 = 0;
        for (ConfusionMatrix confusionMatrix : confusionMatrices) {
            x2 = confusionMatrix.getFalsePositiveRate();
            y2 = confusionMatrix.getTruePositiveRate();

            // compute the area using trapezoid method
            double base = Math.abs(x1 - x2);
            double height = (y1 + y2) / 2;
            areaUnderCurve += base * height;

            x1 = x2;
            y1 = y2;
        }

        return areaUnderCurve;
    }

    private ConfusionMatrix[] computeConfusionMatrices(ObservedPredictedValue[] observedPredictedValues, int numberOfPositives, int numberOfNegatives) {
        List<ConfusionMatrix> confusionMatrixList = new LinkedList<>();

        int falsePositive = 0;  // incorrectly labeled as belonging to the positive class (also known as false alarm, Type I error)
        int truePositive = 0;  // the number of items correctly labeled as belonging to the positive class
        int trueNegative = numberOfNegatives;
        int falseNegative = numberOfPositives;  // items which were not labeled as belonging to the positive class but should have been (also known as miss, Type II error)
        double threshold = -1.0;
        for (ObservedPredictedValue observedPredictedValue : observedPredictedValues) {
            if (observedPredictedValue.getPredictedValue() != threshold) {
                confusionMatrixList.add(
                        new ConfusionMatrix(truePositive, trueNegative, falsePositive, falseNegative, threshold));
                threshold = observedPredictedValue.getPredictedValue();
            }

            if (observedPredictedValue.getObservedValue() == 1) {
                truePositive++;
                falseNegative--;
            } else {
                falsePositive++;
                trueNegative--;
            }
        }
        confusionMatrixList.add(
                new ConfusionMatrix(truePositive, trueNegative, falsePositive, falseNegative, threshold));

        return confusionMatrixList.stream()
                .toArray(ConfusionMatrix[]::new);
    }

    @Override
    public double[] getTruePositiveRates() {
        return truePositiveRates;
    }

    @Override
    public double[] getFalsePositiveRates() {
        return falsePositiveRates;
    }

    @Override
    public double[] getPositivePredictedValues() {
        return positivePredictedValues;
    }

    @Override
    public double[] getNegativePredictedValues() {
        return negativePredictedValues;
    }

    @Override
    public ConfusionMatrix[] getConfusionMatrices() {
        return confusionMatrices;
    }

    @Override
    public int getNumberOfPositives() {
        return numberOfPositives;
    }

    @Override
    public int getNumberOfNegatives() {
        return numberOfNegatives;
    }

    @Override
    public double getAreaUnderRocCurve() {
        return areaUnderRocCurve;
    }

}
