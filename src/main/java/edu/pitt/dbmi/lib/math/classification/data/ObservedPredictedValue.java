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
package edu.pitt.dbmi.lib.math.classification.data;

/**
 *
 * Mar 6, 2023 1:31:03 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class ObservedPredictedValue implements Comparable<ObservedPredictedValue> {

    private final int observedValue;

    private final double predictedValue;

    public ObservedPredictedValue(int observedValue, double predictedValue) {
        this.observedValue = observedValue;
        this.predictedValue = predictedValue;
    }

    @Override
    public int compareTo(ObservedPredictedValue otherObservedPredictedValue) {
        if (this.predictedValue > otherObservedPredictedValue.predictedValue) {
            return 1;
        } else if (this.predictedValue < otherObservedPredictedValue.predictedValue) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return predictedValue > 0
                ? String.format("(%d, %f)", this.observedValue, this.predictedValue)
                : String.format("(%d, 0)", this.observedValue);
    }

    public int getObservedValue() {
        return observedValue;
    }

    public double getPredictedValue() {
        return predictedValue;
    }

}
