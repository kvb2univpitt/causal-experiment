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
 * Hosmer-Lemeshow calibration chart binning by decile, splitting the dataset
 * into 10 equal parts.
 *
 * Mar 30, 2012 11:43:49 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class HosmerLemeshowDecileGroup extends AbstractHosmerLemeshow {

    public HosmerLemeshowDecileGroup(List<ObservedPredictedValue> observedPredictedValues) {
        super(observedPredictedValues);
    }

    @Override
    protected void computePlotPoints() {
        int groupIndex = 0;
        int groupNumber = 1;

        double percent = 0.10;
        int size = predictedValues.length;
        int index = 0;
        while (index < size) {
            // compute the threshold
            int intUpperIndex = (int) (percent * size);
            double threshold = (intUpperIndex < size)
                    ? predictedValues[intUpperIndex]
                    : predictedValues[size - 1];

            // if the data falls within current threshold (bin), sum up all the predictions and tally number of positive
            double predictedValueSum = 0.0;  // sum of predicted values within the interval
            int numOfData = 0;  // total number of data within the interval
            int numOfPosObserVal = 0;
            if (predictedValues[index] <= threshold) {
                numOfData++;  // number of observations in the jth group
                predictedValueSum += predictedValues[index];
                if (observedValues[index] == 1) {
                    numOfPosObserVal++;
                }

                // continue to tally the data that falls in the current threshold (bin)
                index++;
                for (int j = index; j < predictedValues.length && (predictedValues[j] <= threshold); j++) {
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
                double xValue = predictedValueSum / numOfData;  // average of the predicted values within the interval
                double yValue = ((double) numOfPosObserVal) / numOfData;  // # positive divided by (# positive + # negative) with in the interval

                hlExpectedValues[groupIndex] = xValue;
                hlObservedValues[groupIndex] = yValue;
                groups[groupIndex] = groupNumber;
                numberOfDataPerGroup[groupIndex] = numOfData;
                positiveObservedSumPerGroup[groupIndex] = numOfPosObserVal;
                predictedSumPerGroup[groupIndex] = predictedValueSum;
                marginOfErrorPerGroup[groupIndex] = CRITICAL_VALUE * Math.sqrt((xValue * (1 - xValue)) / numOfData);
                hlChi2PerGroup[groupIndex] = Math.pow(numOfPosObserVal - predictedValueSum, 2) / (predictedValueSum * (1 - (predictedValueSum / numOfData)));

                groupIndex++;
            }

            groupNumber++;
            percent += 0.10;
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

        double percent = 0.10d;
        int size = predictedValues.length;
        int index = 0;
        while (index < size) {
            // compute the threshold
            int intUpperIndex = (int) (percent * size);
            double threshold = (intUpperIndex < size)
                    ? predictedValues[intUpperIndex]
                    : predictedValues[size - 1];

            // check if the data falls within current threshold (bin)
            if (predictedValues[index] <= threshold) {
                numOfGroups++;

                // skip all the data that already falls in the current threshold (bin)
                index++;
                for (int j = index; j < predictedValues.length && (predictedValues[j] <= threshold); j++) {
                    index++;
                }
            }

            percent += 0.10;
        }

        return numOfGroups;
    }

}
