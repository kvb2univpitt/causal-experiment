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
import java.util.List;

/**
 * Hosmer-Lemeshow calibration chart binning by risk-factor.
 *
 * Mar 30, 2012 1:35:07 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class HosmerLemeshowRiskGroup extends AbstractHosmerLemeshow {

    public HosmerLemeshowRiskGroup(List<ObservedPredictedValue> observedPredictedValues) {
        super(observedPredictedValues);
    }

    @Override
    protected void computePlotPoints() {
        int groupIndex = 0;
        int groupNumber = 1;

        double increment = 1.0 / NUM_OF_INTERVAL;
        double threshold = increment;
        int size = predictedValues.length;
        int index = 0;
        while (index < size) {
            // if the data falls within current threshold (bin), sum up all the predictions and tally number of positive in the interval
            double predictedValueSum = 0;  // expected number of cases in the jth group (sum of predictions within the interval)
            int numOfData = 0;  // total number of data within the interval
            int numOfPosObserVal = 0;  // observed number of cases in the jth group
            if (predictedValues[index] < threshold || threshold > 0.9) {
                numOfData++;  // number of observations in the jth group
                predictedValueSum += predictedValues[index];
                if (observedValues[index] == 1) {
                    numOfPosObserVal++;
                }

                // continue to tally the data that falls in the current threshold (bin)
                index++;
                for (int j = index; j < predictedValues.length && (predictedValues[j] < threshold || threshold > 0.9); j++) {
                    numOfData++;  // number of observations in the jth group
                    predictedValueSum += predictedValues[index];
                    if (observedValues[index] == 1) {
                        numOfPosObserVal++;
                    }

                    index++;
                }
            }

            // compute chart points if there is data in the bin
            if (numOfData > 0) {
                // average of the predicted values within the interval
                double xValue = predictedValueSum / numOfData;

                // # positive divided by (# positive + # negative) with in the interval
                // use Laplace smoothing to ensure posterior probabilities are never zero
                double yValue = ((double) (numOfPosObserVal + 1)) / (numOfData + 2);

                hlExpectedValues[groupIndex] = xValue;
                hlObservedValues[groupIndex] = yValue;
                groups[groupIndex] = groupNumber;
                numberOfDataPerGroup[groupIndex] = numOfData;
                positiveObservedSumPerGroup[groupIndex] = numOfPosObserVal;
                predictedSumPerGroup[groupIndex] = predictedValueSum;
                marginOfErrorPerGroup[groupIndex] = CRITICAL_VALUE * Math.sqrt((yValue * (1 - yValue)) / numOfData);

                double averagePredictedValues = predictedValueSum / numOfData;
                if (averagePredictedValues == 1.0) {
                    averagePredictedValues = 0.99;
                }
                hlChi2PerGroup[groupIndex] = Math.pow(numOfPosObserVal - predictedValueSum, 2) / (predictedValueSum * (1 - averagePredictedValues));

                groupIndex++;
            }

            groupNumber++;

            // prevents threshold from going over 1.0
            if (threshold < 0.9) {
                threshold += increment;
            }
        }
    }

    /**
     * Split groups into intervals and count how many of groups has at least one
     * member.
     *
     * @return number of groups with at least one member
     */
    @Override
    protected int computeTotalNumberOfGroups() {
        int numOfGroups = 0;

        double increment = 0.1;
        double threshold = increment;
        int size = predictedValues.length;
        int index = 0;
        while (index < size) {
            // check if the data falls below current threshold (bin)
            // or if predicted value is between 0.9 and 1.0, inclusive
            if (predictedValues[index] < threshold || threshold > 0.9) {
                numOfGroups++;

                // skip all the data that already falls in the current threshold (bin)
                index++;
                for (int j = index; (j < predictedValues.length) && (predictedValues[j] < threshold); j++) {
                    index++;
                }
            }

            // prevents threshold from going over 1.0
            if (threshold < 0.9) {
                threshold += increment;
            }
        }

        return numOfGroups;
    }

}
